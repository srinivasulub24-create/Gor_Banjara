package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.UserProfileEntity
import com.example.ui.MainViewModel
import com.example.ui.components.ClanChip
import com.example.ui.components.ReportProfileDialog
import com.example.ui.components.SendInterestDialog
import com.example.ui.components.VerifiedPill
import com.example.ui.theme.*

@Composable
fun ProfileDetailScreen(
    profile: UserProfileEntity,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val shortlists by viewModel.shortlists.collectAsState()
    val isShortlisted = remember(shortlists, profile.id) {
        shortlists.any { it.targetProfileId == profile.id }
    }

    var showSendInterestDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showBlockConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Surface(
                color = IvorySurface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedIconButton(
                        onClick = { viewModel.toggleFavorite(profile.id) },
                        modifier = Modifier.size(48.dp).testTag("detail_favorite_btn"),
                        colors = IconButtonDefaults.outlinedIconButtonColors(contentColor = CrimsonRed)
                    ) {
                        Icon(
                            imageVector = if (profile.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Toggle Favorite",
                            tint = if (profile.isFavorite) CrimsonRed else MaroonPrimary
                        )
                    }

                    OutlinedIconButton(
                        onClick = { viewModel.toggleShortlist(profile.id) },
                        modifier = Modifier.size(48.dp).testTag("detail_shortlist_btn"),
                        colors = IconButtonDefaults.outlinedIconButtonColors(contentColor = MaroonPrimary)
                    ) {
                        Icon(
                            imageVector = if (isShortlisted) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Shortlist",
                            tint = if (isShortlisted) GoldDark else MaroonPrimary
                        )
                    }

                    Button(
                        onClick = { showSendInterestDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("detail_send_interest_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaroonPrimary)
                    ) {
                        Icon(Icons.Filled.Favorite, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Express Interest", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .testTag("profile_detail_content")
        ) {
            // Hero Photo Section with Back and Action Buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                AsyncImage(
                    model = profile.photoUrl,
                    contentDescription = "Photo of ${profile.fullName}",
                    modifier = Modifier
                        .fillMaxSize()
                        .then(if (profile.isPhotoBlurred) Modifier.blur(22.dp) else Modifier),
                    contentScale = ContentScale.Crop
                )

                // Scrim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0x33000000), Color.Transparent, Color(0xDD000000)),
                                startY = 0f
                            )
                        )
                )

                // Back Button & More Options (Report / Block)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier
                            .background(Color(0x88000000), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { viewModel.toggleFavorite(profile.id) },
                            modifier = Modifier
                                .background(Color(0x88000000), CircleShape)
                                .size(40.dp)
                                .testTag("detail_favorite_header_btn")
                        ) {
                            Icon(
                                imageVector = if (profile.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Toggle Favorite",
                                tint = if (profile.isFavorite) CrimsonRed else Color.White
                            )
                        }

                        IconButton(
                            onClick = { showReportDialog = true },
                            modifier = Modifier
                                .background(Color(0x88000000), CircleShape)
                                .size(40.dp)
                                .testTag("detail_report_icon")
                        ) {
                            Icon(Icons.Default.ReportProblem, contentDescription = "Report Grievance", tint = Color.White)
                        }

                        IconButton(
                            onClick = { showBlockConfirmDialog = true },
                            modifier = Modifier
                                .background(Color(0x88000000), CircleShape)
                                .size(40.dp)
                                .testTag("detail_block_icon")
                        ) {
                            Icon(Icons.Default.Block, contentDescription = "Block Member", tint = Color.White)
                        }
                    }
                }

                // Overlay Info at Bottom
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = profile.fullName,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        if (profile.isIdentityVerified) {
                            VerifiedPill()
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${profile.age} yrs • ${profile.height} • ${profile.maritalStatus}",
                        color = Color(0xFFEFEFEF),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    ClanChip(clan = profile.clan, subClan = profile.subClan)
                }
            }

            // Body Sections
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // About Me Card
                DetailSectionCard(title = "About Me") {
                    Text(
                        text = profile.aboutMe,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimaryLight,
                            lineHeight = 22.sp
                        )
                    )
                }

                // Banjara Community & Roots
                DetailSectionCard(title = "Banjara Community & Heritage") {
                    DetailRow(label = "Community", value = "Banjara / Lambani")
                    DetailRow(label = "Clan (Goth)", value = profile.clan)
                    DetailRow(label = "Sub-clan (Peda)", value = profile.subClan.ifEmpty { "Not Specified" })
                    DetailRow(label = "Native Tanda", value = profile.tanda)
                    DetailRow(label = "Mother Tongue", value = profile.motherTongue)
                }

                // Education & Career
                DetailSectionCard(title = "Education & Career") {
                    DetailRow(label = "Highest Degree", value = profile.education)
                    DetailRow(label = "Occupation", value = profile.occupation)
                    DetailRow(label = "Annual Income", value = profile.annualIncome)
                    DetailRow(label = "Work Location", value = profile.workLocation)
                }

                // Family Details
                DetailSectionCard(title = "Family Background") {
                    Text(
                        text = profile.familyDetails,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimaryLight,
                            lineHeight = 20.sp
                        )
                    )
                }

                // Lifestyle & Habits
                DetailSectionCard(title = "Lifestyle & Habits") {
                    DetailRow(label = "Diet", value = profile.diet)
                    DetailRow(label = "Drinking", value = profile.drinking)
                    DetailRow(label = "Smoking", value = profile.smoking)
                }

                // Partner Preferences
                DetailSectionCard(title = "Partner Preferences") {
                    Text(
                        text = profile.partnerPreferences,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimaryLight,
                            lineHeight = 20.sp
                        )
                    )
                }

                // Privacy & DPDP Notice Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = IvorySurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = MaroonPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Privacy & Safety Protection",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaroonPrimary
                            )
                            Text(
                                text = "Under DPDP Act 2023, direct contact credentials (phone number, email, address) are private and confidential until both families accept mutual interest.",
                                fontSize = 11.sp,
                                color = TextSecondaryLight
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSendInterestDialog) {
        SendInterestDialog(
            profileName = profile.fullName,
            onDismiss = { showSendInterestDialog = false },
            onSend = { note ->
                viewModel.sendInterest(profile.id, note)
                showSendInterestDialog = false
            }
        )
    }

    if (showReportDialog) {
        ReportProfileDialog(
            targetName = profile.fullName,
            onDismiss = { showReportDialog = false },
            onSubmitReport = { cat, det ->
                viewModel.reportProfile(profile.id, profile.fullName, cat, det)
                showReportDialog = false
            }
        )
    }

    if (showBlockConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showBlockConfirmDialog = false },
            title = { Text("Block ${profile.fullName}?", fontWeight = FontWeight.Bold, color = MaroonPrimary) },
            text = {
                Text("This member will no longer appear in your search results and won't be able to view your profile or send you messages.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.blockUser(profile.id, profile.fullName)
                        showBlockConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SafetyRed)
                ) {
                    Text("Block Member")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBlockConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DetailSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = IvorySurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaroonPrimary
                )
            )
            HorizontalDivider(color = BorderLight)
            content()
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondaryLight, fontSize = 13.sp)
        Text(text = value, color = TextPrimaryLight, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}
