package com.example.virtual_steer.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.virtual_steer.model.DiscoveredServer
import com.example.virtual_steer.ui.theme.*
import com.example.virtual_steer.viewmodel.ConnectionStatus
import com.example.virtual_steer.viewmodel.ControllerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PairScreen(
    viewModel: ControllerViewModel,
    onPairSuccess: () -> Unit
) {
    val discoveredServers by viewModel.discoveredServers.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()

    LaunchedEffect(connectionState.status) {
        if (connectionState.status == ConnectionStatus.CONNECTED) {
            onPairSuccess()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(CarbonDark)
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // LEFT COLUMN: Branding, Searching Status, and USB button
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "VIRTUAL STEER",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            
            Text(
                text = "PAIR WITH PC",
                color = ThrottleGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Discovery Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = ThrottleGreen,
                    strokeWidth = 2.dp
                )
                Text(
                    text = "SEARCHING...",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // USB Wired Connection Helper
            var showUsbDialog by remember { mutableStateOf(false) }
            val context = androidx.compose.ui.platform.LocalContext.current

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(GridPanelBg)
                    .border(1.dp, ThrottleGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .clickable { showUsbDialog = true }
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🔌 CONNECT VIA WIRED USB",
                    color = ThrottleGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            if (showUsbDialog) {
                AlertDialog(
                    onDismissRequest = { showUsbDialog = false },
                    title = {
                        Text(
                            "Wired USB Guide",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                "USB connection cuts latency to <1ms and guarantees zero wireless packet loss.",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "1. Connect phone to PC using a USB data cable.",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                "2. Tap the button below to open Tethering settings.",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                "3. Turn on \"USB Tethering\" or \"USB sharing\".",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                "4. Return to this app. The PC will appear in the list.",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                try {
                                    val intent = android.content.Intent().apply {
                                        action = "android.intent.action.MAIN"
                                        setClassName("com.android.settings", "com.android.settings.TetherSettings")
                                        flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    try {
                                        val intent = android.content.Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS).apply {
                                            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                        context.startActivity(intent)
                                    } catch (e2: Exception) {
                                        val intent = android.content.Intent(android.provider.Settings.ACTION_SETTINGS).apply {
                                            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                        context.startActivity(intent)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ThrottleGreen),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("TETHERING SETTINGS", color = CarbonDark, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showUsbDialog = false }) {
                            Text("CLOSE", color = Color.White.copy(alpha = 0.5f), fontFamily = FontFamily.Monospace)
                        }
                    },
                    containerColor = CarbonDark,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            AnimatedVisibility(visible = connectionState.status == ConnectionStatus.CONNECTING) {
                Text(
                    text = "Connecting to ${connectionState.serverIp}...",
                    color = AccentYellow,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        // RIGHT COLUMN: Available Receivers List
        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight()
        ) {
            Text(
                text = "AVAILABLE RECEIVERS",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (discoveredServers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(GridPanelBg.copy(alpha = 0.3f))
                            .border(1.dp, MetallicBorder, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No PC found yet.\nCheck same Wi-Fi or Hotspot.",
                            color = Color.White.copy(alpha = 0.3f),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(discoveredServers) { server ->
                            ServerCard(server = server) {
                                viewModel.connectToPC(server.ip, server.port, server.name)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServerCard(
    server: DiscoveredServer,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(GridPanelBg)
            .border(1.dp, MetallicBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = server.name,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = server.ip,
                    color = ThrottleGreen,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Port ${server.port}",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "READY",
                    color = ThrottleGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
                
                val badgeColor = if (server.connectionType == "USB") ThrottleGreen else Color(0xFF2196F3)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .border(1.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = server.connectionType.uppercase(),
                        color = badgeColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
