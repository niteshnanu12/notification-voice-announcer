package com.example.notifier

import android.content.Context
import android.content.SharedPreferences

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "notification_announcer_prefs"
        private const val KEY_FIRST_LAUNCH = "pref_first_launch"
        private const val KEY_WHITELIST = "pref_whitelist"
        private const val KEY_QUIET_HOURS_ENABLED = "pref_quiet_hours_enabled"
        private const val KEY_QUIET_HOURS_START = "pref_quiet_hours_start"
        private const val KEY_QUIET_HOURS_END = "pref_quiet_hours_end"
        private const val KEY_SCREEN_OFF_ONLY = "pref_screen_off_only"
        private const val KEY_BLUETOOTH_ONLY = "pref_bluetooth_only"

        // Default packages to whitelist on first launch
        private val DEFAULT_WHITELIST = setOf(
            "com.whatsapp",
            "com.google.android.gm",
            "org.telegram.messenger",
            "org.telegram.plus",
            "com.google.android.apps.messaging",
            "com.android.mms"
        )
    }

    init {
        // Initialize default settings on first install
        if (prefs.getBoolean(KEY_FIRST_LAUNCH, true)) {
            prefs.edit()
                .putStringSet(KEY_WHITELIST, DEFAULT_WHITELIST)
                .putBoolean(KEY_FIRST_LAUNCH, false)
                .apply()
        }
    }

    fun getWhitelistedPackages(): Set<String> {
        return prefs.getStringSet(KEY_WHITELIST, emptySet()) ?: emptySet()
    }

    fun isAppWhitelisted(packageName: String): Boolean {
        return getWhitelistedPackages().contains(packageName)
    }

    fun setAppWhitelisted(packageName: String, whitelisted: Boolean) {
        val currentList = getWhitelistedPackages().toMutableSet()
        if (whitelisted) {
            currentList.add(packageName)
        } else {
            currentList.remove(packageName)
        }
        prefs.edit().putStringSet(KEY_WHITELIST, currentList).apply()
    }

    var isQuietHoursEnabled: Boolean
        get() = prefs.getBoolean(KEY_QUIET_HOURS_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_QUIET_HOURS_ENABLED, value).apply()

    var quietHoursStart: String
        get() = prefs.getString(KEY_QUIET_HOURS_START, "22:00") ?: "22:00"
        set(value) = prefs.edit().putString(KEY_QUIET_HOURS_START, value).apply()

    var quietHoursEnd: String
        get() = prefs.getString(KEY_QUIET_HOURS_END, "07:00") ?: "07:00"
        set(value) = prefs.edit().putString(KEY_QUIET_HOURS_END, value).apply()

    var isScreenOffOnlyEnabled: Boolean
        get() = prefs.getBoolean(KEY_SCREEN_OFF_ONLY, false)
        set(value) = prefs.edit().putBoolean(KEY_SCREEN_OFF_ONLY, value).apply()

    var isBluetoothOnlyEnabled: Boolean
        get() = prefs.getBoolean(KEY_BLUETOOTH_ONLY, false)
        set(value) = prefs.edit().putBoolean(KEY_BLUETOOTH_ONLY, value).apply()
}
