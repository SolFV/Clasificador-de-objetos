package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.NetworkSettings

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("conveyor_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ESP32_IP = "esp32_ip"
        private const val KEY_APPS_SCRIPT_URL = "apps_script_url"
        private const val KEY_SHEETS_URL = "sheets_url"
        private const val KEY_HISTORY_SHEETS_URL = "history_sheets_url"
        private const val KEY_DEMO_MODE = "demo_mode"
        private const val KEY_AUTO_POLLING = "auto_polling"
        private const val KEY_POLLING_INTERVAL = "polling_interval"

        const val DEFAULT_IP = "192.168.1.100"
        const val DEFAULT_APPS_SCRIPT_URL = "https://script.google.com/macros/s/AKfycbyExampleScriptID/exec"
        const val DEFAULT_SHEETS_URL = "https://docs.google.com/spreadsheets/d/1tG30khmOwEeP-12x6ZQ8v-A4Sv6noJ8RcLfHRq_Is-Y/edit?usp=sharing"
        const val DEFAULT_HISTORY_SHEETS_URL = "https://docs.google.com/spreadsheets/d/1_CtXY1mKRyKOgUN5AYglNRgF7QFYKvljy8McZxdDOl0/edit?usp=sharing"
    }

    fun getSettings(): NetworkSettings {
        return NetworkSettings(
            esp32Ip = prefs.getString(KEY_ESP32_IP, DEFAULT_IP) ?: DEFAULT_IP,
            appsScriptUrl = prefs.getString(KEY_APPS_SCRIPT_URL, DEFAULT_APPS_SCRIPT_URL) ?: DEFAULT_APPS_SCRIPT_URL,
            googleSheetsUrl = prefs.getString(KEY_SHEETS_URL, DEFAULT_SHEETS_URL) ?: DEFAULT_SHEETS_URL,
            historySheetsUrl = prefs.getString(KEY_HISTORY_SHEETS_URL, DEFAULT_HISTORY_SHEETS_URL) ?: DEFAULT_HISTORY_SHEETS_URL,
            demoMode = prefs.getBoolean(KEY_DEMO_MODE, false),
            autoPolling = prefs.getBoolean(KEY_AUTO_POLLING, true),
            pollingIntervalSeconds = prefs.getInt(KEY_POLLING_INTERVAL, 3)
        )
    }

    fun saveSettings(settings: NetworkSettings) {
        prefs.edit()
            .putString(KEY_ESP32_IP, settings.esp32Ip.trim())
            .putString(KEY_APPS_SCRIPT_URL, settings.appsScriptUrl.trim())
            .putString(KEY_SHEETS_URL, settings.googleSheetsUrl.trim())
            .putString(KEY_HISTORY_SHEETS_URL, settings.historySheetsUrl.trim())
            .putBoolean(KEY_DEMO_MODE, settings.demoMode)
            .putBoolean(KEY_AUTO_POLLING, settings.autoPolling)
            .putInt(KEY_POLLING_INTERVAL, settings.pollingIntervalSeconds)
            .apply()
    }
}
