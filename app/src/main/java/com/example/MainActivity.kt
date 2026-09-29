package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.data.local.AppDatabase
import com.example.data.repository.MatrimonyRepository
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.MatrimonyBottomBar
import com.example.ui.components.MatrimonyTopAppBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val repository = MatrimonyRepository(database)
        viewModel = MainViewModel(repository)

        setContent {
            MyApplicationTheme {
                BanjaraMatrimonyApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun BanjaraMatrimonyApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedProfile by viewModel.selectedProfile.collectAsState()
    val activeChatPartner by viewModel.activeChatPartner.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val receivedInterests by viewModel.receivedInterests.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // System Back Press Handler
    BackHandler(enabled = true) {
        if (!viewModel.navigateBack()) {
            // At root screen, allow default exit or stay
        }
    }

    val isSubScreen = currentScreen in listOf(
        Screen.PROFILE_DETAIL,
        Screen.CHAT,
        Screen.ADMIN_CONSOLE,
        Screen.PLANS,
        Screen.REGISTRATION
    )

    val title = when (currentScreen) {
        Screen.DISCOVER -> "Banjara Matrimony"
        Screen.SEARCH -> "Find Banjara Matches"
        Screen.INTERESTS -> "Interests & Connections"
        Screen.SAFETY_GRIEVANCE -> "Safety & DPDP Grievance"
        Screen.MY_PROFILE -> "My Profile & Biodata"
        Screen.PROFILE_DETAIL -> selectedProfile?.fullName ?: "Biodata"
        Screen.CHAT -> activeChatPartner?.fullName ?: "Matrimonial Chat"
        Screen.ADMIN_CONSOLE -> "Moderation Console"
        Screen.PLANS -> "Membership Plans"
        Screen.REGISTRATION -> "Adult Profile Registration"
    }

    val unreadNotifs = remember(notifications) { notifications.count { !it.isRead } }
    val pendingInterestsCount = remember(receivedInterests) { receivedInterests.count { it.status == "PENDING" } }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (!isSubScreen) {
                MatrimonyTopAppBar(
                    title = title,
                    canNavigateBack = false,
                    onNavigateBack = { viewModel.navigateBack() },
                    unreadNotifCount = unreadNotifs,
                    onAdminClick = { viewModel.navigateTo(Screen.ADMIN_CONSOLE) },
                    onNotifClick = { viewModel.navigateTo(Screen.INTERESTS) }
                )
            }
        },
        bottomBar = {
            if (!isSubScreen) {
                MatrimonyBottomBar(
                    currentScreen = currentScreen,
                    receivedInterestsCount = pendingInterestsCount,
                    onTabSelected = { screen -> viewModel.navigateTo(screen) }
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isSubScreen) androidx.compose.foundation.layout.PaddingValues() else innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                Screen.DISCOVER -> DiscoverMatchesScreen(viewModel = viewModel)
                Screen.SEARCH -> SearchScreen(viewModel = viewModel)
                Screen.INTERESTS -> InterestsConnectionsScreen(viewModel = viewModel)
                Screen.SAFETY_GRIEVANCE -> SafetyGrievanceScreen(viewModel = viewModel)
                Screen.MY_PROFILE -> MyProfileScreen(viewModel = viewModel)
                Screen.PROFILE_DETAIL -> {
                    selectedProfile?.let {
                        ProfileDetailScreen(profile = it, viewModel = viewModel)
                    } ?: DiscoverMatchesScreen(viewModel = viewModel)
                }
                Screen.CHAT -> {
                    activeChatPartner?.let {
                        ChatScreen(partner = it, viewModel = viewModel)
                    } ?: InterestsConnectionsScreen(viewModel = viewModel)
                }
                Screen.ADMIN_CONSOLE -> AdminConsoleScreen(viewModel = viewModel)
                Screen.PLANS -> MembershipPlansScreen(viewModel = viewModel)
                Screen.REGISTRATION -> RegistrationScreen(viewModel = viewModel)
            }
        }
    }
}
