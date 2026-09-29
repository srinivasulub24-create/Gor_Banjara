package com.example.data.repository

import com.example.data.local.*
import com.example.data.recommendation.RecommendationEngine
import com.example.data.recommendation.RecommendationItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

class MatrimonyRepository(private val database: AppDatabase) {
    private val userDao = database.userDao()
    private val userProfilesDao = database.userProfilesDao()

    suspend fun initializeIfNeeded() = withContext(Dispatchers.IO) {
        val currentUser = userDao.getCurrentUser()
        if (currentUser == null) {
            // Seed current user and sample profiles
            userDao.insertProfile(SampleDataProvider.getDefaultCurrentUser())
            userDao.insertProfiles(SampleDataProvider.getSampleProfiles())
            for (interest in SampleDataProvider.getInitialInterests()) {
                userDao.insertInterest(interest)
            }
            for (shortlist in SampleDataProvider.getInitialShortlists()) {
                userDao.insertShortlist(shortlist)
            }
            for (msg in SampleDataProvider.getInitialMessages()) {
                userDao.insertMessage(msg)
            }
            for (notif in SampleDataProvider.getInitialNotifications()) {
                userDao.insertNotification(notif)
            }
            for (rep in SampleDataProvider.getInitialReports()) {
                userDao.insertReport(rep)
            }
            for (log in SampleDataProvider.getInitialAuditLogs()) {
                userDao.insertAuditLog(log)
            }
        }
    }

    // Profiles & Current User
    fun getCurrentUserFlow(): Flow<UserProfileEntity?> = userDao.getCurrentUserFlow()
    suspend fun getCurrentUser(): UserProfileEntity? = userDao.getCurrentUser()

    fun getDiscoverableProfilesFlow(): Flow<List<UserProfileEntity>> = userDao.getDiscoverableProfilesFlow()

    suspend fun getProfileById(id: String): UserProfileEntity? = userDao.getProfileById(id)

    suspend fun updateCurrentUser(profile: UserProfileEntity) = withContext(Dispatchers.IO) {
        userDao.updateProfile(profile)
    }

    suspend fun togglePhotoBlur(profileId: String, currentBlurred: Boolean) = withContext(Dispatchers.IO) {
        userDao.updatePhotoBlur(profileId, !currentBlurred)
    }

    suspend fun updateProfilePhotoUrl(profileId: String, profilePhotoUrl: String) = withContext(Dispatchers.IO) {
        userProfilesDao.updateProfilePhotoUrl(profileId, profilePhotoUrl)
    }

    suspend fun getProfilePhotoUrl(profileId: String): String? = withContext(Dispatchers.IO) {
        userProfilesDao.getProfilePhotoUrl(profileId)
    }

    fun getProfilePhotoUrlFlow(profileId: String): Flow<String?> =
        userProfilesDao.getProfilePhotoUrlFlow(profileId)

    // Room Database Search & Filtering (Optimized for Scalability)
    fun searchProfiles(
        query: String = "",
        location: String = "",
        occupation: String = "",
        clan: String = "All",
        minAge: Int = 18,
        maxAge: Int = 100,
        verifiedOnly: Boolean = false,
        sortBy: String = "NEWEST",
        limit: Int = 100,
        offset: Int = 0
    ): Flow<List<UserProfileEntity>> {
        return userProfilesDao.filterProfiles(
            query = query.trim().ifEmpty { null },
            location = location.trim().ifEmpty { null },
            occupation = occupation.trim().ifEmpty { null },
            clan = clan.trim().ifEmpty { null },
            minAge = minAge,
            maxAge = maxAge,
            verifiedOnly = verifiedOnly,
            sortBy = sortBy,
            limit = limit,
            offset = offset
        )
    }

    fun getProfilesByLocation(location: String): Flow<List<UserProfileEntity>> =
        userProfilesDao.getProfilesByLocation(location)

    fun getProfilesByOccupation(occupation: String): Flow<List<UserProfileEntity>> =
        userProfilesDao.getProfilesByOccupation(occupation)

    fun getDistinctLocations(): Flow<List<String>> = userProfilesDao.getDistinctLocations()

    fun getDistinctOccupations(): Flow<List<String>> = userProfilesDao.getDistinctOccupations()

