package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "UserProfiles",
    indices = [
        Index(value = ["location"]),
        Index(value = ["city"]),
        Index(value = ["occupation"]),
        Index(value = ["clan"]),
        Index(value = ["age"]),
        Index(value = ["isFavorite"]),
        Index(value = ["isCurrentUser", "isVisibleInSearch", "isAccountRestricted"])
    ]
)
data class UserProfiles(
    @PrimaryKey val id: String,
    val name: String,
    val age: Int,
    val occupation: String,
    val location: String,
    val biodata: String,
    val isFavorite: Boolean = false,
    val fullName: String = name,
    val gender: String = "Female", // "Female" or "Male"
    val dateOfBirth: String = "1998-01-01", // "YYYY-MM-DD"
    val height: String = "5 ft 5 in",
    val maritalStatus: String = "Never Married",
    val motherTongue: String = "Gor Boli (Banjara)",
    val clan: String = "Rathod", // "Rathod", "Pawar", "Chauhan", "Jadhav", "Vadtya", etc.
    val subClan: String = "",
    val tanda: String = location,
    val city: String = location,
    val state: String = "Telangana",
    val education: String = "Graduate",
    val annualIncome: String = "₹15 - 20 LPA",
    val workLocation: String = location,
    val diet: String = "Vegetarian",
    val drinking: String = "No",
    val smoking: String = "No",
    val aboutMe: String = biodata,
    val familyDetails: String = "",
    val partnerPreferences: String = "",
    val profilePhotoUrl: String = "",
    val photoUrl: String = profilePhotoUrl,
    val isPhotoBlurred: Boolean = false,
    val isIdentityVerified: Boolean = true,
    val isPhoneVerified: Boolean = true,
    val verificationStatus: String = "VERIFIED", // "VERIFIED", "PENDING", "REJECTED"
    val membershipTier: String = "GOLD", // "FREE", "SILVER", "GOLD", "ROYAL"
    val isCurrentUser: Boolean = false,
    val isVisibleInSearch: Boolean = true,
    val isAccountRestricted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

typealias UserProfileEntity = UserProfiles
typealias UserProfile = UserProfiles

@Entity(tableName = "interests")
data class InterestEntity(
    @PrimaryKey val id: String,
    val senderId: String,
    val receiverId: String,
    val status: String, // "PENDING", "ACCEPTED", "DECLINED", "WITHDRAWN"
    val personalNote: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "shortlists")
data class ShortlistEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val targetProfileId: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val reporterId: String,
    val targetProfileId: String,
    val targetName: String,
    val category: String,
    val description: String,
    val status: String = "PENDING", // "PENDING", "RESOLVED", "DISMISSED"
    val actionTaken: String = "None", // "None", "Warning Issued", "Profile Restricted", "Suspended"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "blocked_users")
data class BlockedUserEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val blockedProfileId: String,
    val blockedName: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String, // "INTEREST", "CONNECTION", "MESSAGE", "SECURITY", "SYSTEM"
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val actorRole: String, // "Admin", "Moderator"
    val action: String,
    val targetProfileId: String,
    val targetName: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
