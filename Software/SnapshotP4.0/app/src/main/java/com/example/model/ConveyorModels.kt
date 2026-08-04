package com.example.model

enum class LogType {
    POWER,
    METRICS,
    SYSTEM,
    ERROR
}

data class LogEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: String,
    val type: LogType,
    val message: String
)

sealed class ConnectionState {
    object Online : ConnectionState()
    object Offline : ConnectionState()
    object Connecting : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}

data class Esp32PowerResponse(
    val status: String? = null,
    val systemActive: Boolean? = null,
    val message: String? = null
)

data class Esp32StatusResponse(
    val systemActive: Boolean = false
)

data class AppsScriptMetricsResponse(
    val total: Int = 0,
    val verde: Int = 0,
    val amarillo: Int = 0,
    val rojo: Int = 0,
    val timestamp: String? = null
)

data class SheetRowData(
    val fecha: String,
    val hora: String,
    val estado: String
)

data class NetworkSettings(
    val esp32Ip: String = "192.168.1.100",
    val appsScriptUrl: String = "https://script.google.com/macros/s/AKfycbyExampleScriptID/exec",
    val googleSheetsUrl: String = "https://docs.google.com/spreadsheets/d/1tG30khmOwEeP-12x6ZQ8v-A4Sv6noJ8RcLfHRq_Is-Y/edit?usp=sharing",
    val historySheetsUrl: String = "https://docs.google.com/spreadsheets/d/1_CtXY1mKRyKOgUN5AYglNRgF7QFYKvljy8McZxdDOl0/edit?usp=sharing",
    val demoMode: Boolean = false, // Set to false by default to use real Google Sheets data
    val autoPolling: Boolean = true,
    val pollingIntervalSeconds: Int = 3
)
