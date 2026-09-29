package com.example.data.recommendation

import com.example.data.local.UserProfileEntity

data class RecommendationItem(
    val profile: UserProfileEntity,
    val matchScore: Int, // 0 to 100%
    val isLocationMatch: Boolean,
    val isOccupationMatch: Boolean,
    val matchReason: String,
    val badges: List<String>
)

object RecommendationEngine {

    // Professional clusters to match similar occupations
    private val CAREER_CLUSTERS = mapOf(
        "TECH" to listOf("software", "engineer", "developer", "data", "tech", "architect", "it", "programmer", "analyst", "ai", "machine learning"),
        "HEALTHCARE" to listOf("doctor", "pediatrician", "physician", "medical", "surgeon", "dentist", "nurse", "pharma", "clinical"),
        "GOVT_ADMIN" to listOf("govt", "civil services", "officer", "administrative", "ias", "ips", "upsc", "mpsc", "revenue", "police", "inspector"),
        "FINANCE" to listOf("ca", "chartered accountant", "audit", "finance", "banking", "investment", "tax", "accounts", "consultant"),
        "DESIGN_ARCH" to listOf("architect", "interior", "design", "creative", "urban"),
        "ACADEMIA" to listOf("professor", "lecturer", "teacher", "researcher", "phd", "education"),
        "BUSINESS" to listOf("entrepreneur", "business", "founder", "manager", "operations", "marketing")
    )

    fun scoreRecommendation(
        candidate: UserProfileEntity,
        userLocation: String,
        userOccupation: String,
        userClan: String = ""
    ): RecommendationItem {
        var score = 40 // Baseline score for eligible community member
        var isLocationMatch = false
        var isOccupationMatch = false
        val badges = mutableListOf<String>()

        // 1. Location Matching
        val candCity = candidate.city.trim().lowercase()
        val candState = candidate.state.trim().lowercase()
        val candLocation = candidate.location.trim().lowercase()

        val myLocLower = userLocation.trim().lowercase()
        val myCityTokens = myLocLower.split(",", " ", "/").filter { it.length > 2 }

        val exactCityMatch = myCityTokens.any { token ->
            candCity.contains(token) || candLocation.contains(token)
        }

        val stateMatch = (candState.isNotBlank() && myLocLower.contains(candState)) ||
                (candLocation.contains("telangana") && myLocLower.contains("telangana")) ||
                (candLocation.contains("karnataka") && myLocLower.contains("karnataka")) ||
                (candLocation.contains("maharashtra") && myLocLower.contains("maharashtra"))

        if (exactCityMatch) {
            score += 35
            isLocationMatch = true
            badges.add("📍 Same City (${candidate.city.ifBlank { candidate.location }})")
        } else if (stateMatch) {
            score += 20
            isLocationMatch = true
            badges.add("📍 Same Region (${candidate.state})")
        }

        // 2. Professional Interest Matching
        val candOccLower = candidate.occupation.trim().lowercase()
        val myOccLower = userOccupation.trim().lowercase()

        val myClusters = CAREER_CLUSTERS.filter { (_, keywords) ->
            keywords.any { k -> myOccLower.contains(k) }
        }.keys

        val candClusters = CAREER_CLUSTERS.filter { (_, keywords) ->
            keywords.any { k -> candOccLower.contains(k) }
        }.keys

        val sharedClusters = myClusters.intersect(candClusters)
        val directKeywordMatch = myOccLower.split(" ", "/", "@", "-")
            .filter { it.length > 3 }
            .any { token -> candOccLower.contains(token) }

        if (sharedClusters.isNotEmpty() || directKeywordMatch) {
            score += 35
            isOccupationMatch = true
            val clusterName = when (sharedClusters.firstOrNull()) {
                "TECH" -> "Tech & Software"
                "HEALTHCARE" -> "Healthcare & Medicine"
                "GOVT_ADMIN" -> "Civil Services & Administration"
                "FINANCE" -> "Finance & Corporate Audit"
                "DESIGN_ARCH" -> "Architecture & Design"
                "ACADEMIA" -> "Education & Research"
                "BUSINESS" -> "Business & Management"
                else -> "Professional Match"
            }
            badges.add("💼 $clusterName")
        }

        // 3. Cultural Alignment (Banjara Clan / Goth exogamy compatibility)
        if (userClan.isNotBlank() && !candidate.clan.equals(userClan, ignoreCase = true)) {
            score += 5
            badges.add("👑 Compatible Clan (${candidate.clan})")
        }

        // 4. Verification trust bonus
        if (candidate.isIdentityVerified) {
            score += 5
            badges.add("🛡️ Verified Member")
        }

        val clampedScore = score.coerceIn(45, 98)

        val primaryReason = when {
            isLocationMatch && isOccupationMatch ->
                "Top Match: Shared ${candidate.city.ifBlank { candidate.location }} & compatible career in ${candidate.occupation.take(24)}"
            isLocationMatch ->
                "Location Match: Based in ${candidate.city.ifBlank { candidate.location }}"
            isOccupationMatch ->
                "Career Match: Professional in ${candidate.occupation.take(28)}"
            else ->
                "Curated Match: Verified profile in ${candidate.location}"
        }

        return RecommendationItem(
            profile = candidate,
            matchScore = clampedScore,
            isLocationMatch = isLocationMatch,
            isOccupationMatch = isOccupationMatch,
            matchReason = primaryReason,
            badges = badges
        )
    }
}
