package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.SampleDataProvider
import com.example.data.local.UserProfiles
import com.example.data.recommendation.RecommendationEngine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  private lateinit var database: AppDatabase

  @Before
  fun setup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Banjara Matrimony", appName)
  }

  @Test
  fun `verify sample profiles adult age requirement`() {
    val sampleProfiles = SampleDataProvider.getSampleProfiles()
    for (profile in sampleProfiles) {
      assertTrue("Profile ${profile.fullName} must be >= 18", profile.age >= 18)
      assertTrue("Name must not be empty", profile.name.isNotEmpty())
      assertTrue("Location must not be empty", profile.location.isNotEmpty())
      assertTrue("Occupation must not be empty", profile.occupation.isNotEmpty())
      assertTrue("Biodata must not be empty", profile.biodata.isNotEmpty())
    }
  }

  @Test
  fun `verify UserProfiles entity and UserProfilesDao CRUD operations`() = runBlocking {
    val dao = database.userProfilesDao()

    // 1. CREATE
    val newProfile = UserProfiles(
      id = "test_user_001",
      name = "Ramesh Pawar",
      age = 28,
      occupation = "Software Architect",
      location = "Hyderabad, Telangana",
      biodata = "Traditional Banjara family from Nalgonda, values cultural roots.",
      clan = "Pawar"
    )
    dao.insertProfile(newProfile)

    // 2. READ
    val fetched = dao.getProfileById("test_user_001")
    assertNotNull(fetched)
    assertEquals("Ramesh Pawar", fetched?.name)
    assertEquals(28, fetched?.age)
    assertEquals("Software Architect", fetched?.occupation)
    assertEquals("Hyderabad, Telangana", fetched?.location)
    assertEquals("Traditional Banjara family from Nalgonda, values cultural roots.", fetched?.biodata)

    val list = dao.getAllProfiles().first()
    assertEquals(1, list.size)

    val byLocation = dao.getProfilesByLocation("Hyderabad").first()
    assertEquals(1, byLocation.size)

    // 3. UPDATE
    dao.updateOccupation("test_user_001", "Principal Engineer")
    dao.updateBiodata("test_user_001", "Updated biodata for matrimonial alliance.")

    val updated = dao.getProfileById("test_user_001")
    assertEquals("Principal Engineer", updated?.occupation)
    assertEquals("Updated biodata for matrimonial alliance.", updated?.biodata)

    // 4. DELETE
    dao.deleteProfileById("test_user_001")
    val deleted = dao.getProfileById("test_user_001")
    assertNull(deleted)
  }

  @Test
  fun `verify Room search filtering by location and occupation`() = runBlocking {
    val dao = database.userProfilesDao()

    val p1 = UserProfiles(
      id = "p_01",
      name = "Suresh Rathod",
      age = 29,
      occupation = "Lead Software Engineer",
      location = "Hyderabad, Telangana",
      city = "Hyderabad",
      state = "Telangana",
      biodata = "Banjara tech professional.",
      clan = "Rathod",
      isCurrentUser = false,
      isVisibleInSearch = true,
      isAccountRestricted = false
    )
    val p2 = UserProfiles(
      id = "p_02",
      name = "Dr. Anita Pawar",
      age = 27,
      occupation = "Pediatric Doctor",
      location = "Bengaluru, Karnataka",
      city = "Bengaluru",
      state = "Karnataka",
      biodata = "Doctor serving community.",
      clan = "Pawar",
      isCurrentUser = false,
      isVisibleInSearch = true,
      isAccountRestricted = false
    )
    val p3 = UserProfiles(
      id = "p_03",
      name = "Vinod Chauhan",
      age = 31,
      occupation = "Civil Services Officer",
      location = "Hyderabad, Telangana",
      city = "Hyderabad",
      state = "Telangana",
      biodata = "Govt administration.",
      clan = "Chauhan",
      isCurrentUser = false,
      isVisibleInSearch = true,
      isAccountRestricted = false
    )

    dao.insertAll(listOf(p1, p2, p3))

    // 1. Filter by location = Hyderabad
    val hydProfiles = dao.filterProfiles(location = "Hyderabad").first()
    assertEquals(2, hydProfiles.size)
    assertTrue(hydProfiles.any { it.name == "Suresh Rathod" })
    assertTrue(hydProfiles.any { it.name == "Vinod Chauhan" })

    // 2. Filter by location = Bengaluru
    val blrProfiles = dao.filterProfiles(location = "Bengaluru").first()
    assertEquals(1, blrProfiles.size)
    assertEquals("Dr. Anita Pawar", blrProfiles[0].name)

    // 3. Filter by occupation = Doctor
    val docProfiles = dao.filterProfiles(occupation = "Doctor").first()
    assertEquals(1, docProfiles.size)
    assertEquals("Dr. Anita Pawar", docProfiles[0].name)

    // 4. Filter by occupation = Engineer
    val engProfiles = dao.filterProfiles(occupation = "Engineer").first()
    assertEquals(1, engProfiles.size)
    assertEquals("Suresh Rathod", engProfiles[0].name)

    // 5. Compound filter: location = Hyderabad AND occupation = Civil Services
    val compoundProfiles = dao.filterProfiles(location = "Hyderabad", occupation = "Civil Services").first()
    assertEquals(1, compoundProfiles.size)
    assertEquals("Vinod Chauhan", compoundProfiles[0].name)

    // 6. Distinct locations check
    val distinctLocs = dao.getDistinctLocations().first()
    assertTrue(distinctLocs.contains("Bengaluru"))
    assertTrue(distinctLocs.contains("Hyderabad"))

    // 7. Distinct occupations check
    val distinctOccs = dao.getDistinctOccupations().first()
    assertTrue(distinctOccs.contains("Pediatric Doctor"))
    assertTrue(distinctOccs.contains("Lead Software Engineer"))
  }

  @Test
  fun `verify Favorites system with isFavorite boolean and DAO toggle method`() = runBlocking {
    val dao = database.userProfilesDao()

    val profile = UserProfiles(
      id = "fav_test_001",
      name = "Pooja Jadhav",
      age = 26,
      occupation = "Chartered Accountant",
      location = "Hyderabad, Telangana",
      biodata = "Banjara family with strong cultural roots.",
      clan = "Jadhav",
      isCurrentUser = false,
      isVisibleInSearch = true,
      isAccountRestricted = false,
      isFavorite = false
    )
    dao.insertProfile(profile)

    // Verify initial isFavorite is false
    val initial = dao.getProfileById("fav_test_001")
    assertNotNull(initial)
    assertEquals(false, initial?.isFavorite)

    // No favorites yet in query
    val initialFavs = dao.getFavoriteProfiles().first()
    assertTrue(initialFavs.none { it.id == "fav_test_001" })

    // 1. Toggle Favorite: false -> true
    dao.toggleFavorite("fav_test_001")
    val favorited = dao.getProfileById("fav_test_001")
    assertEquals(true, favorited?.isFavorite)

    val favsAfterToggle = dao.getFavoriteProfiles().first()
    assertEquals(1, favsAfterToggle.size)
    assertEquals("fav_test_001", favsAfterToggle[0].id)
    assertEquals("Pooja Jadhav", favsAfterToggle[0].name)

    // 2. Toggle Favorite again: true -> false
    dao.toggleFavorite("fav_test_001")
    val unfavorited = dao.getProfileById("fav_test_001")
    assertEquals(false, unfavorited?.isFavorite)

    val favsAfterSecondToggle = dao.getFavoriteProfiles().first()
    assertTrue(favsAfterSecondToggle.isEmpty())

    // 3. Update status directly
    dao.updateFavoriteStatus("fav_test_001", true)
    val directUpdated = dao.getProfileById("fav_test_001")
    assertEquals(true, directUpdated?.isFavorite)
  }

  @Test
  fun `verify Recommendation Engine with shared location and similar professional interests`() = runBlocking {
    val dao = database.userProfilesDao()

    val pLocationMatch = UserProfiles(
      id = "rec_01",
      name = "Rohit Pawar",
      age = 28,
      occupation = "Civil Contractor",
      location = "Hyderabad, Telangana",
      city = "Hyderabad",
      state = "Telangana",
      biodata = "Banjara businessman in Hyderabad.",
      clan = "Pawar",
      isCurrentUser = false,
      isVisibleInSearch = true,
      isAccountRestricted = false
    )

    val pCareerMatch = UserProfiles(
      id = "rec_02",
      name = "Deepak Chauhan",
      age = 29,
      occupation = "Senior Software Architect",
      location = "Pune, Maharashtra",
      city = "Pune",
      state = "Maharashtra",
      biodata = "Tech professional in Pune.",
      clan = "Chauhan",
      isCurrentUser = false,
      isVisibleInSearch = true,
      isAccountRestricted = false
    )

    val pDualMatch = UserProfiles(
      id = "rec_03",
      name = "Vikram Jadhav",
      age = 27,
      occupation = "Full Stack Software Developer",
      location = "Hyderabad, Telangana",
      city = "Hyderabad",
      state = "Telangana",
      biodata = "Living and coding in Hyderabad.",
      clan = "Jadhav",
      isCurrentUser = false,
      isVisibleInSearch = true,
      isAccountRestricted = false
    )

    dao.insertAll(listOf(pLocationMatch, pCareerMatch, pDualMatch))

    // 1. Query Room Database for Recommendations based on User (Location: Hyderabad, Occupation: Software Engineer)
    val candidateProfiles = dao.getRecommendedProfiles(
      location = "Hyderabad",
      occupation = "Software",
      limit = 10
    ).first()

    assertEquals(3, candidateProfiles.size)

    // 2. Score candidates using RecommendationEngine
    val scoredList = candidateProfiles.map { candidate ->
      RecommendationEngine.scoreRecommendation(
        candidate = candidate,
        userLocation = "Hyderabad, Telangana",
        userOccupation = "Lead Software Engineer @ MNC",
        userClan = "Rathod"
      )
    }.sortedByDescending { it.matchScore }

    // Dual match (both shared location AND similar career) must rank highest
    val topRecommendation = scoredList.first()
    assertEquals("rec_03", topRecommendation.profile.id)
    assertEquals(true, topRecommendation.isLocationMatch)
    assertEquals(true, topRecommendation.isOccupationMatch)
    assertTrue("Dual match should score >= 90%", topRecommendation.matchScore >= 90)

    // Verify individual match detections
    val locationOnlyRec = scoredList.first { it.profile.id == "rec_01" }
    assertEquals(true, locationOnlyRec.isLocationMatch)
    assertEquals(false, locationOnlyRec.isOccupationMatch)
    assertTrue(locationOnlyRec.badges.any { it.contains("Same City") })

    val careerOnlyRec = scoredList.first { it.profile.id == "rec_02" }
    assertEquals(false, careerOnlyRec.isLocationMatch)
    assertEquals(true, careerOnlyRec.isOccupationMatch)
    assertTrue(careerOnlyRec.badges.any { it.contains("Tech & Software") })
  }

  @Test
  fun `verify profilePhotoUrl field and DAO storing and retrieving image path`() = runBlocking {
    val dao = database.userProfilesDao()

    val profile = UserProfiles(
      id = "photo_test_001",
      name = "Suresh Rathod",
      age = 30,
      occupation = "Software Engineer",
      location = "Hyderabad, Telangana",
      biodata = "Banjara professional in Hyderabad.",
      clan = "Rathod",
      profilePhotoUrl = "https://example.com/photos/initial_photo.jpg"
    )
    dao.insertProfile(profile)

    // 1. Verify initial profilePhotoUrl retrieved via DAO
    val initialPhoto = dao.getProfilePhotoUrl("photo_test_001")
    assertEquals("https://example.com/photos/initial_photo.jpg", initialPhoto)

    val initialFlowPhoto = dao.getProfilePhotoUrlFlow("photo_test_001").first()
    assertEquals("https://example.com/photos/initial_photo.jpg", initialFlowPhoto)

    // 2. Store updated image path / URL using DAO method
    val newImagePath = "content://media/external/images/media/45821.jpg"
    dao.updateProfilePhotoUrl("photo_test_001", newImagePath)

    // 3. Verify retrieved image path matches the new stored value
    val updatedPhoto = dao.getProfilePhotoUrl("photo_test_001")
    assertEquals(newImagePath, updatedPhoto)

    val updatedFlowPhoto = dao.getProfilePhotoUrlFlow("photo_test_001").first()
    assertEquals(newImagePath, updatedFlowPhoto)

    val updatedEntity = dao.getProfileById("photo_test_001")
    assertEquals(newImagePath, updatedEntity?.profilePhotoUrl)
  }
}
