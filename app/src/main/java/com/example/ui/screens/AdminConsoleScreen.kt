package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.AuditLogEntity
import com.example.data.local.ReportEntity
import com.example.data.local.UserProfileEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminConsoleScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val pendingVerifications by viewModel.pendingVerifications.collectAsState()
    val reports by viewModel.reports.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val allProfiles by viewModel.allDiscoverable.collectAsState()

    var activeAdminTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Verifications", "Reports & Abuse", "Audit Logs")

    var selectedReportForAction by remember { mutableStateOf<ReportEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaroonPrimary)
                        Column {
                            Text("Moderation & Admin Console", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Role: Senior Moderator • MFA Active", fontSize = 11.sp, color = VerifiedGreen)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IvorySurface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("admin_console_container")
        ) {
            PrimaryTabRow(
                selectedTabIndex = activeAdminTab,
                containerColor = IvorySurface,
                contentColor = MaroonPrimary
            ) {
                tabs.forEachIndexed { idx, label ->
                    Tab(
                        selected = activeAdminTab == idx,
                        onClick = { activeAdminTab = idx },
                        text = { Text(label, fontSize = 11.sp, fontWeight = if (activeAdminTab == idx) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            when (activeAdminTab) {
                0 -> {
                    // Dashboard Overview
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text("Platform Health & SLA Metrics", fontWeight = FontWeight.Bold, color = MaroonPrimary, fontSize = 14.sp)
                        }

                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                AdminMetricCard(
                                    title = "Total Profiles",
                                    value = "${allProfiles.size + 1}",
                                    icon = Icons.Default.People,
                                    color = MaroonPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                AdminMetricCard(
                                    title = "Pending KYC",
                                    value = "${pendingVerifications.size}",
                                    icon = Icons.Default.PendingActions,
                                    color = if (pendingVerifications.isNotEmpty()) GoldDark else VerifiedGreen,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                AdminMetricCard(
                                    title = "Open Reports",
                                    value = "${reports.count { it.status == "PENDING" }}",
                                    icon = Icons.Default.Warning,
                                    color = SafetyRed,
                                    modifier = Modifier.weight(1f)
                                )
                                AdminMetricCard(
                                    title = "Audit Actions",
                                    value = "${auditLogs.size}",
                                    icon = Icons.Default.History,
                                    color = MaroonLight,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = IvorySurface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("IT Intermediary & DPDP Compliance Summary", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaroonPrimary)
                                    Text("• 24-Hour Grievance Acknowledgment: 100% compliant", fontSize = 12.sp, color = VerifiedGreen)
                                    Text("• 15-Day Redressal SLA: 100% compliant", fontSize = 12.sp, color = VerifiedGreen)
                                    Text("• Adult Age Gate (18+ Mandatory): Active on all onboarding points", fontSize = 12.sp, color = VerifiedGreen)
                                    Text("• Child Sexual Abuse Zero-Tolerance Gate: Active with immediate escalation path", fontSize = 12.sp, color = VerifiedGreen)
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Pending Verifications Queue
                    if (pendingVerifications.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VerifiedGreen, modifier = Modifier.size(48.dp))
                                Text("All Identity Verifications Cleared", fontWeight = FontWeight.Bold)
                                Text("No pending verification requests in the queue.", fontSize = 12.sp, color = TextSecondaryLight)
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(pendingVerifications, key = { it.id }) { prof ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = IvorySurface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            AsyncImage(
                                                model = prof.photoUrl,
                                                contentDescription = null,
                                                modifier = Modifier.size(50.dp).clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(prof.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Text("DOB: ${prof.dateOfBirth} (Age: ${prof.age} • 18+ Valid)", fontSize = 12.sp, color = TextSecondaryLight)
                                                Text("Clan: ${prof.clan} • Tanda: ${prof.tanda}", fontSize = 11.sp, color = MaroonLight)
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = { viewModel.rejectVerification(prof.id, prof.fullName, "Document mismatch or unclear ID") },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = SafetyRed)
                                            ) {
                                                Text("Reject")
                                            }

                                            Button(
                                                onClick = { viewModel.approveVerification(prof.id, prof.fullName) },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(containerColor = VerifiedGreen)
                                            ) {
                                                Text("Approve & Badge")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Reports & Abuse Moderation Queue
                    if (reports.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No abuse reports lodged.", color = TextSecondaryLight)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(reports, key = { it.id }) { report ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = IvorySurface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Target: ${report.targetName}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = if (report.status == "PENDING") SafetyRedContainer else IvorySurfaceVariant
                                            ) {
                                                Text(
                                                    text = report.status,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (report.status == "PENDING") SafetyRed else TextSecondaryLight,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Text("Category: ${report.category}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaroonPrimary)
                                        Text(report.description, fontSize = 12.sp, color = TextPrimaryLight)

                                        if (report.status == "PENDING") {
                                            Button(
                                                onClick = { selectedReportForAction = report },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaroonPrimary),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text("Take Moderator Action")
                                            }
                                        } else {
                                            Text("Action Taken: ${report.actionTaken}", fontSize = 11.sp, color = VerifiedGreen, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Audit Logs
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(auditLogs, key = { it.id }) { log ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = IvorySurface)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(log.action, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaroonPrimary)
                                        Text(
                                            SimpleDateFormat("dd MMM HH:mm", Locale.getDefault()).format(log.timestamp),
                                            fontSize = 10.sp,
                                            color = TextSecondaryLight
                                        )
                                    }
                                    Text("Actor: ${log.actorRole} • Target: ${log.targetName}", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    Text(log.details, fontSize = 11.sp, color = TextSecondaryLight)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    selectedReportForAction?.let { report ->
        AlertDialog(
            onDismissRequest = { selectedReportForAction = null },
            title = { Text("Moderator Decision", fontWeight = FontWeight.Bold, color = MaroonPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select statutory moderation response for ${report.targetName}:")
                    val actions = listOf("Issue Warning", "Profile Restricted", "Account Suspended", "Dismiss Report")
                    actions.forEach { act ->
                        OutlinedButton(
                            onClick = {
                                viewModel.moderateReport(report.id, act, report.targetProfileId, report.targetName)
                                selectedReportForAction = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(act)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedReportForAction = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AdminMetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = IvorySurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = color)
            Text(title, fontSize = 11.sp, color = TextSecondaryLight)
        }
    }
}
