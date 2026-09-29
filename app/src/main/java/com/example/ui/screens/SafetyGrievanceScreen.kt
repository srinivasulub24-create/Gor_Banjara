package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun SafetyGrievanceScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val blockedUsers by viewModel.blockedUsers.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("safety_grievance_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Legal & Trust Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaroonPrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = GoldLight, modifier = Modifier.size(28.dp))
                    Text(
                        text = "Safety & Legal Compliance",
                        style = MaterialTheme.typography.titleMedium.copy(color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold)
                    )
                }
                Text(
                    text = "Banjara Matrimony is built with Privacy-by-Design under India's DPDP Act 2023, IT Intermediary Rules 2021, and zero-tolerance child protection policies.",
                    color = IvoryBackground,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        // DPDP Act 2023 Privacy Safeguards
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = IvorySurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaroonPrimary, modifier = Modifier.size(20.dp))
                    Text("Digital Personal Data Protection (DPDP)", fontWeight = FontWeight.Bold, color = MaroonPrimary, fontSize = 14.sp)
                }
                HorizontalDivider(color = BorderLight)
                CompliancePoint(title = "Data Minimization", desc = "We only collect information strictly required for matrimonial match-making (Clan, Education, Profession, Family background).")
                CompliancePoint(title = "Contact Confidentiality", desc = "Your phone number and exact residential address are encrypted and never shown to casual browsers. They are disclosed only upon mutual connection.")
                CompliancePoint(title = "Photo Privacy Controls", desc = "You can blur your photograph at any time. Non-connected members see a protected blurred image.")
                CompliancePoint(title = "Right to Erasure", desc = "Members have the statutory right to request permanent deletion of their profile and media at any time.")
            }
        }

        // POCSO & Adult Age Gate Policy
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = IvorySurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.ChildCare, contentDescription = null, tint = SafetyRed, modifier = Modifier.size(20.dp))
                    Text("Strict Adult-Only (18+) & Child Safety", fontWeight = FontWeight.Bold, color = SafetyRed, fontSize = 14.sp)
                }
                HorizontalDivider(color = BorderLight)
                Text(
                    text = "• In strict compliance with Indian law (including POCSO Act) and platform guidelines, Banjara Matrimony is strictly for consenting adults aged 18 and above.",
                    fontSize = 12.sp,
                    color = TextPrimaryLight,
                    lineHeight = 18.sp
                )
                Text(
                    text = "• Any profile attempting to register an underage individual is rejected immediately at our automated age gate and flagged to moderators.",
                    fontSize = 12.sp,
                    color = TextPrimaryLight,
                    lineHeight = 18.sp
                )
                Text(
                    text = "• Zero tolerance for exploitation, abuse or inappropriate content. Violations result in permanent bans and mandatory referral to law enforcement.",
                    fontSize = 12.sp,
                    color = SafetyRed,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 18.sp
                )
            }
        }

        // IT Rules 2021 Grievance Officer
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = IvorySurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Gavel, contentDescription = null, tint = MaroonPrimary, modifier = Modifier.size(20.dp))
                    Text("Statutory Grievance Redressal (IT Rules)", fontWeight = FontWeight.Bold, color = MaroonPrimary, fontSize = 14.sp)
                }
                HorizontalDivider(color = BorderLight)
                Text("Designated Grievance Officer:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text("Officer: Dr. Suresh Rathod, Advocate & Compliance Officer", fontSize = 12.sp, color = TextPrimaryLight)
                Text("Address: Banjara Matrimony Trust, Banjara Hills Road No. 12, Hyderabad, Telangana - 500034", fontSize = 11.sp, color = TextSecondaryLight)
                Text("Grievance Email: grievance@banjaramatrimony.org", fontSize = 11.sp, color = MaroonPrimary, fontWeight = FontWeight.Medium)
                Text("Response SLA: Acknowledgment within 24 hours; resolution within 15 days.", fontSize = 11.sp, color = TextSecondaryLight)
            }
        }

        // Blocked Users Management
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = IvorySurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Blocked Users (${blockedUsers.size})", fontWeight = FontWeight.Bold, color = MaroonPrimary)
                    Icon(Icons.Default.Block, contentDescription = null, tint = SafetyRed, modifier = Modifier.size(18.dp))
                }
                HorizontalDivider(color = BorderLight)

                if (blockedUsers.isEmpty()) {
                    Text(
                        text = "You have not blocked any users. You can block any member from their profile or chat screen.",
                        fontSize = 12.sp,
                        color = TextSecondaryLight
                    )
                } else {
                    blockedUsers.forEach { b ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(b.blockedName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Blocked on ${java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(b.createdAt)}", fontSize = 10.sp, color = TextSecondaryLight)
                            }
                            TextButton(onClick = { viewModel.unblockUser(b.blockedProfileId) }) {
                                Text("Unblock", color = MaroonPrimary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompliancePoint(title: String, desc: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text("• $title", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = TextPrimaryLight)
        Text(desc, fontSize = 11.sp, color = TextSecondaryLight, lineHeight = 16.sp, modifier = Modifier.padding(start = 12.dp))
    }
}
