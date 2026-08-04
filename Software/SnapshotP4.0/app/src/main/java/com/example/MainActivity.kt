package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ActivityLogSection
import com.example.ui.components.HeaderSection
import com.example.ui.components.MetricsGrid
import com.example.ui.components.PowerControlCard
import com.example.ui.components.QuickActionsRow
import com.example.ui.components.ResetConfirmDialog
import com.example.ui.components.SettingsModal
import com.example.ui.theme.ConveyorTheme
import com.example.ui.theme.SlateDarkBackground
import com.example.ui.viewmodel.ConveyorViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConveyorTheme {
                ConveyorApp()
            }
        }
    }
}

@Composable
fun ConveyorApp(
    viewModel: ConveyorViewModel = viewModel()
) {
    val systemActive by viewModel.systemActive.collectAsStateWithLifecycle()
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val metrics by viewModel.metrics.collectAsStateWithLifecycle()
    val networkSettings by viewModel.networkSettings.collectAsStateWithLifecycle()
    val logs by viewModel.logs.collectAsStateWithLifecycle()
    val isPowerLoading by viewModel.isPowerLoading.collectAsStateWithLifecycle()
    val isRefreshingMetrics by viewModel.isRefreshingMetrics.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsStateWithLifecycle()
    val showResetDialog by viewModel.showResetDialog.collectAsStateWithLifecycle()

    var showSplashScreen by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(1800)
        showSplashScreen = false
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = SlateDarkBackground
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SlateDarkBackground)
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header with status indicator and settings button
                    HeaderSection(
                        connectionState = connectionState,
                        isDemoMode = networkSettings.demoMode,
                        onOpenSettings = { viewModel.openSettingsDialog() }
                    )

                    // Industrial Power Control Switch ON/OFF
                    PowerControlCard(
                        systemActive = systemActive,
                        isLoading = isPowerLoading,
                        esp32Ip = networkSettings.esp32Ip,
                        onTogglePower = { viewModel.togglePower() }
                    )

                    // Metrics Grid 2x2
                    MetricsGrid(
                        metrics = metrics
                    )

                    // Google Sheets Action Button & Refresh
                    QuickActionsRow(
                        sheetsUrl = networkSettings.historySheetsUrl,
                        isRefreshing = isRefreshingMetrics,
                        onRefresh = { viewModel.refreshMetricsManual() },
                        onRequestReset = { viewModel.openResetDialog() }
                    )

                    // Live System Audit Activity Log
                    ActivityLogSection(
                        logs = logs
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Settings Dialog Modal
                if (showSettingsDialog) {
                    SettingsModal(
                        currentSettings = networkSettings,
                        onDismiss = { viewModel.dismissSettingsDialog() },
                        onSave = { updated -> viewModel.updateSettings(updated) }
                    )
                }

                // Reset Confirmation Dialog Modal
                if (showResetDialog) {
                    ResetConfirmDialog(
                        onDismiss = { viewModel.dismissResetDialog() },
                        onConfirm = { viewModel.confirmResetTodayCounters() }
                    )
                }
            }
        }

        // Animated Splash Screen overlay
        AnimatedVisibility(
            visible = showSplashScreen,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SlateDarkBackground),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.splash),
                    contentDescription = "Pantalla de Inicio",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

