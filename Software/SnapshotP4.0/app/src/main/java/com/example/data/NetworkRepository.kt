package com.example.data

import com.example.model.AppsScriptMetricsResponse
import com.example.model.Esp32PowerResponse
import com.example.model.Esp32StatusResponse
import com.example.model.SheetRowData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class NetworkRepository {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .writeTimeout(3, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    companion object {
        private val IP_PATTERN = Pattern.compile(
            "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$"
        )

        fun extractSheetId(url: String): String? {
            val regex = Regex("spreadsheets/d/([a-zA-Z0-9-_]+)")
            val match = regex.find(url)
            return match?.groupValues?.get(1)
        }

        fun isValidIpAddress(ip: String): Boolean {
            val cleanIp = ip.trim().removePrefix("http://").removePrefix("https://").split(":")[0]
            return IP_PATTERN.matcher(cleanIp).matches()
        }

        fun sanitizeBaseUrl(ipOrUrl: String): String {
            var url = ipOrUrl.trim()
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://$url"
            }
            if (url.endsWith("/")) {
                url = url.substring(0, url.length - 1)
            }
            return url
        }
    }

    /**
     * Sends power command to ESP32: GET http://<IP_ESP32>/power?state=ON|OFF
     */
    suspend fun setEsp32Power(esp32Ip: String, stateOn: Boolean): Result<Esp32PowerResponse> = withContext(Dispatchers.IO) {
        try {
            val baseUrl = sanitizeBaseUrl(esp32Ip)
            val stateParam = if (stateOn) "ON" else "OFF"
            val targetUrl = "$baseUrl/power?state=$stateParam"

            val request = Request.Builder()
                .url(targetUrl)
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("ESP32 error HTTP ${response.code}"))
                }
                val bodyStr = response.body?.string() ?: ""
                val json = JSONObject(bodyStr)
                val status = json.optString("status", "unknown")
                val systemActive = json.optBoolean("systemActive", stateOn)
                val msg = json.optString("message", null)

                Result.success(Esp32PowerResponse(status = status, systemActive = systemActive, message = msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Checks status from ESP32: GET http://<IP_ESP32>/status
     */
    suspend fun getEsp32Status(esp32Ip: String): Result<Esp32StatusResponse> = withContext(Dispatchers.IO) {
        try {
            val baseUrl = sanitizeBaseUrl(esp32Ip)
            val targetUrl = "$baseUrl/status"

            val request = Request.Builder()
                .url(targetUrl)
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("ESP32 Status HTTP ${response.code}"))
                }
                val bodyStr = response.body?.string() ?: ""
                val json = JSONObject(bodyStr)
                val systemActive = json.optBoolean("systemActive", false)

                Result.success(Esp32StatusResponse(systemActive = systemActive))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetches real-time counts from Google Apps Script endpoint:
     * returns {"total": X, "verde": Y, "amarillo": Z, "rojo": W, "timestamp": "..."}
     */
    suspend fun fetchMetrics(appsScriptUrl: String): Result<AppsScriptMetricsResponse> = withContext(Dispatchers.IO) {
        try {
            val url = appsScriptUrl.trim()
            if (url.isEmpty() || !url.startsWith("http")) {
                return@withContext Result.failure(Exception("URL de Apps Script inválida"))
            }

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("Google Apps Script HTTP ${response.code}"))
                }
                val bodyStr = response.body?.string() ?: ""
                val json = JSONObject(bodyStr)

                val total = json.optInt("total", 0)
                val verde = json.optInt("verde", 0)
                val amarillo = json.optInt("amarillo", 0)
                val rojo = json.optInt("rojo", 0)
                val timestamp = json.optString("timestamp", null)

                Result.success(
                    AppsScriptMetricsResponse(
                        total = total,
                        verde = verde,
                        amarillo = amarillo,
                        rojo = rojo,
                        timestamp = timestamp
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Direct Google Sheets CSV Fetcher:
     * Reads A1=Fecha, B1=Hora, C1=Estado directly from Google Sheets CSV export
     */
    suspend fun fetchSheetDataDirect(googleSheetsUrl: String): Result<List<SheetRowData>> = withContext(Dispatchers.IO) {
        try {
            val sheetId = extractSheetId(googleSheetsUrl)
                ?: return@withContext Result.failure(Exception("ID de Google Sheet no encontrado en URL"))

            val csvUrl = "https://docs.google.com/spreadsheets/d/$sheetId/export?format=csv"

            val request = Request.Builder()
                .url(csvUrl)
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("Error al leer Google Sheets CSV (HTTP ${response.code})"))
                }

                val csvContent = response.body?.string() ?: ""
                val rows = parseCsvRows(csvContent)
                Result.success(rows)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseCsvRows(csvContent: String): List<SheetRowData> {
        val result = mutableListOf<SheetRowData>()
        val lines = csvContent.lines()
        if (lines.size <= 1) return result

        for (i in 1 until lines.size) {
            val line = lines[i].trim()
            if (line.isEmpty()) continue

            val columns = parseCsvLine(line)
            if (columns.size >= 3) {
                val fecha = columns[0].replace("\"", "").trim()
                val hora = columns[1].replace("\"", "").trim()
                val estado = columns[2].replace("\"", "").trim().lowercase()

                if (fecha.isNotEmpty() && estado.isNotEmpty()) {
                    result.add(SheetRowData(fecha = fecha, hora = hora, estado = estado))
                }
            }
        }
        return result
    }

    private fun parseCsvLine(line: String): List<String> {
        val tokens = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false

        for (ch in line) {
            when (ch) {
                '"' -> inQuotes = !inQuotes
                ',' -> {
                    if (inQuotes) {
                        sb.append(ch)
                    } else {
                        tokens.add(sb.toString())
                        sb.setLength(0)
                    }
                }
                else -> sb.append(ch)
            }
        }
        tokens.add(sb.toString())
        return tokens
    }
}