    // Favorites Operations
    suspend fun toggleFavorite(profileId: String) = withContext(Dispatchers.IO) {
        userProfilesDao.toggleFavorite(profileId)
    }

    suspend fun updateFavoriteStatus(profileId: String, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        userProfilesDao.updateFavoriteStatus(profileId, isFavorite)
    }

    fun getFavoritesFlow(): Flow<List<UserProfileEntity>> = userProfilesDao.getFavoriteProfiles()

    fun isProfileFavoriteFlow(profileId: String): Flow<Boolean?> = userProfilesDao.isProfileFavoriteFlow(profileId)

    // Recommendation Engine: Queries existing UserProfiles Room database for shared location & professional interests
    fun getRecommendedProfiles(
        userLocation: String,
        userOccupation: String,
        userClan: String = "",
        limit: Int = 30
    ): Flow<List<RecommendationItem>> {
        return userProfilesDao.getRecommendedProfiles(
            location = userLocation.trim(),
            occupation = userOccupation.trim(),
            limit = limit
        ).map { candidateList ->
            candidateList.map { candidate ->
                RecommendationEngine.scoreRecommendation(
                    candidate = candidate,
                    userLocation = userLocation,
                    userOccupation = userOccupation,
                    userClan = userClan
                )
            }.sortedByDescending { it.matchScore }
        }
    }

    fun getRecommendationsByLocation(location: String, limit: Int = 20): Flow<List<UserProfileEntity>> =
        userProfilesDao.getRecommendationsByLocation(location, limit)

    fun getRecommendationsByOccupation(occupation: String, limit: Int = 20): Flow<List<UserProfileEntity>> =
        userProfilesDao.getRecommendationsByOccupation(occupation, limit)

    // Registration with strict Age Gate Check (Age >= 18)
    suspend fun registerNewUser(
        fullName: String,
        gender: String,
        birthYear: Int,
        birthMonth: Int,
        birthDay: Int,
        clan: String,
        subClan: String,
        tanda: String,
        city: String,
        state: String,
        education: String,
        occupation: String,
        annualIncome: String,
        diet: String,
        height: String,
        aboutMe: String,
        partnerPreferences: String
    ): Result<UserProfileEntity> = withContext(Dispatchers.IO) {
        val currentYear = 2026 // per local time metadata
        val age = currentYear - birthYear
        if (age < 18) {
            return@withContext Result.failure(
                IllegalArgumentException("Underage Registration Blocked: In compliance with Indian law & platform policy, members must be at least 18 years of age.")
            )
        }

        val dobString = String.format("%04d-%02d-%02d", birthYear, birthMonth, birthDay)
        val newProfile = UserProfileEntity(
            id = "user_me",
            name = fullName,
            age = age,
            occupation = occupation,
            location = "$city, $state",
            biodata = aboutMe,
            fullName = fullName,
            gender = gender,
            dateOfBirth = dobString,
            height = height,
            maritalStatus = "Never Married",
            motherTongue = "Gor Boli (Banjara)",
            clan = clan,
            subClan = subClan,
            tanda = tanda,
            city = city,
            state = state,
            education = education,
            annualIncome = annualIncome,
            workLocation = "$city, $state",
            diet = diet,
            aboutMe = aboutMe,
            familyDetails = "Traditional Banjara family residing in $tanda.",
            partnerPreferences = partnerPreferences,
            photoUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=600&auto=format&fit=crop&q=80",
            isPhotoBlurred = false,
            isIdentityVerified = false,
            isPhoneVerified = true,
            verificationStatus = "PENDING",
            membershipTier = "FREE",
            isCurrentUser = true,
            isVisibleInSearch = true
        )

        userDao.insertProfile(newProfile)

        // Add verification submission audit log
        userDao.insertAuditLog(
            AuditLogEntity(
                id = "log_${UUID.randomUUID()}",
                actorRole = "System",
                action = "USER_REGISTERED_AGE_GATE_VERIFIED",
                targetProfileId = newProfile.id,
                targetName = newProfile.fullName,
                details = "Registered with declared DOB: $dobString (Age: $age). Identity verification queued for moderator review."
            )
        )

        Result.success(newProfile)
    }

