package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.NetworkRepository
import com.example.model.NetworkSettings
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateCardSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsModal(
    currentSettings: NetworkSettings,
    onDismiss: () -> Unit,
    onSave: (NetworkSettings) -> Unit
) {
    var esp32Ip by remember { mutableStateOf(currentSettings.esp32Ip) }
    var appsScriptUrl by remember { mutableStateOf(currentSettings.appsScriptUrl) }
    var googleSheetsUrl by remember { mutableStateOf(currentSettings.googleSheetsUrl) }
    var historySheetsUrl by remember { mutableStateOf(currentSettings.historySheetsUrl) }
    var demoMode by remember { mutableStateOf(currentSettings.demoMode) }
    var autoPolling by remember { mutableStateOf(currentSettings.autoPolling) }

    val isIpValid = NetworkRepository.isValidIpAddress(esp32Ip) || demoMode

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, NeonCyan, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SlateCardSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Router,
                        contentDescription = null,
                        tint = NeonCyan
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Configuración de Red & Endpoints",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Demo Mode Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Modo Demo / Simulación",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Permite probar la interfaz sin hardware físico conectado",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextSecondary
                        )
                    }

                    Switch(
                        checked = demoMode,
                        onCheckedChange = { demoMode = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = NeonCyan
                        ),
                        modifier = Modifier.testTag("demo_mode_switch")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ESP32 IP Input
                OutlinedTextField(
                    value = esp32Ip,
                    onValueChange = { esp32Ip = it },
                    label = { Text("Dirección IP Local del ESP32 S3") },
                    placeholder = { Text("Ej: 192.168.1.100") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Dns, contentDescription = null, tint = NeonCyan)
                    },
                    isError = !isIpValid && !demoMode,
                    supportingText = {
                        if (!isIpValid && !demoMode) {
                            Text("Formato de IP inválido (Ej. 192.168.1.100)", color = NeonRed)
                        } else {
                            Text("Usado para comandos /power?state=ON|OFF y /status", color = TextSecondary)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = SlateCardBorder,
                        focusedLabelColor = NeonCyan,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("esp32_ip_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Apps Script Web App URL
                OutlinedTextField(
                    value = appsScriptUrl,
                    onValueChange = { appsScriptUrl = it },
                    label = { Text("URL Google Apps Script (JSON Web App)") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = NeonCyan)
                    },
                    supportingText = {
                        Text("Retorna contadores JSON {total, verde, amarillo, rojo}", color = TextSecondary)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = SlateCardBorder,
                        focusedLabelColor = NeonCyan,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("apps_script_url_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Google Sheets Web URL for Conteo
                OutlinedTextField(
                    value = googleSheetsUrl,
                    onValueChange = { googleSheetsUrl = it },
                    label = { Text("URL Google Sheets (Conteo en Tiempo Real)") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.TableChart, contentDescription = null, tint = NeonGreen)
                    },
                    supportingText = {
                        Text("Lee A1=Fecha, B1=Hora, C1=Estado para actualizar contadores", color = TextSecondary)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = SlateCardBorder,
                        focusedLabelColor = NeonGreen,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sheets_url_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Google Sheets Web URL for Historial
                OutlinedTextField(
                    value = historySheetsUrl,
                    onValueChange = { historySheetsUrl = it },
                    label = { Text("URL Google Sheets (Historial Completo)") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.TableChart, contentDescription = null, tint = NeonCyan)
                    },
                    supportingText = {
                        Text("Abre la hoja de historial completo al pulsar el botón verde", color = TextSecondary)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = SlateCardBorder,
                        focusedLabelColor = NeonCyan,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("history_sheets_url_input")
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = TextSecondary)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (isIpValid || demoMode) {
                                onSave(
                                    currentSettings.copy(
                                        esp32Ip = esp32Ip.trim(),
                                        appsScriptUrl = appsScriptUrl.trim(),
                                        googleSheetsUrl = googleSheetsUrl.trim(),
                                        historySheetsUrl = historySheetsUrl.trim(),
                                        demoMode = demoMode,
                                        autoPolling = autoPolling
                                    )
                                )
                            }
                        },
                        enabled = isIpValid || demoMode,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.testTag("save_settings_button")
                    ) {
                        Text("Guardar Cambios", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
