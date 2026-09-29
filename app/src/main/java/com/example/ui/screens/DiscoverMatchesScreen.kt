package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.data.recommendation.RecommendationItem
import com.example.ui.MainViewModel
import com.example.ui.RecommendationFilter
import com.example.ui.components.ProfileCard
import com.example.ui.components.SendInterestDialog
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverMatchesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val profiles by viewModel.allDiscoverable.collectAsState()
    val recommendations by viewModel.recommendedProfiles.collectAsState()
    val recommendationFilter by viewModel.recommendationFilter.collectAsState()
    val shortlists by viewModel.shortlists.collectAsState()
    val blockedUsers by viewModel.blockedUsers.collectAsState()
    val blockedIds = remember(blockedUsers) { blockedUsers.map { it.blockedProfileId }.toSet() }

    var selectedClanFilter by remember { mutableStateOf("All") }
    var interestDialogProfile by remember { mutableStateOf<UserProfileEntity?>(null) }

    val filteredProfiles = remember(profiles, blockedIds, selectedClanFilter) {
        profiles.filter { profile ->
            !blockedIds.contains(profile.id) &&
                    (selectedClanFilter == "All" || profile.clan.equals(selectedClanFilter, ignoreCase = true))
        }
    }

    val shortlistedIds = remember(shortlists) { shortlists.map { it.targetProfileId }.toSet() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("discover_matches_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Hero Cultural Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaroonPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.banjara_hero_banner),
                        contentDescription = "Banjara Matrimony Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        contentScale = ContentScale.Crop
                    )

                    // Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xEE7E152F),
                                        Color(0x887E152F),
                                        Color(0x66000000)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterStart)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GoldAccent
                        ) {
                            Text(
                                text = "COMMUNITY FIRST",
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Find Your Banjara Life Partner",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "100% Verified Profiles • Goth/Clan Matching • Privacy Assured",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = IvoryBackground,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Recommendation Engine Section (Shared Location & Professional Interests)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = GoldDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Recommended For You",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaroonPrimary
                            )
                        )
                    }

                    Surface(shape = RoundedCornerShape(12.dp), color = GoldContainer) {
                        Text(
                            text = "${recommendations.size} Matches",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Text(
                    text = "Smart suggestions based on shared location or compatible career fields from the Room database.",
                    fontSize = 12.sp,
                    color = TextSecondaryLight
                )

                // Recommendation Filter Toggle Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = recommendationFilter == RecommendationFilter.ALL_RECOMMENDED,
                        onClick = { viewModel.setRecommendationFilter(RecommendationFilter.ALL_RECOMMENDED) },
                        label = { Text("⭐ All Matches", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaroonPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = IvorySurface
                        )
                    )
                    FilterChip(
                        selected = recommendationFilter == RecommendationFilter.SHARED_LOCATION,
                        onClick = { viewModel.setRecommendationFilter(RecommendationFilter.SHARED_LOCATION) },
                        label = { Text("📍 Shared Location", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaroonPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = IvorySurface
                        )
                    )
                    FilterChip(
                        selected = recommendationFilter == RecommendationFilter.SHARED_PROFESSION,
                        onClick = { viewModel.setRecommendationFilter(RecommendationFilter.SHARED_PROFESSION) },
                        label = { Text("💼 Career Match", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaroonPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = IvorySurface
                        )
                    )
                }

                if (recommendations.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = IvorySurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaroonLight)
                            Text("No current recommendations under this filter. Tap 'All Matches' to explore.", fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("recommended_profiles_carousel")
                    ) {
                        items(recommendations, key = { it.profile.id }) { recItem ->
                            RecommendationCard(
                                recommendation = recItem,
                                isShortlisted = shortlistedIds.contains(recItem.profile.id),
                                onCardClick = { viewModel.openProfileDetail(recItem.profile) },
                                onFavoriteClick = { viewModel.toggleFavorite(recItem.profile.id) },
                                onSendInterestClick = { interestDialogProfile = recItem.profile }
                            )
                        }
                    }
                }
            }
        }

        // Clan Filter Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Browse All Profiles by Clan (Goth)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaroonPrimary
                        )
                    )
                    Text(
                        text = "${filteredProfiles.size} Profiles",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TextSecondaryLight
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Rathod", "Pawar", "Chauhan", "Jadhav", "Vadtya").forEach { clan ->
                        FilterChip(
                            selected = selectedClanFilter == clan,
                            onClick = { selectedClanFilter = clan },
                            label = { Text(clan, fontWeight = FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaroonPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = IvorySurface
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedClanFilter == clan,
                                selectedBorderColor = GoldLight,
                                borderColor = BorderLight
                            )
                        )
                    }
                }
            }
        }

        // Profile Cards
        if (filteredProfiles.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = IvorySurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = MaroonPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No profiles found in $selectedClanFilter clan",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight
                        )
                        Text(
                            text = "Try switching the clan filter to 'All' or adjusting your partner preferences.",
                            fontSize = 12.sp,
                            color = TextSecondaryLight
                        )
                    }
                }
            }
        } else {
            items(filteredProfiles, key = { it.id }) { profile ->
                ProfileCard(
                    profile = profile,
                    isShortlisted = shortlistedIds.contains(profile.id),
                    onCardClick = { viewModel.openProfileDetail(profile) },
                    onShortlistClick = { viewModel.toggleShortlist(profile.id) },
                    onSendInterestClick = { interestDialogProfile = profile },
                    onFavoriteClick = { viewModel.toggleFavorite(profile.id) }
                )
            }
        }
    }

    interestDialogProfile?.let { profile ->
        SendInterestDialog(
            profileName = profile.fullName,
            onDismiss = { interestDialogProfile = null },
            onSend = { note ->
                viewModel.sendInterest(profile.id, note)
                interestDialogProfile = null
            }
        )
    }
}

@Composable
fun RecommendationCard(
    recommendation: RecommendationItem,
    isShortlisted: Boolean,
    onCardClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onSendInterestClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile = recommendation.profile

    Card(
        modifier = modifier
            .width(260.dp)
            .clickable(onClick = onCardClick)
            .testTag("recommendation_card_${profile.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = IvorySurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                AsyncImage(
                    model = profile.photoUrl,
                    contentDescription = profile.fullName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Match score badge
                Surface(
                    shape = RoundedCornerShape(bottomEnd = 10.dp),
                    color = MaroonPrimary,
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "${recommendation.matchScore}% Match",
                        color = GoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Favorite Button
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .background(Color(0x88000000), CircleShape)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = if (profile.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (profile.isFavorite) CrimsonRed else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Bottom badge on image
                val firstBadge = recommendation.badges.firstOrNull() ?: "📍 ${profile.city}"
                Surface(
                    shape = RoundedCornerShape(topStart = 8.dp),
                    color = Color(0xCC000000),
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = firstBadge,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${profile.name.take(16)}, ${profile.age}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimaryLight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (profile.isIdentityVerified) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = VerifiedGreen,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Text(
                    text = profile.occupation,
                    fontSize = 11.sp,
                    color = MaroonLight,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Goth: ${profile.clan} • ${profile.city.ifBlank { profile.location }}",
                    fontSize = 11.sp,
                    color = TextSecondaryLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = onSendInterestClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaroonPrimary),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Express Interest", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
