package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.NetworkRepository
import com.example.data.SettingsRepository
import com.example.model.AppsScriptMetricsResponse
import com.example.model.ConnectionState
import com.example.model.LogEntry
import com.example.model.LogType
import com.example.model.NetworkSettings
import com.example.model.SheetRowData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class ConveyorViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepo = SettingsRepository(application)
    private val networkRepo = NetworkRepository()

    private val _networkSettings = MutableStateFlow(settingsRepo.getSettings())
    val networkSettings: StateFlow<NetworkSettings> = _networkSettings.asStateFlow()

    private val _systemActive = MutableStateFlow(true)
    val systemActive: StateFlow<Boolean> = _systemActive.asStateFlow()

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Connecting)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _metrics = MutableStateFlow(
        AppsScriptMetricsResponse(
            total = 42,
            verde = 32,
            amarillo = 6,
            rojo = 4,
            timestamp = getCurrentFormattedTime()
        )
    )
    val metrics: StateFlow<AppsScriptMetricsResponse> = _metrics.asStateFlow()

    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()

    private val _isPowerLoading = MutableStateFlow(false)
    val isPowerLoading: StateFlow<Boolean> = _isPowerLoading.asStateFlow()

    private val _isRefreshingMetrics = MutableStateFlow(false)
    val isRefreshingMetrics: StateFlow<Boolean> = _isRefreshingMetrics.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _showResetDialog = MutableStateFlow(false)
    val showResetDialog: StateFlow<Boolean> = _showResetDialog.asStateFlow()

    private var pollingJob: Job? = null
    private var lastResetTime: String? = null
    private var lastKnownRowCountToday: Int = 0

    init {
        addLog(LogType.SYSTEM, "Aplicación iniciada. Configuración de red cargada.")
        if (_networkSettings.value.demoMode) {
            _connectionState.value = ConnectionState.Online
            addLog(LogType.SYSTEM, "Modo Demo de Simulación Activado (Interactivo).")
        }
        startPolling()
    }

    private fun getCurrentFormattedTime(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun addLog(type: LogType, message: String) {
        val entry = LogEntry(
            timestamp = getCurrentFormattedTime(),
            type = type,
            message = message
        )
        _logs.update { current ->
            (listOf(entry) + current).take(50) // keep last 50 entries
        }
    }

    fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                if (_networkSettings.value.autoPolling) {
                    pollData()
                }
                delay(_networkSettings.value.pollingIntervalSeconds * 1000L)
            }
        }
    }

    private suspend fun pollData() {
        val settings = _networkSettings.value

        if (settings.demoMode) {
            // Simulated live data updates in demo mode
            _connectionState.value = ConnectionState.Online
            if (_systemActive.value && Random.nextFloat() < 0.35f) {
                val outcome = Random.nextInt(100)
                _metrics.update { curr ->
                    when {
                        outcome < 70 -> curr.copy(
                            total = curr.total + 1,
                            verde = curr.verde + 1,
                            timestamp = getCurrentFormattedTime()
                        )
                        outcome < 88 -> curr.copy(
                            total = curr.total + 1,
                            amarillo = curr.amarillo + 1,
                            timestamp = getCurrentFormattedTime()
                        )
                        else -> curr.copy(
                            total = curr.total + 1,
                            rojo = curr.rojo + 1,
                            timestamp = getCurrentFormattedTime()
                        )
                    }
                }
                val lastMetric = _metrics.value
                val statusText = if (outcome < 70) "VERDE (Buen estado)" else if (outcome < 88) "AMARILLO (Indeterminado)" else "ROJO (Mal estado)"
                addLog(LogType.METRICS, "Caja clasificada: $statusText. Total actual: ${lastMetric.total}")
            }
            return
        }

        // REAL HTTP NETWORK CALLS
        _isRefreshingMetrics.value = true

        // 1. Check ESP32 Status
        val espStatusResult = networkRepo.getEsp32Status(settings.esp32Ip)
        espStatusResult.onSuccess { resp ->
            _connectionState.value = ConnectionState.Online
            _systemActive.value = resp.systemActive
        }.onFailure { err ->
            _connectionState.value = ConnectionState.Offline
            addLog(LogType.ERROR, "Timeout/Error con ESP32 (${settings.esp32Ip}): ${err.localizedMessage}")
        }

        // 2. Fetch and Map Google Sheets Rows directly (A1=Fecha, B1=Hora, C1=Estado)
        val sheetResult = networkRepo.fetchSheetDataDirect(settings.googleSheetsUrl)
        sheetResult.onSuccess { allRows ->
            processSheetRowsForToday(allRows)
        }.onFailure { sheetErr ->
            // Fallback to Apps Script endpoint if direct CSV is unavailable
            val metricsResult = networkRepo.fetchMetrics(settings.appsScriptUrl)
            metricsResult.onSuccess { data ->
                _metrics.value = data.copy(timestamp = getCurrentFormattedTime())
            }.onFailure { err ->
                addLog(LogType.ERROR, "Error al consultar Google Sheets: ${sheetErr.localizedMessage}")
            }
        }

        _isRefreshingMetrics.value = false
    }

    private fun processSheetRowsForToday(allRows: List<SheetRowData>) {
        val todayRows = allRows.filter { isTodayDate(it.fecha) }

        val cutoff = lastResetTime
        val validRows = if (cutoff != null) {
            todayRows.filter { it.hora > cutoff }
        } else {
            todayRows
        }

        var verdeCount = 0
        var amarilloCount = 0
        var rojoCount = 0

        for (row in validRows) {
            when (row.estado.lowercase().trim()) {
                "verde" -> verdeCount++
                "amarillo" -> amarilloCount++
                "rojo" -> rojoCount++
            }
        }

        val newTotal = verdeCount + amarilloCount + rojoCount

        if (newTotal > _metrics.value.total) {
            val lastRow = validRows.lastOrNull()
            val statusText = lastRow?.estado?.uppercase() ?: "NUEVO REGISTRO"
            addLog(LogType.METRICS, "Nuevo dato en Sheet ($statusText) a las ${lastRow?.hora ?: getCurrentFormattedTime()}")
        }

        _metrics.value = AppsScriptMetricsResponse(
            total = newTotal,
            verde = verdeCount,
            amarillo = amarilloCount,
            rojo = rojoCount,
            timestamp = getCurrentFormattedTime()
        )
        lastKnownRowCountToday = validRows.size
    }

    private fun isTodayDate(rowDateStr: String): Boolean {
        if (rowDateStr.isBlank() || rowDateStr.equals("fecha", ignoreCase = true)) return false

        val clean = rowDateStr.trim().replace(".", "/").replace("-", "/")
        val parts = clean.split("/")
        val todayCal = Calendar.getInstance()
        val todayDay = todayCal.get(Calendar.DAY_OF_MONTH)
        val todayMonth = todayCal.get(Calendar.MONTH) + 1
        val todayYear = todayCal.get(Calendar.YEAR)

        if (parts.size == 3) {
            val p0 = parts[0].toIntOrNull() ?: 0
            val p1 = parts[1].toIntOrNull() ?: 0
            var p2 = parts[2].toIntOrNull() ?: 0

            if (p2 in 1..99) {
                p2 += 2000
            }

            if (p2 == todayYear) {
                val matchDDMM = (p0 == todayDay && p1 == todayMonth)
                val matchMMDD = (p0 == todayMonth && p1 == todayDay)
                if (matchDDMM || matchMMDD) return true
            }

            if (p0 == todayYear) {
                val matchDDMM = (p2 == todayDay && p1 == todayMonth)
                val matchMMDD = (p2 == todayMonth && p1 == todayDay)
                if (matchDDMM || matchMMDD) return true
            }
        }

        val sdf1 = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val sdf3 = SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(Date())
        return clean.equals(sdf1, ignoreCase = true) ||
               clean.equals(sdf2, ignoreCase = true) ||
               clean.equals(sdf3, ignoreCase = true)
    }

    fun togglePower() {
        viewModelScope.launch {
            val currentState = _systemActive.value
            val targetState = !currentState
            val settings = _networkSettings.value

            _isPowerLoading.value = true
            addLog(LogType.POWER, "Enviando comando de energía: ${if (targetState) "ON" else "OFF"}...")

            if (settings.demoMode) {
                delay(400) // Small visual feedback delay
                _systemActive.value = targetState
                _isPowerLoading.value = false
                val label = if (targetState) "ACTIVADO (ON)" else "DESACTIVADO (OFF)"
                addLog(LogType.POWER, "Sistema $label correctamente (Simulación).")
                _userMessage.value = "Sistema $label (Modo Demo)"
                return@launch
            }

            // Real HTTP call GET /power?state=ON|OFF
            val result = networkRepo.setEsp32Power(settings.esp32Ip, targetState)
            _isPowerLoading.value = false

            result.onSuccess { resp ->
                val newActive = resp.systemActive ?: targetState
                _systemActive.value = newActive
                _connectionState.value = ConnectionState.Online
                val label = if (newActive) "ACTIVADO (ON)" else "DESACTIVADO (OFF)"
                addLog(LogType.POWER, "Comando ESP32 exitoso: Sistema $label.")
                _userMessage.value = "ESP32 S3 $label"
            }.onFailure { err ->
                _connectionState.value = ConnectionState.Offline
                val errMsg = "Error al enviar comando al ESP32 (${settings.esp32Ip}): ${err.localizedMessage}"
                addLog(LogType.ERROR, errMsg)
                _userMessage.value = "No se pudo comunicar con el ESP32 S3"
            }
        }
    }

    fun refreshMetricsManual() {
        viewModelScope.launch {
            _isRefreshingMetrics.value = true
            addLog(LogType.SYSTEM, "Refresco manual de métricas iniciado...")
            pollData()
            _isRefreshingMetrics.value = false
            _userMessage.value = "Contadores actualizados"
        }
    }

    fun openSettingsDialog() {
        _showSettingsDialog.value = true
    }

    fun dismissSettingsDialog() {
        _showSettingsDialog.value = false
    }

    fun openResetDialog() {
        _showResetDialog.value = true
    }

    fun dismissResetDialog() {
        _showResetDialog.value = false
    }

    fun confirmResetTodayCounters() {
        lastResetTime = getCurrentFormattedTime()
        lastKnownRowCountToday = 0
        _metrics.value = AppsScriptMetricsResponse(
            total = 0,
            verde = 0,
            amarillo = 0,
            rojo = 0,
            timestamp = getCurrentFormattedTime()
        )
        _showResetDialog.value = false
        addLog(LogType.SYSTEM, "RESETEO DEL DÍA: Contadores de cajas reiniciados a cero (0) a las $lastResetTime.")
        _userMessage.value = "Contadores del día reiniciados a cero"
    }

    fun updateSettings(newSettings: NetworkSettings) {
        settingsRepo.saveSettings(newSettings)
        _networkSettings.value = newSettings
        _showSettingsDialog.value = false
        addLog(LogType.SYSTEM, "Configuración de red actualizada. IP ESP32: ${newSettings.esp32Ip}")
        _userMessage.value = "Configuración guardada"

        if (newSettings.demoMode) {
            _connectionState.value = ConnectionState.Online
        } else {
            _connectionState.value = ConnectionState.Connecting
        }
        startPolling()
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }
}
