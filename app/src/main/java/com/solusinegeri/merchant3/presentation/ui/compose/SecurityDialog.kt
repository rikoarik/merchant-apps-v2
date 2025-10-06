package com.solusinegeri.merchant3.presentation.ui.compose

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.solusinegeri.merchant3.core.security.SecurityThreat
import com.solusinegeri.merchant3.core.security.ThreatSeverity
import com.solusinegeri.merchant3.core.security.ThreatType
import kotlin.system.exitProcess

@SuppressLint("ContextCastToActivity")
@Composable
fun SecurityScreen(threats: List<SecurityThreat>) {
    var showDialog by remember { mutableStateOf(true) }
    val activity = LocalContext.current as? Activity

    if (showDialog) {
        SecurityDialog(
            threats = threats,
            onDismiss = { showDialog = false },
            onExit = {
                activity?.finishAffinity()
                exitProcess(0)
            }
        )
    }

    if (!showDialog) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "App continues...",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun SecurityDialog(
    threats: List<SecurityThreat>,
    onDismiss: () -> Unit,
    onExit: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        SecurityDialogContent(
            threats = threats,
            onDismiss = onDismiss,
            onExit = onExit
        )
    }
}

@Composable
private fun SecurityDialogContent(
    threats: List<SecurityThreat>,
    onDismiss: () -> Unit,
    onExit: () -> Unit
) {
    val criticalThreats = threats.filter { it.severity == ThreatSeverity.CRITICAL }
    val highThreats = threats.filter { it.severity == ThreatSeverity.HIGH }
    val mediumThreats = threats.filter { it.severity == ThreatSeverity.MEDIUM }
    val lowThreats = threats.filter { it.severity == ThreatSeverity.LOW }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Security Alert",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Security Alert",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Security threats detected on this device:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Threats List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (criticalThreats.isNotEmpty()) {
                    item {
                        ThreatSectionHeader(
                            title = "Critical Threats",
                            count = criticalThreats.size,
                            color = Color(0xFFD32F2F),
                            icon = Icons.Default.Warning
                        )
                    }
                    items(criticalThreats) { threat -> ThreatItem(threat) }
                }
                if (highThreats.isNotEmpty()) {
                    item {
                        ThreatSectionHeader(
                            title = "High Priority",
                            count = highThreats.size,
                            color = Color(0xFFF57C00),
                            icon = Icons.Default.Warning
                        )
                    }
                    items(highThreats) { threat -> ThreatItem(threat) }
                }
                if (mediumThreats.isNotEmpty()) {
                    item {
                        ThreatSectionHeader(
                            title = "Medium Priority",
                            count = mediumThreats.size,
                            color = Color(0xFFFF9800),
                            icon = Icons.Default.Info
                        )
                    }
                    items(mediumThreats) { threat -> ThreatItem(threat) }
                }
                if (lowThreats.isNotEmpty()) {
                    item {
                        ThreatSectionHeader(
                            title = "Low Priority",
                            count = lowThreats.size,
                            color = Color(0xFF4CAF50),
                            icon = Icons.Default.CheckCircle
                        )
                    }
                    items(lowThreats) { threat -> ThreatItem(threat) }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Warning message
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "For security reasons, this app cannot run on compromised devices.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onDismiss() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Continue Anyway")
                }

                Button(
                    onClick = { onExit() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Exit App")
                }
            }
        }
    }
}

@Composable
private fun ThreatSectionHeader(
    title: String,
    count: Int,
    color: Color,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$title ($count)",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun ThreatItem(threat: SecurityThreat) {
    val backgroundColor = when (threat.severity) {
        ThreatSeverity.CRITICAL -> Color(0xFFD32F2F).copy(alpha = 0.1f)
        ThreatSeverity.HIGH -> Color(0xFFF57C00).copy(alpha = 0.1f)
        ThreatSeverity.MEDIUM -> Color(0xFFFF9800).copy(alpha = 0.1f)
        ThreatSeverity.LOW -> Color(0xFF4CAF50).copy(alpha = 0.1f)
    }

    val borderColor = when (threat.severity) {
        ThreatSeverity.CRITICAL -> Color(0xFFD32F2F)
        ThreatSeverity.HIGH -> Color(0xFFF57C00)
        ThreatSeverity.MEDIUM -> Color(0xFFFF9800)
        ThreatSeverity.LOW -> Color(0xFF4CAF50)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getThreatIcon(threat.type),
                contentDescription = null,
                tint = borderColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = threat.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun getThreatIcon(type: ThreatType): ImageVector {
    return when (type) {
        ThreatType.ROOT_DETECTED -> Icons.Default.Warning
        ThreatType.MAGISK_DETECTED -> Icons.Default.Build
        ThreatType.FRIDA_DETECTED -> Icons.Default.Warning
        ThreatType.EMULATOR_DETECTED -> Icons.Default.Phone
        ThreatType.DEBUGGER_DETECTED -> Icons.Default.Warning
        ThreatType.HOOK_DETECTED -> Icons.Default.Build
    }
}
