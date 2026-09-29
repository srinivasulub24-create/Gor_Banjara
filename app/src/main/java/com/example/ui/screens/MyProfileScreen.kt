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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.ClanChip
import com.example.ui.theme.*

@Composable
fun MyProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var isEditing by remember { mutableStateOf(false) }

    var editedAboutMe by remember(currentUser) { mutableStateOf(currentUser?.aboutMe ?: "") }
    var editedOccupation by remember(currentUser) { mutableStateOf(currentUser?.occupation ?: "") }
    var editedIncome by remember(currentUser) { mutableStateOf(currentUser?.annualIncome ?: "") }
    var editedPreferences by remember(currentUser) { mutableStateOf(currentUser?.partnerPreferences ?: "") }

    val user = currentUser ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("my_profile_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = IvorySurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box {
                    AsyncImage(
                        model = user.photoUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { viewModel.togglePhotoBlur() },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(MaroonPrimary, CircleShape)
                            .size(30.dp)
                    ) {
                        Icon(
                            imageVector = if (user.isPhotoBlurred) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle photo privacy",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = user.fullName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaroonPrimary)
                )

                Text(
                    text = "${user.age} yrs • ${user.height} • ${user.education}",
                    fontSize = 12.sp,
                    color = TextSecondaryLight
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ClanChip(clan = user.clan, subClan = user.subClan)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GoldContainer
                    ) {
                        Text(
                            text = "${user.membershipTier} MEMBER",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // Privacy by Design: Photo Blur Toggle Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = IvorySurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Photo Privacy Mode (DPDP)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaroonPrimary
                    )
                    Text(
                        text = "Blur your photo for non-connected viewers. Only members whose interest you accept can view your clear photo.",
                        fontSize = 11.sp,
                        color = TextSecondaryLight
                    )
                }
                Switch(
                    checked = user.isPhotoBlurred,
                    onCheckedChange = { viewModel.togglePhotoBlur() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaroonPrimary,
                        checkedTrackColor = MaroonContainer
                    )
                )
            }
        }

        // Verification & Trust Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = IvorySurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = VerifiedGreen)
                    Text("Trust & Verification Status", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Identity Verification (18+ Age Gate)", fontSize = 12.sp, color = TextSecondaryLight)
                    Text(
                        text = if (user.isIdentityVerified) "VERIFIED" else "PENDING REVIEW",
                        color = if (user.isIdentityVerified) VerifiedGreen else GoldDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Phone Number", fontSize = 12.sp, color = TextSecondaryLight)
                    Text("VERIFIED (OTP)", color = VerifiedGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Banjara Clan Roots Check", fontSize = 12.sp, color = TextSecondaryLight)
                    Text("RECORDED", color = VerifiedGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Membership Plan Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = GoldContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Current Tier: ${user.membershipTier}", fontWeight = FontWeight.Bold, color = GoldDark)
                        Text("Direct Chat Enabled • Verified Badge Active", fontSize = 11.sp, color = TextSecondaryLight)
                    }
                    Button(
                        onClick = { viewModel.navigateTo(Screen.PLANS) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaroonPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("View Plans", fontSize = 11.sp)
                    }
                }
            }
        }

        // Editable Biodata Fields
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
                    Text("Matrimonial Biodata", fontWeight = FontWeight.Bold, color = MaroonPrimary)
                    TextButton(onClick = { isEditing = !isEditing }) {
                        Text(if (isEditing) "Cancel" else "Edit", color = MaroonLight)
                    }
                }

                if (isEditing) {
                    OutlinedTextField(
                        value = editedAboutMe,
                        onValueChange = { editedAboutMe = it },
                        label = { Text("About Me & Cultural Background") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                    OutlinedTextField(
                        value = editedOccupation,
                        onValueChange = { editedOccupation = it },
                        label = { Text("Profession / Occupation") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editedIncome,
                        onValueChange = { editedIncome = it },
                        label = { Text("Annual Income (e.g. ₹24 LPA)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editedPreferences,
                        onValueChange = { editedPreferences = it },
                        label = { Text("Partner Preferences (Goth, Age, Location)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Button(
                        onClick = {
                            viewModel.updateUserProfile(
                                user.copy(
                                    aboutMe = editedAboutMe,
                                    occupation = editedOccupation,
                                    annualIncome = editedIncome,
                                    partnerPreferences = editedPreferences
                                )
                            )
                            isEditing = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaroonPrimary)
                    ) {
                        Text("Save Biodata Changes")
                    }
                } else {
                    ProfileFieldDisplay(label = "About Me", value = user.aboutMe)
                    ProfileFieldDisplay(label = "Native Tanda", value = user.tanda)
                    ProfileFieldDisplay(label = "Occupation", value = user.occupation)
                    ProfileFieldDisplay(label = "Annual Income", value = user.annualIncome)
                    ProfileFieldDisplay(label = "Partner Preferences", value = user.partnerPreferences)
                }
            }
        }

        // Registration & Switch Account Shortcut
        OutlinedButton(
            onClick = { viewModel.navigateTo(Screen.REGISTRATION) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null, tint = MaroonPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Register New Member Profile (Age Gate Test)", color = MaroonPrimary)
        }
    }
}

@Composable
fun ProfileFieldDisplay(label: String, value: String) {
    Column {
        Text(label, fontSize = 11.sp, color = TextSecondaryLight, fontWeight = FontWeight.Medium)
        Text(value, fontSize = 13.sp, color = TextPrimaryLight)
        Spacer(modifier = Modifier.height(4.dp))
    }
}
