package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Female") }
    var birthYearStr by remember { mutableStateOf("1999") }
    var birthMonthStr by remember { mutableStateOf("6") }
    var birthDayStr by remember { mutableStateOf("15") }

    val clans = listOf("Rathod", "Pawar", "Chauhan", "Jadhav", "Vadtya", "Banoth", "Dharawat", "Kumpawat")
    var selectedClan by remember { mutableStateOf(clans[0]) }
    var subClan by remember { mutableStateOf("") }
    var tanda by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Hyderabad") }
    var state by remember { mutableStateOf("Telangana") }
    var education by remember { mutableStateOf("") }
    var occupation by remember { mutableStateOf("") }
    var annualIncome by remember { mutableStateOf("₹15 - 20 LPA") }
    var diet by remember { mutableStateOf("Vegetarian") }
    var height by remember { mutableStateOf("5 ft 5 in") }
    var aboutMe by remember { mutableStateOf("") }
    var partnerPreferences by remember { mutableStateOf("") }
    var consentAgreed by remember { mutableStateOf(false) }

    val birthYear = birthYearStr.toIntOrNull() ?: 2000
    val calculatedAge = 2026 - birthYear
    val isUnderage = calculatedAge < 18

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Create Matrimonial Profile", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Adult Matrimonial Verification Gate", fontSize = 11.sp, color = GoldDark)
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .testTag("registration_form"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Mandatory Adult Age Gate Banner
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUnderage) SafetyRedContainer else IvorySurfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (isUnderage) Icons.Default.Cancel else Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = if (isUnderage) SafetyRed else VerifiedGreen
                    )
                    Column {
                        Text(
                            text = if (isUnderage) "REGISTRATION BLOCKED: Age < 18 Years" else "Age Gate Status: Compliant ($calculatedAge yrs)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isUnderage) SafetyRed else VerifiedGreen
                        )
                        Text(
                            text = if (isUnderage)
                                "Under Indian law (POCSO & Marriage laws) and platform policy, individuals under 18 years of age are strictly prohibited from creating a profile."
                            else "Minimum age requirement of 18 years verified based on declared birth year.",
                            fontSize = 11.sp,
                            color = TextSecondaryLight
                        )
                    }
                }
            }

            // Basic Info
            Text("1. Personal & Identity Details", fontWeight = FontWeight.Bold, color = MaroonPrimary)

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name *") },
                modifier = Modifier.fillMaxWidth().testTag("reg_full_name"),
                placeholder = { Text("e.g. Dr. Rajesh Rathod") }
            )

            // Gender
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Profile For:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                listOf("Bride (Female)" to "Female", "Groom (Male)" to "Male").forEach { (label, value) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        RadioButton(
                            selected = gender == value,
                            onClick = { gender = value },
                            colors = RadioButtonDefaults.colors(selectedColor = MaroonPrimary)
                        )
                        Text(label, fontSize = 12.sp)
                    }
                }
            }

            // Date of Birth
            Text("Date of Birth (Adult Gate Verification) *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = birthDayStr,
                    onValueChange = { if (it.length <= 2) birthDayStr = it },
                    label = { Text("Day") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = birthMonthStr,
                    onValueChange = { if (it.length <= 2) birthMonthStr = it },
                    label = { Text("Month") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = birthYearStr,
                    onValueChange = { if (it.length <= 4) birthYearStr = it },
                    label = { Text("Year") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1.4f).testTag("reg_birth_year")
                )
            }

            // Clan & Roots
            Text("2. Banjara Community & Roots", fontWeight = FontWeight.Bold, color = MaroonPrimary)

            Text("Select Clan (Goth):", fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                clans.take(4).forEach { clan ->
                    FilterChip(
                        selected = selectedClan == clan,
                        onClick = { selectedClan = clan },
                        label = { Text(clan, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaroonPrimary, selectedLabelColor = androidx.compose.ui.graphics.Color.White)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                clans.drop(4).forEach { clan ->
                    FilterChip(
                        selected = selectedClan == clan,
                        onClick = { selectedClan = clan },
                        label = { Text(clan, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaroonPrimary, selectedLabelColor = androidx.compose.ui.graphics.Color.White)
                    )
                }
            }

            OutlinedTextField(
                value = subClan,
                onValueChange = { subClan = it },
                label = { Text("Sub-Clan / Peda (e.g. Bhukya, Jarpla, Kramot)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tanda,
                onValueChange = { tanda = it },
                label = { Text("Native Tanda / Village *") },
                placeholder = { Text("e.g. Sevalal Tanda, Nalgonda") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Current City") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state,
                    onValueChange = { state = it },
                    label = { Text("State") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Education & Profession
            Text("3. Education & Career", fontWeight = FontWeight.Bold, color = MaroonPrimary)

            OutlinedTextField(
                value = education,
                onValueChange = { education = it },
                label = { Text("Highest Qualification *") },
                placeholder = { Text("e.g. B.Tech (CSE) / MBBS / MBA") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = occupation,
                onValueChange = { occupation = it },
                label = { Text("Profession / Job Title *") },
                placeholder = { Text("e.g. Software Engineer / Doctor / Govt Officer") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = annualIncome,
                onValueChange = { annualIncome = it },
                label = { Text("Annual Income (approx)") },
                modifier = Modifier.fillMaxWidth()
            )

            // About & Partner Preferences
            Text("4. About & Partner Preferences", fontWeight = FontWeight.Bold, color = MaroonPrimary)

            OutlinedTextField(
                value = aboutMe,
                onValueChange = { aboutMe = it },
                label = { Text("Brief Biodata / Cultural Values") },
                placeholder = { Text("Share about your family background and expectations...") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                maxLines = 4
            )

            OutlinedTextField(
                value = partnerPreferences,
                onValueChange = { partnerPreferences = it },
                label = { Text("Partner Preferences (Goth, Age, Education)") },
                placeholder = { Text("e.g. Pawar or Chauhan clan, Graduate, 24-28 yrs...") },
                modifier = Modifier.fillMaxWidth()
            )

            // DPDP Consent Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = consentAgreed,
                    onCheckedChange = { consentAgreed = it },
                    colors = CheckboxDefaults.colors(checkedColor = MaroonPrimary),
                    modifier = Modifier.testTag("reg_consent_checkbox")
                )
                Column {
                    Text(
                        text = "I affirm that I am 18 years of age or older and consent to processing my matrimonial details under the Digital Personal Data Protection (DPDP) Act 2023.",
                        fontSize = 11.sp,
                        color = TextPrimaryLight,
                        lineHeight = 16.sp
                    )
                }
            }

            // Submit Button
            Button(
                onClick = {
                    viewModel.registerNewUser(
                        fullName = fullName.ifBlank { "Registered Banjara Member" },
                        gender = gender,
                        birthYear = birthYear,
                        birthMonth = birthMonthStr.toIntOrNull() ?: 1,
                        birthDay = birthDayStr.toIntOrNull() ?: 1,
                        clan = selectedClan,
                        subClan = subClan,
                        tanda = tanda.ifBlank { "Sevalal Nagar" },
                        city = city,
                        state = state,
                        education = education.ifBlank { "Graduate" },
                        occupation = occupation.ifBlank { "Professional" },
                        annualIncome = annualIncome,
                        diet = diet,
                        height = height,
                        aboutMe = aboutMe.ifBlank { "Traditional Banjara family looking for suitable alliance." },
                        partnerPreferences = partnerPreferences.ifBlank { "Cultured, family-oriented life partner." },
                        onSuccess = {}
                    )
                },
                enabled = !isUnderage && consentAgreed && fullName.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_registration_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = MaroonPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Verified, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Complete 18+ Verification & Publish Profile", fontWeight = FontWeight.Bold)
            }
        }
    }
}
