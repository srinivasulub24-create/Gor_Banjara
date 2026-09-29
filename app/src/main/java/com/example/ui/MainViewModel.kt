package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.recommendation.RecommendationEngine
import com.example.data.recommendation.RecommendationItem
import com.example.data.repository.MatrimonyRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class Screen {
    DISCOVER,
    SEARCH,
    PROFILE_DETAIL,
    INTERESTS,
    CHAT,
    MY_PROFILE,
    SAFETY_GRIEVANCE,
    PLANS,
    ADMIN_CONSOLE,
    REGISTRATION
}

enum class RecommendationFilter {
    ALL_RECOMMENDED,
    SHARED_LOCATION,
    SHARED_PROFESSION
}

data class SearchFilter(
    val query: String = "",
    val clan: String = "All",
    val minAge: Int = 18,
    val maxAge: Int = 45,
    val location: String = "All",
    val occupation: String = "All",
    val verifiedOnly: Boolean = false,
    val sortBy: String = "NEWEST"
)

class MainViewModel(private val repository: MatrimonyRepository) : ViewModel() {

    private val _currentScreen = MutableStateFlow(Screen.DISCOVER)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _screenHistory = mutableListOf<Screen>()

    private val _selectedProfile = MutableStateFlow<UserProfileEntity?>(null)
    val selectedProfile: StateFlow<UserProfileEntity?> = _selectedProfile.asStateFlow()

    private val _activeChatPartner = MutableStateFlow<UserProfileEntity?>(null)
    val activeChatPartner: StateFlow<UserProfileEntity?> = _activeChatPartner.asStateFlow()

