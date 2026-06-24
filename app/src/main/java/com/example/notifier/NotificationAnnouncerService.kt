package com.example.notifier

import android.app.Notification
import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.os.PowerManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import java.util.Calendar

class NotificationAnnouncerService : NotificationListenerService() {

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var speechManager: SpeechManager

    private val recentNotifications = LinkedHashMap<String, Long>()

    companion object {
        private const val TAG = "AnnouncerService"
        private const val DUPLICATE_INTERVAL_MS = 5000L
        private const val MAX_CACHE_SIZE = 100
    }

    override fun onCreate() {
        super.onCreate()
        settingsRepository = SettingsRepository(applicationContext)
        speechManager = SpeechManager(applicationContext)
        Log.d(TAG, "NotificationAnnouncerService Created")
    }

    override fun onDestroy() {
        speechManager.shutdown()
        super.onDestroy()
        Log.d(TAG, "NotificationAnnouncerService Destroyed")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        val packageName = sbn.packageName
        if (packageName == applicationContext.packageName) {
            // Do not read notifications from this app itself to avoid infinite feedback loops
            return
        }

        // 1. App Whitelist Check
        if (!settingsRepository.isAppWhitelisted(packageName)) {
            Log.d(TAG, "Skipping notification from non-whitelisted app: $packageName")
            return
        }

        // 2. Duplicate Suppression (ignore same notification within 5 seconds)
        val key = sbn.key
        val now = System.currentTimeMillis()
        val lastTime = recentNotifications[key]
        if (lastTime != null && (now - lastTime) < DUPLICATE_INTERVAL_MS) {
            Log.d(TAG, "Suppressed duplicate notification for key: $key")
            return
        }
        recentNotifications[key] = now
        // Clean up cache
        if (recentNotifications.size > MAX_CACHE_SIZE) {
            val eldestKey = recentNotifications.keys.first()
            recentNotifications.remove(eldestKey)
        }

        // 3. Screen-Off Mode Check
        if (settingsRepository.isScreenOffOnlyEnabled) {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (powerManager.isInteractive) {
                Log.d(TAG, "Skipping notification: Screen is on and Screen-Off Only is enabled")
                return
            }
        }

        // 4. Bluetooth/Headset Only Check
        if (settingsRepository.isBluetoothOnlyEnabled) {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val isHeadsetConnected = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
                devices.any { device ->
                    device.type == AudioDeviceInfo.TYPE_WIRED_HEADSET ||
                    device.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
                    device.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                    device.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
                }
            } else {
                @Suppress("DEPRECATION")
                audioManager.isWiredHeadsetOn || @Suppress("DEPRECATION") audioManager.isBluetoothA2dpOn
            }
            if (!isHeadsetConnected) {
                Log.d(TAG, "Skipping notification: Headset/Bluetooth is not connected and Headset Only is enabled")
                return
            }
        }

        // 5. Quiet Hours Check
        if (settingsRepository.isQuietHoursEnabled) {
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)
            val currentMinutes = currentHour * 60 + currentMinute

            val (startHour, startMinute) = parseTime(settingsRepository.quietHoursStart)
            val (endHour, endMinute) = parseTime(settingsRepository.quietHoursEnd)

            val startMinutes = startHour * 60 + startMinute
            val endMinutes = endHour * 60 + endMinute

            val inQuietHours = if (startMinutes <= endMinutes) {
                currentMinutes in startMinutes until endMinutes
            } else {
                // Crosses midnight, e.g. 22:00 to 07:00
                currentMinutes >= startMinutes || currentMinutes < endMinutes
            }

            if (inQuietHours) {
                Log.d(TAG, "Skipping notification: Current time is within Quiet Hours")
                return
            }
        }

        // 6. Speak the notification
        val announcement = NotificationParser.parse(applicationContext, sbn) ?: return
        speechManager.speak(announcement)
    }

    private fun parseTime(timeStr: String): Pair<Int, Int> {
        return try {
            val parts = timeStr.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()
            Pair(hour, minute)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing time: $timeStr, fallback to 00:00", e)
            Pair(0, 0)
        }
    }
}
