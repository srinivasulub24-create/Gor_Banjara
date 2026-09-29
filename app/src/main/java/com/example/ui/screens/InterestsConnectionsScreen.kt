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
import com.example.data.local.InterestEntity
import com.example.data.local.UserProfileEntity
import com.example.ui.MainViewModel
import com.example.ui.components.ClanChip
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestsConnectionsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Received", "Sent", "Connected", "Favorites", "Shortlist")

    val receivedInterests by viewModel.receivedInterests.collectAsState()
    val sentInterests by viewModel.sentInterests.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val shortlists by viewModel.shortlists.collectAsState()
    val allProfiles by viewModel.allDiscoverable.collectAsState()

    val profilesMap = remember(allProfiles) { allProfiles.associateBy { it.id } }

    Column(modifier = modifier.fillMaxSize().testTag("interests_screen_container")) {
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = IvorySurface,
            contentColor = MaroonPrimary
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> {
                // Received Interests
                val pendingReceived = receivedInterests.filter { it.status == "PENDING" }
                if (pendingReceived.isEmpty()) {
                    EmptyTabState(
                        icon = Icons.Outlined.MailOutline,
                        title = "No Pending Interests",
                        subtitle = "When a member expresses interest in your profile, their invitation and note will appear here."
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pendingReceived, key = { it.id }) { interest ->
                            val sender = profilesMap[interest.senderId]
                            if (sender != null) {
                                ReceivedInterestCard(
                                    sender = sender,
                                    interest = interest,
                                    onAccept = { viewModel.acceptInterest(interest.id) },
                                    onDecline = { viewModel.declineInterest(interest.id) },
                                    onViewProfile = { viewModel.openProfileDetail(sender) }
                                )
                            }
                        }
                    }
                }
            }
            1 -> {
                // Sent Interests
                if (sentInterests.isEmpty()) {
                    EmptyTabState(
                        icon = Icons.Outlined.Send,
                        title = "No Sent Interests",
                        subtitle = "You have not expressed interest to anyone yet. Discover matches and send your first interest!"
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(sentInterests, key = { it.id }) { interest ->
                            val receiver = profilesMap[interest.receiverId]
                            if (receiver != null) {
                                SentInterestCard(
                                    receiver = receiver,
                                    interest = interest,
                                    onWithdraw = { viewModel.withdrawInterest(interest.id) },
                                    onViewProfile = { viewModel.openProfileDetail(receiver) }
                                )
                            }
                        }
                    }
                }
            }
            2 -> {
                // Connected Matches (Accepted mutual interest)
                val connectedInterests = (receivedInterests + sentInterests).filter { it.status == "ACCEPTED" }
                val connectedPartnerIds = connectedInterests.map {
                    if (it.senderId == "user_me") it.receiverId else it.senderId
                }.distinct()

                val connectedProfiles = remember(connectedPartnerIds, profilesMap) {
                    connectedPartnerIds.mapNotNull { profilesMap[it] }
                }

                if (connectedProfiles.isEmpty()) {
                    EmptyTabState(
                        icon = Icons.Outlined.Favorite,
                        title = "No Active Connections",
                        subtitle = "Mutual acceptances create a secure connection where you can chat and exchange family contacts."
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(connectedProfiles, key = { it.id }) { partner ->
                            ConnectedPartnerCard(
                                partner = partner,
                                onChatClick = { viewModel.openChatWith(partner) },
                                onViewProfile = { viewModel.openProfileDetail(partner) }
                            )
                        }
                    }
                }
            }
            3 -> {
                // Favorites (Room Database isFavorite = true)
                if (favorites.isEmpty()) {
                    EmptyTabState(
                        icon = Icons.Outlined.FavoriteBorder,
                        title = "No Favorite Profiles Yet",
                        subtitle = "Tap the heart icon on any profile to mark them as a favorite. Saved directly in your local Room database."
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(favorites, key = { it.id }) { profile ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.openProfileDetail(profile) }
                                    .testTag("favorite_card_${profile.id}"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = IvorySurface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    AsyncImage(
                                        model = profile.photoUrl,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(profile.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            if (profile.isIdentityVerified) {
                                                Icon(Icons.Default.Verified, contentDescription = null, tint = VerifiedGreen, modifier = Modifier.size(15.dp))
                                            }
                                        }
                                        Text("${profile.age} yrs • ${profile.clan} • ${profile.location}", fontSize = 12.sp, color = TextSecondaryLight)
                                        Text(profile.occupation, fontSize = 12.sp, color = MaroonLight, fontWeight = FontWeight.Medium)
                                    }
                                    IconButton(
                                        onClick = { viewModel.toggleFavorite(profile.id) },
                                        modifier = Modifier.testTag("remove_favorite_${profile.id}")
                                    ) {
                                        Icon(
                                            Icons.Filled.Favorite,
                                            contentDescription = "Remove from Favorites",
                                            tint = CrimsonRed
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            4 -> {
                // Shortlist
                val shortlistedProfiles = remember(shortlists, profilesMap) {
                    shortlists.mapNotNull { profilesMap[it.targetProfileId] }
                }

                if (shortlistedProfiles.isEmpty()) {
                    EmptyTabState(
                        icon = Icons.Outlined.BookmarkBorder,
                        title = "Shortlist is Empty",
                        subtitle = "Tap the bookmark icon on any profile to save them to your private shortlist for family discussion."
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(shortlistedProfiles, key = { it.id }) { profile ->
                            ShortlistedCard(
                                profile = profile,
                                onRemove = { viewModel.toggleShortlist(profile.id) },
                                onViewProfile = { viewModel.openProfileDetail(profile) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReceivedInterestCard(
    sender: UserProfileEntity,
    interest: InterestEntity,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onViewProfile: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onViewProfile),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = IvorySurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = sender.photoUrl,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(sender.fullName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("${sender.age} yrs • ${sender.occupation}", fontSize = 12.sp, color = TextSecondaryLight)
                    Spacer(modifier = Modifier.height(2.dp))
                    ClanChip(clan = sender.clan, subClan = sender.subClan)
                }
            }

            if (interest.personalNote.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = IvorySurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "\"${interest.personalNote}\"",
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = TextPrimaryLight,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDecline,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondaryLight)
                ) {
                    Text("Decline")
                }

                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaroonPrimary)
                ) {
                    Text("Accept & Connect")
                }
            }
        }
    }
}

@Composable
fun SentInterestCard(
    receiver: UserProfileEntity,
    interest: InterestEntity,
    onWithdraw: () -> Unit,
    onViewProfile: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onViewProfile),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = IvorySurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = receiver.photoUrl,
                contentDescription = null,
                modifier = Modifier.size(52.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(receiver.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("${receiver.age} yrs • Goth: ${receiver.clan}", fontSize = 12.sp, color = TextSecondaryLight)
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (interest.status == "ACCEPTED") VerifiedGreenContainer else GoldContainer
                ) {
                    Text(
                        text = "Status: ${interest.status}",
                        color = if (interest.status == "ACCEPTED") VerifiedGreen else GoldDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (interest.status == "PENDING") {
                TextButton(onClick = onWithdraw) {
                    Text("Withdraw", color = MaroonLight, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ConnectedPartnerCard(
    partner: UserProfileEntity,
    onChatClick: () -> Unit,
    onViewProfile: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = IvorySurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = partner.photoUrl,
                    contentDescription = null,
                    modifier = Modifier.size(54.dp).clip(CircleShape).clickable(onClick = onViewProfile),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.weight(1f).clickable(onClick = onViewProfile)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(partner.fullName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Icon(Icons.Default.Verified, contentDescription = null, tint = VerifiedGreen, modifier = Modifier.size(16.dp))
                    }
                    Text("Goth: ${partner.clan} • ${partner.city}", fontSize = 12.sp, color = TextSecondaryLight)
                    Text("Direct communication authorized", fontSize = 11.sp, color = VerifiedGreen, fontWeight = FontWeight.Medium)
                }
            }

            Button(
                onClick = onChatClick,
                modifier = Modifier.fillMaxWidth().height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaroonPrimary)
            ) {
                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Open Secure Chat", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ShortlistedCard(
    profile: UserProfileEntity,
    onRemove: () -> Unit,
    onViewProfile: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onViewProfile),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = IvorySurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = profile.photoUrl,
                contentDescription = null,
                modifier = Modifier.size(52.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(profile.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("${profile.age} yrs • Goth: ${profile.clan}", fontSize = 12.sp, color = TextSecondaryLight)
                Text(profile.occupation, fontSize = 11.sp, color = MaroonLight)
            }

            IconButton(onClick = onRemove) {
                Icon(Icons.Default.BookmarkRemove, contentDescription = "Remove shortlist", tint = GoldDark)
            }
        }
    }
}

@Composable
fun EmptyTabState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GoldDark,
                modifier = Modifier.size(54.dp)
            )
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimaryLight)
            Text(
                subtitle,
                fontSize = 12.sp,
                color = TextSecondaryLight,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
