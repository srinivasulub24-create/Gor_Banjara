package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserProfileEntity
import com.example.ui.MainViewModel
import com.example.ui.SearchFilter
import com.example.ui.components.ProfileCard
import com.example.ui.components.SendInterestDialog
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    // Room database reactive query results
    val searchResults by viewModel.searchResults.collectAsState()
    val filter by viewModel.searchFilter.collectAsState()
    val shortlists by viewModel.shortlists.collectAsState()
    val availableLocations by viewModel.availableLocations.collectAsState()
    val availableOccupations by viewModel.availableOccupations.collectAsState()

    val shortlistedIds = remember(shortlists) { shortlists.map { it.targetProfileId }.toSet() }

    var interestDialogProfile by remember { mutableStateOf<UserProfileEntity?>(null) }
    var showFilterSheet by remember { mutableStateOf(false) }

    // Popular default locations & occupations combined with database distinct values
    val locationOptions = remember(availableLocations) {
        (listOf("All", "Hyderabad", "Bengaluru", "Pune", "Mumbai", "Warangal", "Bellary") + availableLocations)
            .distinct()
            .take(12)
    }

    val occupationOptions = remember(availableOccupations) {
        listOf("All", "Software", "Doctor", "Engineer", "Govt", "Architect", "Finance")
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("search_screen_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search Bar with Filter Icon
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = filter.query,
                    onValueChange = { viewModel.updateSearchFilter(filter.copy(query = it)) },
                    placeholder = { Text("Search location, occupation, clan, biodata...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaroonPrimary) },
                    trailingIcon = {
                        if (filter.query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchFilter(filter.copy(query = "")) }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = TextSecondaryLight)
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_query_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaroonPrimary,
                        unfocusedBorderColor = BorderLight,
                        focusedContainerColor = IvorySurface,
                        unfocusedContainerColor = IvorySurface
                    ),
                    singleLine = true
                )

                FilledIconButton(
                    onClick = { showFilterSheet = true },
                    modifier = Modifier
                        .size(50.dp)
                        .testTag("open_filters_btn"),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaroonPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    BadgedBox(
                        badge = {
                            val activeFilterCount = (if (filter.location != "All") 1 else 0) +
                                    (if (filter.occupation != "All") 1 else 0) +
                                    (if (filter.clan != "All") 1 else 0) +
                                    (if (filter.verifiedOnly) 1 else 0)
                            if (activeFilterCount > 0) {
                                Badge(containerColor = GoldAccent) {
                                    Text("$activeFilterCount", color = Color.Black)
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = "Filters", tint = Color.White)
                    }
                }
            }
        }

        // Location Filter Chips Row (Scalable Room Database Filter)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaroonPrimary, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Filter by Location",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaroonPrimary
                    )
                    if (filter.location != "All") {
                        Text(
                            text = "(${filter.location})",
                            fontSize = 11.sp,
                            color = GoldDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    locationOptions.forEach { loc ->
                        FilterChip(
                            selected = filter.location == loc,
                            onClick = { viewModel.updateSearchFilter(filter.copy(location = loc)) },
                            label = { Text(loc, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaroonPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = IvorySurface
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = filter.location == loc,
                                selectedBorderColor = GoldLight,
                                borderColor = BorderLight
                            )
                        )
                    }
                }
            }
        }

        // Occupation Filter Chips Row (Scalable Room Database Filter)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Work, contentDescription = null, tint = MaroonPrimary, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Filter by Occupation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaroonPrimary
                    )
                    if (filter.occupation != "All") {
                        Text(
                            text = "(${filter.occupation})",
                            fontSize = 11.sp,
                            color = GoldDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    occupationOptions.forEach { occ ->
                        FilterChip(
                            selected = filter.occupation == occ,
                            onClick = { viewModel.updateSearchFilter(filter.copy(occupation = occ)) },
                            label = { Text(occ, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaroonPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = IvorySurface
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = filter.occupation == occ,
                                selectedBorderColor = GoldLight,
                                borderColor = BorderLight
                            )
                        )
                    }
                }
            }
        }

        // Active Filter Summary & Result Count
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "${searchResults.size} Profiles Found",
                        fontWeight = FontWeight.Bold,
                        color = MaroonPrimary
                    )
                    Surface(shape = RoundedCornerShape(4.dp), color = GoldContainer) {
                        Text(
                            text = "Room DB Indexed",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (filter.clan != "All" || filter.location != "All" || filter.occupation != "All" || filter.verifiedOnly || filter.query.isNotEmpty()) {
                    TextButton(
                        onClick = { viewModel.updateSearchFilter(SearchFilter()) },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Reset All", color = MaroonLight, fontSize = 12.sp)
                    }
                }
            }
        }

        if (searchResults.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
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
                        Icon(Icons.Default.SearchOff, contentDescription = null, tint = MaroonPrimary, modifier = Modifier.size(44.dp))
                        Text("No matching profiles found in database", fontWeight = FontWeight.Bold)
                        Text(
                            "Try relaxing your location (${filter.location}) or occupation (${filter.occupation}) filters.",
                            fontSize = 12.sp,
                            color = TextSecondaryLight
                        )
                        OutlinedButton(
                            onClick = { viewModel.updateSearchFilter(SearchFilter()) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Clear Filters", color = MaroonPrimary)
                        }
                    }
                }
            }
        } else {
            items(searchResults, key = { it.id }) { profile ->
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

    // Advanced Filter Modal Bottom Sheet (Location, Occupation, Clan, Age Range, Sorting)
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = IvorySurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Advanced Matrimonial Filters",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaroonPrimary)
                    )
                    TextButton(onClick = { viewModel.updateSearchFilter(SearchFilter()) }) {
                        Text("Reset All", color = MaroonLight)
                    }
                }

                // Custom Location Filter Input
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Target Location (City, State, or Native Tanda)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    OutlinedTextField(
                        value = if (filter.location == "All") "" else filter.location,
                        onValueChange = { viewModel.updateSearchFilter(filter.copy(location = it.ifBlank { "All" })) },
                        placeholder = { Text("e.g. Hyderabad, Bengaluru, Pune, Nalgonda...") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaroonPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Custom Occupation Filter Input
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Target Profession / Occupation", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    OutlinedTextField(
                        value = if (filter.occupation == "All") "" else filter.occupation,
                        onValueChange = { viewModel.updateSearchFilter(filter.copy(occupation = it.ifBlank { "All" })) },
                        placeholder = { Text("e.g. Software, Doctor, Civil Services, Architect...") },
                        leadingIcon = { Icon(Icons.Default.Work, contentDescription = null, tint = MaroonPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Clan Filter
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Banjara Clan (Goth)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Rathod", "Pawar", "Chauhan", "Jadhav").forEach { c ->
                            FilterChip(
                                selected = filter.clan == c,
                                onClick = { viewModel.updateSearchFilter(filter.copy(clan = c)) },
                                label = { Text(c, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaroonPrimary, selectedLabelColor = Color.White)
                            )
                        }
                    }
                }

                // Sort By Order
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Sort Results By", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("NEWEST" to "Newest", "AGE_ASC" to "Age (Low)", "AGE_DESC" to "Age (High)", "NAME" to "Name").forEach { (key, label) ->
                            FilterChip(
                                selected = filter.sortBy == key,
                                onClick = { viewModel.updateSearchFilter(filter.copy(sortBy = key)) },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaroonPrimary, selectedLabelColor = Color.White)
                            )
                        }
                    }
                }

                // Age Range Slider
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Age: ${filter.minAge} to ${filter.maxAge} years", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    RangeSlider(
                        value = filter.minAge.toFloat()..filter.maxAge.toFloat(),
                        onValueChange = { range ->
                            viewModel.updateSearchFilter(filter.copy(minAge = range.start.toInt(), maxAge = range.endInclusive.toInt()))
                        },
                        valueRange = 18f..50f,
                        colors = SliderDefaults.colors(thumbColor = MaroonPrimary, activeTrackColor = MaroonPrimary)
                    )
                }

                // Verified Only Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Identity Verified Only", fontWeight = FontWeight.Medium)
                        Text("Show only profiles with verified Aadhaar/Govt ID", fontSize = 11.sp, color = TextSecondaryLight)
                    }
                    Switch(
                        checked = filter.verifiedOnly,
                        onCheckedChange = { viewModel.updateSearchFilter(filter.copy(verifiedOnly = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = MaroonPrimary, checkedTrackColor = MaroonContainer)
                    )
                }

                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaroonPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Apply Database Filters (${searchResults.size} Matches)", fontWeight = FontWeight.Bold)
                }
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