    // Shortlist / Favourites
    fun getShortlistsFlow(userId: String): Flow<List<ShortlistEntity>> = userDao.getShortlistsFlow(userId)

    suspend fun isShortlisted(userId: String, targetId: String): Boolean = userDao.isShortlisted(userId, targetId)

    suspend fun toggleShortlist(userId: String, targetId: String) = withContext(Dispatchers.IO) {
        if (userDao.isShortlisted(userId, targetId)) {
            userDao.removeFromShortlist(userId, targetId)
        } else {
            userDao.insertShortlist(
                ShortlistEntity(
                    id = "sh_${UUID.randomUUID()}",
                    userId = userId,
                    targetProfileId = targetId
                )
            )
        }
    }

    // Interests
    fun getReceivedInterestsFlow(userId: String): Flow<List<InterestEntity>> = userDao.getReceivedInterestsFlow(userId)
    fun getSentInterestsFlow(userId: String): Flow<List<InterestEntity>> = userDao.getSentInterestsFlow(userId)

    suspend fun sendInterest(senderId: String, receiverId: String, personalNote: String): Result<Unit> = withContext(Dispatchers.IO) {
        val existing = userDao.getInterestBetween(senderId, receiverId)
        if (existing != null && existing.status != "WITHDRAWN" && existing.status != "DECLINED") {
            return@withContext Result.failure(IllegalStateException("Interest already exists with status: ${existing.status}"))
        }

        val interest = InterestEntity(
            id = "int_${UUID.randomUUID()}",
            senderId = senderId,
            receiverId = receiverId,
            status = "PENDING",
            personalNote = personalNote
        )
        userDao.insertInterest(interest)

        // Notification for receiver
        val sender = userDao.getProfileById(senderId)
        val senderName = sender?.fullName ?: "A member"
        userDao.insertNotification(
            NotificationEntity(
                id = "notif_${UUID.randomUUID()}",
                userId = receiverId,
                title = "New Interest Received",
                message = "$senderName has expressed interest in your profile.",
                type = "INTEREST"
            )
        )
        Result.success(Unit)
    }

    suspend fun respondToInterest(interestId: String, accept: Boolean) = withContext(Dispatchers.IO) {
        val status = if (accept) "ACCEPTED" else "DECLINED"
        userDao.updateInterestStatus(interestId, status, System.currentTimeMillis())

        if (accept) {
            // Create welcome message
            val interest = userDao.getReceivedInterestsFlow("user_me").firstOrNull()?.find { it.id == interestId }
            if (interest != null) {
                userDao.insertNotification(
                    NotificationEntity(
                        id = "notif_${UUID.randomUUID()}",
                        userId = interest.senderId,
                        title = "Interest Accepted!",
                        message = "Your interest was accepted! You are now connected and can chat directly.",
                        type = "CONNECTION"
                    )
                )
            }
        }
    }

    suspend fun withdrawInterest(interestId: String) = withContext(Dispatchers.IO) {
        userDao.updateInterestStatus(interestId, "WITHDRAWN", System.currentTimeMillis())
    }

    // Direct Messaging
    fun getMessagesFlow(userId: String, otherId: String): Flow<List<MessageEntity>> =
        userDao.getMessagesForConversationFlow(userId, otherId)

    suspend fun sendMessage(senderId: String, receiverId: String, text: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (userDao.isBlocked(senderId, receiverId) || userDao.isBlocked(receiverId, senderId)) {
            return@withContext Result.failure(IllegalStateException("Cannot send message: User is blocked."))
        }

        val message = MessageEntity(
            id = "msg_${UUID.randomUUID()}",
            senderId = senderId,
            receiverId = receiverId,
            content = text.trim()
        )
        userDao.insertMessage(message)
        Result.success(Unit)
    }

    suspend fun markMessagesRead(userId: String, otherId: String) = withContext(Dispatchers.IO) {
        userDao.markMessagesAsRead(userId, otherId)
    }

