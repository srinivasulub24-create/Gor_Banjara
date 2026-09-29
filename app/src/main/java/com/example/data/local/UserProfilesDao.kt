package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfilesDao {

    // --- CREATE ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfiles)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(profiles: List<UserProfiles>)

    // --- READ ---
    @Query("SELECT * FROM UserProfiles ORDER BY createdAt DESC")
    fun getAllProfiles(): Flow<List<UserProfiles>>

    @Query("SELECT * FROM UserProfiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: String): UserProfiles?

    @Query("SELECT * FROM UserProfiles WHERE id = :id LIMIT 1")
    fun getProfileByIdFlow(id: String): Flow<UserProfiles?>

    @Query("SELECT * FROM UserProfiles WHERE location LIKE '%' || :location || '%'")
    fun getProfilesByLocation(location: String): Flow<List<UserProfiles>>

    @Query("SELECT * FROM UserProfiles WHERE occupation LIKE '%' || :occupation || '%'")
    fun getProfilesByOccupation(occupation: String): Flow<List<UserProfiles>>

    @Query("SELECT * FROM UserProfiles WHERE location LIKE '%' || :location || '%' AND occupation LIKE '%' || :occupation || '%'")
    fun getProfilesByLocationAndOccupation(location: String, occupation: String): Flow<List<UserProfiles>>

    @Query("""
        SELECT * FROM UserProfiles
        WHERE isCurrentUser = 0 
          AND isVisibleInSearch = 1 
          AND isAccountRestricted = 0
          AND (:location IS NULL OR :location = '' OR :location = 'All' 
               OR location LIKE '%' || :location || '%' 
               OR city LIKE '%' || :location || '%' 
               OR state LIKE '%' || :location || '%' 
               OR tanda LIKE '%' || :location || '%')
          AND (:occupation IS NULL OR :occupation = '' OR :occupation = 'All' 
               OR occupation LIKE '%' || :occupation || '%')
          AND (:clan IS NULL OR :clan = '' OR :clan = 'All' OR clan = :clan)
          AND (age BETWEEN :minAge AND :maxAge)
          AND (:verifiedOnly = 0 OR isIdentityVerified = 1)
          AND (:query IS NULL OR :query = '' 
               OR name LIKE '%' || :query || '%' 
               OR fullName LIKE '%' || :query || '%' 
               OR biodata LIKE '%' || :query || '%' 
               OR education LIKE '%' || :query || '%' 
               OR occupation LIKE '%' || :query || '%' 
               OR location LIKE '%' || :query || '%')
        ORDER BY 
          CASE WHEN :sortBy = 'AGE_ASC' THEN age END ASC,
          CASE WHEN :sortBy = 'AGE_DESC' THEN age END DESC,
          CASE WHEN :sortBy = 'NAME' THEN name END ASC,
          createdAt DESC
        LIMIT :limit OFFSET :offset
    """)
    fun filterProfiles(
        query: String? = null,
        location: String? = null,
        occupation: String? = null,
        clan: String? = null,
        minAge: Int = 18,
        maxAge: Int = 100,
        verifiedOnly: Boolean = false,
        sortBy: String = "NEWEST",
        limit: Int = 100,
        offset: Int = 0
    ): Flow<List<UserProfiles>>

    @Query("""
        SELECT DISTINCT city FROM UserProfiles 
        WHERE city != '' AND isVisibleInSearch = 1 
        ORDER BY city ASC
    """)
    fun getDistinctLocations(): Flow<List<String>>

    @Query("""
        SELECT DISTINCT occupation FROM UserProfiles 
        WHERE occupation != '' AND isVisibleInSearch = 1 
        ORDER BY occupation ASC
    """)
    fun getDistinctOccupations(): Flow<List<String>>

    // --- RECOMMENDATION ENGINE ---
    @Query("""
        SELECT * FROM UserProfiles
        WHERE isCurrentUser = 0 
          AND isVisibleInSearch = 1 
          AND isAccountRestricted = 0
          AND (
            (:location != '' AND (location LIKE '%' || :location || '%' OR city LIKE '%' || :location || '%' OR state LIKE '%' || :location || '%'))
            OR 
            (:occupation != '' AND (occupation LIKE '%' || :occupation || '%' OR :occupation LIKE '%' || occupation || '%'))
          )
        ORDER BY (
            (CASE WHEN :location != '' AND (location LIKE '%' || :location || '%' OR city LIKE '%' || :location || '%') THEN 3 ELSE 0 END) +
            (CASE WHEN :occupation != '' AND (occupation LIKE '%' || :occupation || '%' OR :occupation LIKE '%' || occupation || '%') THEN 3 ELSE 0 END) +
            (CASE WHEN isIdentityVerified = 1 THEN 1 ELSE 0 END)
        ) DESC, createdAt DESC
        LIMIT :limit
    """)
    fun getRecommendedProfiles(
        location: String,
        occupation: String,
        limit: Int = 30
    ): Flow<List<UserProfiles>>

    @Query("""
        SELECT * FROM UserProfiles
        WHERE isCurrentUser = 0 
          AND isVisibleInSearch = 1 
          AND isAccountRestricted = 0
          AND (location LIKE '%' || :location || '%' OR city LIKE '%' || :location || '%' OR state LIKE '%' || :location || '%')
        ORDER BY createdAt DESC
        LIMIT :limit
    """)
    fun getRecommendationsByLocation(
        location: String,
        limit: Int = 20
    ): Flow<List<UserProfiles>>

    @Query("""
        SELECT * FROM UserProfiles
        WHERE isCurrentUser = 0 
          AND isVisibleInSearch = 1 
          AND isAccountRestricted = 0
          AND (occupation LIKE '%' || :occupation || '%' OR :occupation LIKE '%' || occupation || '%')
        ORDER BY createdAt DESC
        LIMIT :limit
    """)
    fun getRecommendationsByOccupation(
        occupation: String,
        limit: Int = 20
    ): Flow<List<UserProfiles>>

    @Query("SELECT * FROM UserProfiles WHERE age BETWEEN :minAge AND :maxAge")
    fun getProfilesByAgeRange(minAge: Int, maxAge: Int): Flow<List<UserProfiles>>

    @Query("SELECT * FROM UserProfiles WHERE name LIKE '%' || :query || '%' OR biodata LIKE '%' || :query || '%'")
    fun searchProfiles(query: String): Flow<List<UserProfiles>>

    // --- UPDATE ---
    @Update
    suspend fun updateProfile(profile: UserProfiles)

    @Query("UPDATE UserProfiles SET biodata = :biodata WHERE id = :id")
    suspend fun updateBiodata(id: String, biodata: String)

    @Query("UPDATE UserProfiles SET occupation = :occupation WHERE id = :id")
    suspend fun updateOccupation(id: String, occupation: String)

    @Query("UPDATE UserProfiles SET location = :location WHERE id = :id")
    suspend fun updateLocation(id: String, location: String)

    @Query("UPDATE UserProfiles SET name = :name, age = :age, occupation = :occupation, location = :location, biodata = :biodata WHERE id = :id")
    suspend fun updateBasicInfo(id: String, name: String, age: Int, occupation: String, location: String, biodata: String)

    // --- PROFILE PHOTO URL (STORE & RETRIEVE) ---
    @Query("UPDATE UserProfiles SET profilePhotoUrl = :profilePhotoUrl, photoUrl = :profilePhotoUrl WHERE id = :id")
    suspend fun updateProfilePhotoUrl(id: String, profilePhotoUrl: String)

    @Query("SELECT profilePhotoUrl FROM UserProfiles WHERE id = :id LIMIT 1")
    suspend fun getProfilePhotoUrl(id: String): String?

    @Query("SELECT profilePhotoUrl FROM UserProfiles WHERE id = :id LIMIT 1")
    fun getProfilePhotoUrlFlow(id: String): Flow<String?>

    // --- FAVORITES ---
    @Query("UPDATE UserProfiles SET isFavorite = CASE WHEN isFavorite = 1 THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun toggleFavorite(id: String)

    @Query("UPDATE UserProfiles SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)

    @Query("SELECT * FROM UserProfiles WHERE isFavorite = 1 AND isVisibleInSearch = 1 AND isAccountRestricted = 0 ORDER BY createdAt DESC")
    fun getFavoriteProfiles(): Flow<List<UserProfiles>>

    @Query("SELECT isFavorite FROM UserProfiles WHERE id = :id LIMIT 1")
    fun isProfileFavoriteFlow(id: String): Flow<Boolean?>

    // --- DELETE ---
    @Delete
    suspend fun deleteProfile(profile: UserProfiles)

    @Query("DELETE FROM UserProfiles WHERE id = :id")
    suspend fun deleteProfileById(id: String)

    @Query("DELETE FROM UserProfiles")
    suspend fun deleteAllProfiles()
}

typealias UserProfileDao = UserProfilesDao