    private val _searchFilter = MutableStateFlow(SearchFilter())
    val searchFilter: StateFlow<SearchFilter> = _searchFilter.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    val currentUser: StateFlow<UserProfileEntity?> = repository.getCurrentUserFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allDiscoverable: StateFlow<List<UserProfileEntity>> = repository.getDiscoverableProfilesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val blockedUsers: StateFlow<List<BlockedUserEntity>> = repository.getBlockedUsersFlow("user_me")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Room Database Reactive Search Results (Debounced, Indexed, and Scalable)
    @OptIn(kotlinx.coroutines.FlowPreview::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<UserProfileEntity>> = _searchFilter
        .debounce(200)
        .flatMapLatest { filter ->
            repository.searchProfiles(
                query = filter.query,
                location = if (filter.location == "All") "" else filter.location,
                occupation = if (filter.occupation == "All") "" else filter.occupation,
                clan = filter.clan,
                minAge = filter.minAge,
                maxAge = filter.maxAge,
                verifiedOnly = filter.verifiedOnly,
                sortBy = filter.sortBy
            )
        }
        .combine(blockedUsers) { profiles, blockedList ->
            val blockedIds = blockedList.map { it.blockedProfileId }.toSet()
            profiles.filter { !blockedIds.contains(it.id) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamically available locations from Room database for scalable filter suggestions
    val availableLocations: StateFlow<List<String>> = repository.getDistinctLocations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamically available occupations from Room database for scalable filter suggestions
    val availableOccupations: StateFlow<List<String>> = repository.getDistinctOccupations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shortlists: StateFlow<List<ShortlistEntity>> = repository.getShortlistsFlow("user_me")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<UserProfileEntity>> = repository.getFavoritesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Recommendation Engine Filter (Shared Location, Shared Profession, or All)
    private val _recommendationFilter = MutableStateFlow(RecommendationFilter.ALL_RECOMMENDED)
    val recommendationFilter: StateFlow<RecommendationFilter> = _recommendationFilter.asStateFlow()

    fun setRecommendationFilter(filter: RecommendationFilter) {
        _recommendationFilter.value = filter
    }

    // Recommendation Engine Flow: Evaluates candidate profiles from Room database against Current User preferences
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val recommendedProfiles: StateFlow<List<RecommendationItem>> = currentUser
        .flatMapLatest { user ->
            val myLocation = user?.city?.ifBlank { user.location } ?: "Hyderabad"
            val myOccupation = user?.occupation ?: "Software"
            val myClan = user?.clan ?: ""

            repository.getRecommendedProfiles(
                userLocation = myLocation,
                userOccupation = myOccupation,
                userClan = myClan
            )
        }
        .combine(blockedUsers) { recList, blockedList ->
            val blockedIds = blockedList.map { it.blockedProfileId }.toSet()
            recList.filter { !blockedIds.contains(it.profile.id) }
        }
        .combine(_recommendationFilter) { recList, filter ->
            when (filter) {
                RecommendationFilter.ALL_RECOMMENDED -> recList
                RecommendationFilter.SHARED_LOCATION -> recList.filter { it.isLocationMatch }
                RecommendationFilter.SHARED_PROFESSION -> recList.filter { it.isOccupationMatch }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val receivedInterests: StateFlow<List<InterestEntity>> = repository.getReceivedInterestsFlow("user_me")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sentInterests: StateFlow<List<InterestEntity>> = repository.getSentInterestsFlow("user_me")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.getNotificationsFlow("user_me")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingVerifications: StateFlow<List<UserProfileEntity>> = repository.getPendingVerificationsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reports: StateFlow<List<ReportEntity>> = repository.getAllReportsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.getAuditLogsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initializeIfNeeded()
        }
    }

    // Navigation
    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            _screenHistory.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (_screenHistory.isNotEmpty()) {
            val previous = _screenHistory.removeAt(_screenHistory.size - 1)
            _currentScreen.value = previous
            return true
        }
        if (_currentScreen.value != Screen.DISCOVER) {
            _currentScreen.value = Screen.DISCOVER
            return true
        }
        return false
    }

    fun openProfileDetail(profile: UserProfileEntity) {
        _selectedProfile.value = profile
        navigateTo(Screen.PROFILE_DETAIL)
    }

    fun openChatWith(partner: UserProfileEntity) {
        _activeChatPartner.value = partner
        navigateTo(Screen.CHAT)
    }

    fun updateSearchFilter(filter: SearchFilter) {
        _searchFilter.value = filter
    }

    fun toggleShortlist(targetId: String) {
        viewModelScope.launch {
            repository.toggleShortlist("user_me", targetId)
            val isNowShortlisted = repository.isShortlisted("user_me", targetId)
            _snackbarMessage.emit(if (isNowShortlisted) "Profile added to private shortlist" else "Removed from shortlist")
        }
    }

    fun toggleFavorite(targetId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(targetId)
            if (_selectedProfile.value?.id == targetId) {
                val current = _selectedProfile.value
                if (current != null) {
                    _selectedProfile.value = current.copy(isFavorite = !current.isFavorite)
                }
            }
            _snackbarMessage.emit("Favorite status updated in Room database")
        }
    }

    fun sendInterest(targetId: String, note: String) {
        viewModelScope.launch {
            val result = repository.sendInterest("user_me", targetId, note)
            if (result.isSuccess) {
                _snackbarMessage.emit("Interest sent with respectful personal note!")
            } else {
                _snackbarMessage.emit(result.exceptionOrNull()?.message ?: "Could not send interest")
            }
        }
    }

    fun acceptInterest(interestId: String) {
        viewModelScope.launch {
            repository.respondToInterest(interestId, accept = true)
            _snackbarMessage.emit("Interest accepted! Connection established. You can now chat.")
        }
    }

    fun declineInterest(interestId: String) {
        viewModelScope.launch {
            repository.respondToInterest(interestId, accept = false)
            _snackbarMessage.emit("Interest declined.")
        }
    }

    fun withdrawInterest(interestId: String) {
        viewModelScope.launch {
            repository.withdrawInterest(interestId)
            _snackbarMessage.emit("Interest withdrawn.")
        }
    }

    fun getChatMessages(otherId: String): Flow<List<MessageEntity>> {
        return repository.getMessagesFlow("user_me", otherId)
    }

    fun sendMessage(otherId: String, text: String) {
        viewModelScope.launch {
            val result = repository.sendMessage("user_me", otherId, text)
            if (result.isFailure) {
                _snackbarMessage.emit(result.exceptionOrNull()?.message ?: "Failed to send message")
            }
        }
    }

    fun reportProfile(targetId: String, targetName: String, category: String, details: String) {
        viewModelScope.launch {
            repository.reportProfile("user_me", targetId, targetName, category, details)
            _snackbarMessage.emit("Grievance report submitted to moderation team for priority review.")
            navigateBack()
        }
    }

    fun blockUser(targetId: String, targetName: String) {
        viewModelScope.launch {
            repository.blockUser("user_me", targetId, targetName)
            _snackbarMessage.emit("User $targetName has been blocked.")
            navigateBack()
        }
    }

    fun unblockUser(targetId: String) {
        viewModelScope.launch {
            repository.unblockUser("user_me", targetId)
            _snackbarMessage.emit("User unblocked.")
        }
    }

    fun togglePhotoBlur() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.togglePhotoBlur(user.id, user.isPhotoBlurred)
            _snackbarMessage.emit(if (!user.isPhotoBlurred) "Photo privacy enabled (blurred until interest accepted)" else "Photo privacy disabled (photo visible)")
        }
    }

    fun updateUserProfile(profile: UserProfileEntity) {
        viewModelScope.launch {
            repository.updateCurrentUser(profile)
            _snackbarMessage.emit("Profile details saved successfully!")
        }
    }

    fun updateProfilePhoto(profileId: String, photoUrl: String) {
        viewModelScope.launch {
            repository.updateProfilePhotoUrl(profileId, photoUrl)
            _snackbarMessage.emit("Profile photo updated successfully!")
        }
    }

    fun registerNewUser(
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
        partnerPreferences: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.registerNewUser(
                fullName = fullName,
                gender = gender,
                birthYear = birthYear,
                birthMonth = birthMonth,
                birthDay = birthDay,
                clan = clan,
                subClan = subClan,
                tanda = tanda,
                city = city,
                state = state,
                education = education,
                occupation = occupation,
                annualIncome = annualIncome,
                diet = diet,
                height = height,
                aboutMe = aboutMe,
                partnerPreferences = partnerPreferences
            )
            if (result.isSuccess) {
                _snackbarMessage.emit("Welcome to Banjara Matrimony! 18+ Age verification passed.")
                _currentScreen.value = Screen.DISCOVER
                onSuccess()
            } else {
                _snackbarMessage.emit(result.exceptionOrNull()?.message ?: "Registration error")
            }
        }
    }

    // Admin / Moderator actions
    fun approveVerification(profileId: String, profileName: String) {
        viewModelScope.launch {
            repository.approveVerification(profileId, profileName)
            _snackbarMessage.emit("Approved identity verification for $profileName")
        }
    }

    fun rejectVerification(profileId: String, profileName: String, reason: String) {
        viewModelScope.launch {
            repository.rejectVerification(profileId, profileName, reason)
            _snackbarMessage.emit("Rejected verification for $profileName")
        }
    }

    fun moderateReport(reportId: String, action: String, targetId: String, targetName: String) {
        viewModelScope.launch {
            repository.moderateReport(reportId, action, targetId, targetName)
            _snackbarMessage.emit("Report actioned: $action for $targetName")
        }
    }

    fun upgradeMembership(tier: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.upgradeMembership(user.id, tier)
            _snackbarMessage.emit("Plan upgraded to $tier! Verified webhook confirmation received.")
        }
    }
}
