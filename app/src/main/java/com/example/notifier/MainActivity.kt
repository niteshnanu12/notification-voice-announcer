package com.example.notifier

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notifier.databinding.ActivityMainBinding
import com.example.notifier.databinding.ItemAppBinding
import java.util.concurrent.Executors

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable,
    var isWhitelisted: Boolean
)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsRepository: SettingsRepository
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsRepository = SettingsRepository(applicationContext)

        setupToolbar()
        setupPermissionButton()
        setupSettingsToggles()
        setupQuietHoursClick()
        
        binding.rvAppList.layoutManager = LinearLayoutManager(this)
        loadInstalledApps()
    }

    override fun onResume() {
        super.onResume()
        updatePermissionStatus()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupPermissionButton() {
        binding.btnGrantPermission.setOnClickListener {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
        }
    }

    private fun updatePermissionStatus() {
        val enabled = NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)
        if (enabled) {
            binding.permissionStatusText.text = getString(R.string.permission_enabled)
            binding.permissionStatusText.setTextColor(ContextCompat.getColor(this, R.color.success))
            binding.btnGrantPermission.visibility = View.GONE
        } else {
            binding.permissionStatusText.text = getString(R.string.permission_disabled)
            binding.permissionStatusText.setTextColor(ContextCompat.getColor(this, R.color.error))
            binding.btnGrantPermission.visibility = View.VISIBLE
        }
    }

    private fun setupSettingsToggles() {
        // Quiet Hours Toggle
        binding.switchQuietHours.isChecked = settingsRepository.isQuietHoursEnabled
        binding.switchQuietHours.setOnCheckedChangeListener { _, isChecked ->
            settingsRepository.isQuietHoursEnabled = isChecked
        }
        updateQuietHoursSummary()

        // Screen Off Only Toggle
        binding.switchScreenOff.isChecked = settingsRepository.isScreenOffOnlyEnabled
        binding.switchScreenOff.setOnCheckedChangeListener { _, isChecked ->
            settingsRepository.isScreenOffOnlyEnabled = isChecked
        }

        // Bluetooth/Headset Only Toggle
        binding.switchBluetooth.isChecked = settingsRepository.isBluetoothOnlyEnabled
        binding.switchBluetooth.setOnCheckedChangeListener { _, isChecked ->
            settingsRepository.isBluetoothOnlyEnabled = isChecked
        }
    }

    private fun setupQuietHoursClick() {
        binding.layoutQuietHours.setOnClickListener {
            val currentStart = settingsRepository.quietHoursStart
            val parts = currentStart.split(":")
            val startHour = parts.getOrNull(0)?.toIntOrNull() ?: 22
            val startMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0

            val startPicker = android.app.TimePickerDialog(this, { _, sHour, sMinute ->
                val startTimeStr = String.format("%02d:%02d", sHour, sMinute)

                val currentEnd = settingsRepository.quietHoursEnd
                val endParts = currentEnd.split(":")
                val endHour = endParts.getOrNull(0)?.toIntOrNull() ?: 7
                val endMinute = endParts.getOrNull(1)?.toIntOrNull() ?: 0

                val endPicker = android.app.TimePickerDialog(this, { _, eHour, eMinute ->
                    val endTimeStr = String.format("%02d:%02d", eHour, eMinute)
                    
                    settingsRepository.quietHoursStart = startTimeStr
                    settingsRepository.quietHoursEnd = endTimeStr
                    updateQuietHoursSummary()
                }, endHour, endMinute, false)
                endPicker.setTitle("Select Quiet Hours End Time")
                endPicker.show()

            }, startHour, startMinute, false)
            startPicker.setTitle("Select Quiet Hours Start Time")
            startPicker.show()
        }
    }

    private fun updateQuietHoursSummary() {
        val startFormatted = formatTime12Hr(settingsRepository.quietHoursStart)
        val endFormatted = formatTime12Hr(settingsRepository.quietHoursEnd)
        binding.txtQuietHoursSummary.text = "Mute all voice announcements between $startFormatted and $endFormatted"
    }

    private fun formatTime12Hr(timeStr: String): String {
        return try {
            val parts = timeStr.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()
            val amPm = if (hour >= 12) "PM" else "AM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            String.format("%d:%02d %s", displayHour, minute, amPm)
        } catch (e: Exception) {
            timeStr
        }
    }

    private fun loadInstalledApps() {
        executor.execute {
            val pm = packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(mainIntent, 0)

            val apps = mutableListOf<AppInfo>()
            val seenPackages = mutableSetOf<String>()
            val whitelisted = settingsRepository.getWhitelistedPackages()

            for (resolveInfo in resolveInfos) {
                val packageName = resolveInfo.activityInfo.packageName
                if (seenPackages.contains(packageName)) continue
                seenPackages.add(packageName)

                val label = resolveInfo.loadLabel(pm).toString()
                val icon = try {
                    pm.getApplicationIcon(packageName)
                } catch (e: PackageManager.NameNotFoundException) {
                    pm.defaultActivityIcon
                }

                apps.add(AppInfo(
                    label = label,
                    packageName = packageName,
                    icon = icon,
                    isWhitelisted = whitelisted.contains(packageName)
                ))
            }

            apps.sortBy { it.label.lowercase() }

            mainHandler.post {
                setupRecyclerView(apps)
            }
        }
    }

    private fun setupRecyclerView(apps: List<AppInfo>) {
        val adapter = AppListAdapter(apps) { pkg, isWhitelisted ->
            settingsRepository.setAppWhitelisted(pkg, isWhitelisted)
        }
        binding.rvAppList.adapter = adapter
    }
}

class AppListAdapter(
    private val apps: List<AppInfo>,
    private val onWhitelistChanged: (String, Boolean) -> Unit
) : RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        holder.binding.txtAppName.text = app.label
        holder.binding.txtPackageName.text = app.packageName
        holder.binding.imgAppIcon.setImageDrawable(app.icon)

        // Remove listener, check box, and re-add listener to avoid recycle triggers
        holder.binding.switchAnnounce.setOnCheckedChangeListener(null)
        holder.binding.switchAnnounce.isChecked = app.isWhitelisted
        holder.binding.switchAnnounce.setOnCheckedChangeListener { _, isChecked ->
            app.isWhitelisted = isChecked
            onWhitelistChanged(app.packageName, isChecked)
        }
    }

    override fun getItemCount(): Int = apps.size
}
