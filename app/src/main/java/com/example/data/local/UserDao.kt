package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // User Profiles
    @Query("SELECT * FROM UserProfiles WHERE isCurrentUser = 1 LIMIT 1")
    fun getCurrentUserFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM UserProfiles WHERE isCurrentUser = 1 LIMIT 1")
    suspend fun getCurrentUser(): UserProfileEntity?

    @Query("SELECT * FROM UserProfiles WHERE isCurrentUser = 0 AND isVisibleInSearch = 1 AND isAccountRestricted = 0")
    fun getDiscoverableProfilesFlow(): Flow<List<UserProfileEntity>>

    @Query("SELECT * FROM UserProfiles WHERE id = :profileId LIMIT 1")
    suspend fun getProfileById(profileId: String): UserProfileEntity?

    @Query("SELECT * FROM UserProfiles")
    fun getAllProfilesFlow(): Flow<List<UserProfileEntity>>

    @Query("SELECT * FROM UserProfiles WHERE verificationStatus = 'PENDING'")
    fun getPendingVerificationsFlow(): Flow<List<UserProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<UserProfileEntity>)

    @Update
    suspend fun updateProfile(profile: UserProfileEntity)

    @Query("UPDATE UserProfiles SET verificationStatus = :status, isIdentityVerified = :isVerified WHERE id = :profileId")
    suspend fun updateVerification(profileId: String, status: String, isVerified: Boolean)

    @Query("UPDATE UserProfiles SET isAccountRestricted = :restricted WHERE id = :profileId")
    suspend fun updateAccountRestriction(profileId: String, restricted: Boolean)

    @Query("UPDATE UserProfiles SET isPhotoBlurred = :blurred WHERE id = :profileId")
    suspend fun updatePhotoBlur(profileId: String, blurred: Boolean)

    // Profile Photo URL (Store & Retrieve)
    @Query("UPDATE UserProfiles SET profilePhotoUrl = :profilePhotoUrl, photoUrl = :profilePhotoUrl WHERE id = :id")
    suspend fun updateProfilePhotoUrl(id: String, profilePhotoUrl: String)

    @Query("SELECT profilePhotoUrl FROM UserProfiles WHERE id = :id LIMIT 1")
    suspend fun getProfilePhotoUrl(id: String): String?

    @Query("SELECT profilePhotoUrl FROM UserProfiles WHERE id = :id LIMIT 1")
    fun getProfilePhotoUrlFlow(id: String): Flow<String?>

    @Query("UPDATE UserProfiles SET membershipTier = :tier WHERE id = :profileId")
    suspend fun updateMembershipTier(profileId: String, tier: String)

    // Favorites
    @Query("UPDATE UserProfiles SET isFavorite = CASE WHEN isFavorite = 1 THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun toggleFavorite(id: String)

    @Query("UPDATE UserProfiles SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)

    @Query("SELECT * FROM UserProfiles WHERE isFavorite = 1 AND isVisibleInSearch = 1 AND isAccountRestricted = 0 ORDER BY createdAt DESC")
    fun getFavoriteProfilesFlow(): Flow<List<UserProfileEntity>>

    // Interests
    @Query("SELECT * FROM interests WHERE receiverId = :userId ORDER BY updatedAt DESC")
    fun getReceivedInterestsFlow(userId: String): Flow<List<InterestEntity>>

    @Query("SELECT * FROM interests WHERE senderId = :userId ORDER BY updatedAt DESC")
    fun getSentInterestsFlow(userId: String): Flow<List<InterestEntity>>

    @Query("SELECT * FROM interests WHERE (senderId = :userId1 AND receiverId = :userId2) OR (senderId = :userId2 AND receiverId = :userId1) LIMIT 1")
    suspend fun getInterestBetween(userId1: String, userId2: String): InterestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterest(interest: InterestEntity)

    @Update
    suspend fun updateInterest(interest: InterestEntity)

    @Query("UPDATE interests SET status = :status, updatedAt = :timestamp WHERE id = :interestId")
    suspend fun updateInterestStatus(interestId: String, status: String, timestamp: Long)

    @Query("DELETE FROM interests WHERE id = :interestId")
    suspend fun deleteInterest(interestId: String)

    // Shortlist / Favourites
    @Query("SELECT * FROM shortlists WHERE userId = :userId ORDER BY createdAt DESC")
    fun getShortlistsFlow(userId: String): Flow<List<ShortlistEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM shortlists WHERE userId = :userId AND targetProfileId = :targetId)")
    suspend fun isShortlisted(userId: String, targetId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShortlist(shortlist: ShortlistEntity)

    @Query("DELETE FROM shortlists WHERE userId = :userId AND targetProfileId = :targetId")
    suspend fun removeFromShortlist(userId: String, targetId: String)

    // Messages / Conversations
    @Query("SELECT * FROM messages WHERE (senderId = :userId AND receiverId = :otherId) OR (senderId = :otherId AND receiverId = :userId) ORDER BY createdAt ASC")
    fun getMessagesForConversationFlow(userId: String, otherId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE senderId = :userId OR receiverId = :userId ORDER BY createdAt DESC")
    fun getAllMessagesForUserFlow(userId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("UPDATE messages SET isRead = 1 WHERE senderId = :otherId AND receiverId = :userId")
    suspend fun markMessagesAsRead(userId: String, otherId: String)

    // Reports / Moderation
    @Query("SELECT * FROM reports ORDER BY createdAt DESC")
    fun getAllReportsFlow(): Flow<List<ReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Query("UPDATE reports SET status = :status, actionTaken = :actionTaken WHERE id = :reportId")
    suspend fun updateReportStatus(reportId: String, status: String, actionTaken: String)

    // Blocked Users
    @Query("SELECT * FROM blocked_users WHERE userId = :userId")
    fun getBlockedUsersFlow(userId: String): Flow<List<BlockedUserEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM blocked_users WHERE userId = :userId AND blockedProfileId = :targetId)")
    suspend fun isBlocked(userId: String, targetId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedUser(blocked: BlockedUserEntity)

    @Query("DELETE FROM blocked_users WHERE userId = :userId AND blockedProfileId = :targetId")
    suspend fun unblockUser(userId: String, targetId: String)

    // Notifications
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotificationsFlow(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllNotificationsRead(userId: String)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAuditLogsFlow(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)
}
