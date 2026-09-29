package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.UserProfileEntity
import com.example.ui.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatrimonyTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit,
    unreadNotifCount: Int = 0,
    onNotifClick: () -> Unit = {},
    onAdminClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.banjara_logo),
                    contentDescription = "Banjara Emblem",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                )
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaroonPrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Trusted Community Matrimony",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GoldDark,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("nav_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Navigate back",
                        tint = MaroonPrimary
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = onAdminClick,
                modifier = Modifier.testTag("admin_console_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.AdminPanelSettings,
                    contentDescription = "Moderator & Admin Console",
                    tint = MaroonPrimary
                )
            }
            IconButton(
                onClick = onNotifClick,
                modifier = Modifier.testTag("notifications_button")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadNotifCount > 0) {
                            Badge(
                                containerColor = MaroonPrimary,
                                contentColor = Color.White
                            ) {
                                Text("$unreadNotifCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = MaroonPrimary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = IvorySurface,
            titleContentColor = MaroonPrimary
        )
    )
}

@Composable
fun MatrimonyBottomBar(
    currentScreen: Screen,
    receivedInterestsCount: Int,
    onTabSelected: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = IvorySurface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen == Screen.DISCOVER,
            onClick = { onTabSelected(Screen.DISCOVER) },
            icon = { Icon(if (currentScreen == Screen.DISCOVER) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, contentDescription = "Discover") },
            label = { Text("Matches", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaroonPrimary,
                selectedTextColor = MaroonPrimary,
                indicatorColor = MaroonContainer
            ),
            modifier = Modifier.testTag("nav_tab_discover")
        )
        NavigationBarItem(
            selected = currentScreen == Screen.SEARCH,
            onClick = { onTabSelected(Screen.SEARCH) },
            icon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            label = { Text("Search", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaroonPrimary,
                selectedTextColor = MaroonPrimary,
                indicatorColor = MaroonContainer
            ),
            modifier = Modifier.testTag("nav_tab_search")
        )
        NavigationBarItem(
            selected = currentScreen == Screen.INTERESTS,
            onClick = { onTabSelected(Screen.INTERESTS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (receivedInterestsCount > 0) {
                            Badge(containerColor = MaroonPrimary) {
                                Text("$receivedInterestsCount")
                            }
                        }
                    }
                ) {
                    Icon(if (currentScreen == Screen.INTERESTS) Icons.Filled.People else Icons.Outlined.People, contentDescription = "Interests")
                }
            },
            label = { Text("Interests", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaroonPrimary,
                selectedTextColor = MaroonPrimary,
                indicatorColor = MaroonContainer
            ),
            modifier = Modifier.testTag("nav_tab_interests")
        )
        NavigationBarItem(
            selected = currentScreen == Screen.SAFETY_GRIEVANCE,
            onClick = { onTabSelected(Screen.SAFETY_GRIEVANCE) },
            icon = { Icon(if (currentScreen == Screen.SAFETY_GRIEVANCE) Icons.Filled.Shield else Icons.Outlined.Shield, contentDescription = "Safety") },
            label = { Text("Safety", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaroonPrimary,
                selectedTextColor = MaroonPrimary,
                indicatorColor = MaroonContainer
            ),
            modifier = Modifier.testTag("nav_tab_safety")
        )
        NavigationBarItem(
            selected = currentScreen == Screen.MY_PROFILE,
            onClick = { onTabSelected(Screen.MY_PROFILE) },
            icon = { Icon(if (currentScreen == Screen.MY_PROFILE) Icons.Filled.AccountCircle else Icons.Outlined.AccountCircle, contentDescription = "My Profile") },
            label = { Text("Profile", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaroonPrimary,
                selectedTextColor = MaroonPrimary,
                indicatorColor = MaroonContainer
            ),
            modifier = Modifier.testTag("nav_tab_profile")
        )
    }
}

@Composable
fun ProfileCard(
    profile: UserProfileEntity,
    isShortlisted: Boolean,
    onCardClick: () -> Unit,
    onShortlistClick: () -> Unit,
    onSendInterestClick: () -> Unit,
    modifier: Modifier = Modifier,
    onFavoriteClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("profile_card_${profile.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = IvorySurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Photo header with tags and Shortlist FAB
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                AsyncImage(
                    model = profile.photoUrl,
                    contentDescription = "Photo of ${profile.fullName}",
                    modifier = Modifier
                        .fillMaxSize()
                        .then(if (profile.isPhotoBlurred) Modifier.blur(20.dp) else Modifier),
                    contentScale = ContentScale.Crop
                )

                // Gradient scrim at bottom of photo
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xAA000000)),
                                startY = 300f
                            )
                        )
                )

                // Privacy Indicator if blurred
                if (profile.isPhotoBlurred) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(8.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xCC000000)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = GoldLight, modifier = Modifier.size(16.dp))
                            Text("Photo Protected by Privacy", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }

                // Top Tags: Clan & Favorite / Shortlist Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ClanChip(clan = profile.clan, subClan = profile.subClan)

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (onFavoriteClick != null) {
                            IconButton(
                                onClick = onFavoriteClick,
                                modifier = Modifier
                                    .background(Color(0x88000000), CircleShape)
                                    .size(36.dp)
                                    .testTag("favorite_button_${profile.id}")
                            ) {
                                Icon(
                                    imageVector = if (profile.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Favorite profile",
                                    tint = if (profile.isFavorite) CrimsonRed else Color.White,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = onShortlistClick,
                            modifier = Modifier
                                .background(Color(0x88000000), CircleShape)
                                .size(36.dp)
                                .testTag("shortlist_button_${profile.id}")
                        ) {
                            Icon(
                                imageVector = if (isShortlisted) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Shortlist profile",
                                tint = if (isShortlisted) GoldLight else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Bottom overlay on photo: Name and Verified badge
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = profile.fullName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (profile.isIdentityVerified) {
                            VerifiedPill()
                        }
                    }

                    Text(
                        text = "${profile.age} yrs • ${profile.height}",
                        color = Color(0xFFEFEFEF),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Card Body: Details & CTAs
            Column(modifier = Modifier.padding(16.dp)) {
                // Key attributes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProfileAttribute(
                        icon = Icons.Default.School,
                        label = "Education",
                        value = profile.education
                    )
                    ProfileAttribute(
                        icon = Icons.Default.Work,
                        label = "Occupation",
                        value = profile.occupation
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProfileAttribute(
                        icon = Icons.Default.LocationOn,
                        label = "Location",
                        value = "${profile.city}, ${profile.state}"
                    )
                    ProfileAttribute(
                        icon = Icons.Default.CurrencyRupee,
                        label = "Income",
                        value = profile.annualIncome
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(modifier = Modifier.height(12.dp))

                // Actions: View Bio & Send Interest
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onCardClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("view_profile_${profile.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaroonPrimary)
                    ) {
                        Text("View Biodata", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onSendInterestClick,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("send_interest_${profile.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaroonPrimary)
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Interest", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ClanChip(clan: String, subClan: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaroonPrimary,
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldLight)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Goth: $clan",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            if (subClan.isNotEmpty()) {
                Text(
                    text = "($subClan)",
                    color = GoldContainer,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun VerifiedPill() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = VerifiedGreenContainer,
        border = androidx.compose.foundation.BorderStroke(1.dp, VerifiedGreen)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                Icons.Default.Verified,
                contentDescription = "Verified Identity",
                tint = VerifiedGreen,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "Verified",
                color = VerifiedGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProfileAttribute(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.widthIn(max = 160.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GoldDark,
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextSecondaryLight
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimaryLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SendInterestDialog(
    profileName: String,
    onDismiss: () -> Unit,
    onSend: (String) -> Unit
) {
    var note by remember { mutableStateOf("Namaste! We reviewed your profile and found our family values and career goals very compatible. We would be privileged to connect.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Filled.Favorite, contentDescription = null, tint = MaroonPrimary)
                Text("Express Matrimonial Interest", color = MaroonPrimary, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Expressing interest in $profileName. You may include a respectful personal or family message:",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("interest_note_input"),
                    placeholder = { Text("Write a polite note...") },
                    maxLines = 4
                )
                Text(
                    text = "• Private contact info (Phone/Email) will only be shared once mutual interest is accepted.",
                    fontSize = 11.sp,
                    color = TextSecondaryLight
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSend(note) },
                colors = ButtonDefaults.buttonColors(containerColor = MaroonPrimary),
                modifier = Modifier.testTag("confirm_send_interest_btn")
            ) {
                Text("Send Interest")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondaryLight)
            }
        }
    )
}

@Composable
fun ReportProfileDialog(
    targetName: String,
    onDismiss: () -> Unit,
    onSubmitReport: (category: String, details: String) -> Unit
) {
    val categories = listOf(
        "Underage / Minor concern (<18 yrs)",
        "Fake Profile / Impersonation",
        "Financial Fraud or Dowry Demand",
        "Harassment / Inappropriate Messages",
        "Inappropriate or Stolen Photo",
        "Incorrect Clan/Marital Status Info"
    )
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var details by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = SafetyRed)
                Text("File Grievance / Safety Report", color = SafetyRed, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Reporting $targetName under Indian IT Rules 2021 & DPDP Act 2023. Reports are reviewed urgently by our safety grievance desk.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryLight
                )

                Text("Select Category:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)

                categories.forEach { category ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCategory = category }
                            .padding(vertical = 2.dp)
                    ) {
                        RadioButton(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            colors = RadioButtonDefaults.colors(selectedColor = MaroonPrimary)
                        )
                        Text(category, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                    }
                }

                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Details & Evidence description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .testTag("report_details_input"),
                    placeholder = { Text("Describe the issue...") },
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmitReport(selectedCategory, details) },
                colors = ButtonDefaults.buttonColors(containerColor = SafetyRed),
                modifier = Modifier.testTag("submit_report_btn")
            ) {
                Text("Submit to Moderation")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