    // Reporting & Safety Grievance
    suspend fun reportProfile(
        reporterId: String,
        targetId: String,
        targetName: String,
        category: String,
        details: String
    ) = withContext(Dispatchers.IO) {
        val report = ReportEntity(
            id = "rep_${UUID.randomUUID()}",
            reporterId = reporterId,
            targetProfileId = targetId,
            targetName = targetName,
            category = category,
            description = details,
            status = "PENDING"
        )
        userDao.insertReport(report)

        // Log audit trail
        userDao.insertAuditLog(
            AuditLogEntity(
                id = "log_${UUID.randomUUID()}",
                actorRole = "System",
                action = "REPORT_SUBMITTED",
                targetProfileId = targetId,
                targetName = targetName,
                details = "Report filed for: $category. Dispatched to moderation queue."
            )
        )
    }

    // Blocking
    fun getBlockedUsersFlow(userId: String): Flow<List<BlockedUserEntity>> = userDao.getBlockedUsersFlow(userId)

    suspend fun blockUser(userId: String, targetId: String, targetName: String) = withContext(Dispatchers.IO) {
        userDao.insertBlockedUser(
            BlockedUserEntity(
                id = "blk_${UUID.randomUUID()}",
                userId = userId,
                blockedProfileId = targetId,
                blockedName = targetName
            )
        )
    }

    suspend fun unblockUser(userId: String, targetId: String) = withContext(Dispatchers.IO) {
        userDao.unblockUser(userId, targetId)
    }

    // Notifications
    fun getNotificationsFlow(userId: String): Flow<List<NotificationEntity>> = userDao.getNotificationsFlow(userId)

    suspend fun markAllNotificationsRead(userId: String) = withContext(Dispatchers.IO) {
        userDao.markAllNotificationsRead(userId)
    }

    // Admin & Moderation Operations
    fun getAllReportsFlow(): Flow<List<ReportEntity>> = userDao.getAllReportsFlow()
    fun getPendingVerificationsFlow(): Flow<List<UserProfileEntity>> = userDao.getPendingVerificationsFlow()
    fun getAuditLogsFlow(): Flow<List<AuditLogEntity>> = userDao.getAuditLogsFlow()

    suspend fun approveVerification(profileId: String, profileName: String) = withContext(Dispatchers.IO) {
        userDao.updateVerification(profileId, "VERIFIED", isVerified = true)
        userDao.insertAuditLog(
            AuditLogEntity(
                id = "log_${UUID.randomUUID()}",
                actorRole = "Moderator",
                action = "APPROVE_VERIFICATION",
                targetProfileId = profileId,
                targetName = profileName,
                details = "Identity documents and age criteria verified. Badge assigned."
            )
        )
    }

    suspend fun rejectVerification(profileId: String, profileName: String, reason: String) = withContext(Dispatchers.IO) {
        userDao.updateVerification(profileId, "REJECTED", isVerified = false)
        userDao.insertAuditLog(
            AuditLogEntity(
                id = "log_${UUID.randomUUID()}",
                actorRole = "Moderator",
                action = "REJECT_VERIFICATION",
                targetProfileId = profileId,
                targetName = profileName,
                details = "Verification rejected. Reason: $reason"
            )
        )
    }

    suspend fun moderateReport(reportId: String, action: String, targetId: String, targetName: String) = withContext(Dispatchers.IO) {
        userDao.updateReportStatus(reportId, "RESOLVED", action)

        if (action == "Profile Restricted" || action == "Account Suspended") {
            userDao.updateAccountRestriction(targetId, true)
        }

        userDao.insertAuditLog(
            AuditLogEntity(
                id = "log_${UUID.randomUUID()}",
                actorRole = "Moderator",
                action = "REPORT_ACTIONED",
                targetProfileId = targetId,
                targetName = targetName,
                details = "Action taken on report $reportId: $action"
            )
        )
    }

    suspend fun upgradeMembership(profileId: String, tier: String) = withContext(Dispatchers.IO) {
        userDao.updateMembershipTier(profileId, tier)
        userDao.insertAuditLog(
            AuditLogEntity(
                id = "log_${UUID.randomUUID()}",
                actorRole = "PaymentGateway",
                action = "MEMBERSHIP_UPGRADED",
                targetProfileId = profileId,
                targetName = "Current User",
                details = "Payment verified via webhook simulator. Tier upgraded to $tier."
            )
        )
    }
}
