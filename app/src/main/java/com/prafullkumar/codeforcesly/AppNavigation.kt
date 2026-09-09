package com.prafullkumar.codeforcesly

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.prafullkumar.codeforcesly.common.SharedPrefManager
import com.prafullkumar.codeforcesly.contests.ui.ContestQuestions
import com.prafullkumar.codeforcesly.contests.ui.ContestsScreen
import com.prafullkumar.codeforcesly.contests.ui.ContestsViewModel
import com.prafullkumar.codeforcesly.friends.ui.FriendsScreen
import com.prafullkumar.codeforcesly.friends.ui.FriendsViewModel
import com.prafullkumar.codeforcesly.friends.ui.friendDetailScren.FriendDetailScreen
import com.prafullkumar.codeforcesly.onBoarding.ui.OnboardingScreen
import com.prafullkumar.codeforcesly.problem.domain.model.Problem
import com.prafullkumar.codeforcesly.problem.ui.ProblemsScreen
import com.prafullkumar.codeforcesly.problem.ui.ProblemsViewModel
import com.prafullkumar.codeforcesly.profile.profile.ProfileScreen
import com.prafullkumar.codeforcesly.profile.profile.ProfileViewModel
import com.prafullkumar.codeforcesly.profile.submissions.SubmissionsScreen
import com.prafullkumar.codeforcesly.profile.submissions.SubmissionsViewModel
import com.prafullkumar.codeforcesly.settings.SettingsScreen
import com.prafullkumar.codeforcesly.visualizer.ui.VisualizerScreen
import com.prafullkumar.codeforcesly.visualizer.ui.VisualizerViewModel
import com.prafullkumar.codeforcesly.webview.WebViewScreen
import kotlinx.serialization.Serializable

// Navigation.kt

sealed interface Screen {
    @Serializable
    data object Auth : Screen

    @Serializable
    data object Main : Screen
}

sealed interface AuthScreens : Screen {
    @Serializable
    data object Login : AuthScreens
}

sealed interface MainScreens : Screen {
    @Serializable
    data object Profile : MainScreens

    @Serializable
    data object Contests : MainScreens

    @Serializable
    data class ContestDetailScreen(val contestId: Int) : MainScreens

    @Serializable
    data object Friends : MainScreens

    @Serializable
    data class FriendDetail(val handle: String) : MainScreens

    @Serializable
    data object Problems : MainScreens

    @Serializable
    data object Visualizer : MainScreens

    @Serializable
    data object Submissions : MainScreens

    @Serializable
    data object Settings : MainScreens

    @Serializable
    data class WebView(val url: String, val title: String) : MainScreens
}

// AppNavigation.kt
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val pref = LocalContext.current.getSharedPreferences(
        SharedPrefManager.SHARED_PREF_NAME, Context.MODE_PRIVATE
    )
    val isAuthenticated = pref.getBoolean(SharedPrefManager.LOGGED_IN, false)

    NavHost(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .systemBarsPadding(),
        navController = navController,
        startDestination = if (isAuthenticated) Screen.Main else Screen.Auth
    ) {
        // Auth Navigation Graph
        navigation<Screen.Auth>(startDestination = AuthScreens.Login) {
            composable<AuthScreens.Login> {
                OnboardingScreen(
                    viewModel = hiltViewModel()
                ) {
                    pref.edit().putBoolean(SharedPrefManager.LOGGED_IN, true).apply()
                    navController.navigate(Screen.Main) {
                        popUpTo(Screen.Auth) { inclusive = true }
                    }
                }
            }
        }

        navigation<Screen.Main>(startDestination = MainScreens.Profile) {
            composable<MainScreens.Profile> {
                MainScreen(
                    navController = navController,
                    startDestination = MainScreens.Profile

                )
            }
            composable<MainScreens.Contests> {
                MainScreen(
                    navController = navController,
                    startDestination = MainScreens.Contests
                )
            }
            composable<MainScreens.ContestDetailScreen> {
                val route = it.toRoute<MainScreens.ContestDetailScreen>()
                ContestQuestions(
                    contestId = route.contestId,
                    navController = navController
                )
            }
            composable<MainScreens.Friends> {
                MainScreen(
                    navController = navController,
                    startDestination = MainScreens.Friends
                )
            }
            composable<MainScreens.Problems> {
                MainScreen(
                    navController = navController,
                    startDestination = MainScreens.Problems
                )
            }
            composable<MainScreens.Visualizer> {
                MainScreen(
                    navController = navController,
                    startDestination = MainScreens.Visualizer
                )
            }
            composable<MainScreens.Submissions> {
                val viewModel = hiltViewModel<SubmissionsViewModel>()
                SubmissionsScreen(viewModel, navController)
            }
            composable<MainScreens.FriendDetail> {
                FriendDetailScreen(hiltViewModel(), navController)
            }
            composable<MainScreens.Settings> {
                SettingsScreen(viewModel = hiltViewModel(), onLogoutSuccess = {
                    navController.navigate(Screen.Auth) {
                        popUpTo(0) { inclusive = true }
                    }
                }, navController = navController, onChangeHandleSuccess = {
                    navController.navigate(MainScreens.Profile) {
                        popUpTo(MainScreens.Profile) { inclusive = true }
                        launchSingleTop = true
                    }
                })
            }
            composable<MainScreens.WebView> {
                val url = it.toRoute<MainScreens.WebView>()
                WebViewScreen(url = url.url, title = url.title) {
                    navController.popBackStack()
                }
            }

        }
    }
}

@Composable
fun MainScreen(
    navController: NavController, startDestination: MainScreens
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val destinations = mainDestinations()
        if (maxWidth >= 600.dp) {
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainer
                ) {
                    destinations.forEach { item ->
                        NavigationRailItem(
                            selected = startDestination == item.destination,
                            onClick = { navController.navigateFromMain(item.destination) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
                MainDestinationContent(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    navController = navController,
                    startDestination = startDestination
                )
            }
        } else {
            Scaffold(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                bottomBar = {
                    NavigationBar(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        destinations.forEach { item ->
                            NavigationBarItem(
                                selected = startDestination == item.destination,
                                onClick = { navController.navigateFromMain(item.destination) },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) }
                            )
                        }
                    }
                }
            ) { paddingValues ->
                MainDestinationContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    navController = navController,
                    startDestination = startDestination
                )
            }
        }
    }
}

private data class MainDestination(
    val destination: MainScreens,
    val label: String,
    val icon: ImageVector
)

@Composable
private fun mainDestinations(): List<MainDestination> = listOf(
    MainDestination(MainScreens.Profile, "Profile", Icons.Filled.Person),
    MainDestination(
        MainScreens.Visualizer,
        "Visualizer",
        ImageVector.vectorResource(R.drawable.baseline_timeline_24)
    ),
    MainDestination(
        MainScreens.Contests,
        "Contests",
        ImageVector.vectorResource(R.drawable.baseline_event_note_24)
    ),
    MainDestination(
        MainScreens.Friends,
        "Friends",
        ImageVector.vectorResource(R.drawable.baseline_group_24)
    ),
    MainDestination(
        MainScreens.Problems,
        "Problems",
        ImageVector.vectorResource(R.drawable.baseline_code_24)
    )
)

@Composable
private fun MainDestinationContent(
    modifier: Modifier,
    navController: NavController,
    startDestination: MainScreens
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .widthIn(max = 1040.dp)
        ) {
            when (startDestination) {
                MainScreens.Profile -> {
                    val viewModel = hiltViewModel<ProfileViewModel>()
                    ProfileScreen(viewModel, onNavigateToSubmissions = {
                        navController.navigate(MainScreens.Submissions)
                    }, onNavigateToSettings = {
                        navController.navigate(MainScreens.Settings)
                    })
                }
                MainScreens.Contests -> {
                    val viewModel = hiltViewModel<ContestsViewModel>()
                    ContestsScreen(viewModel, navController = navController)
                }
                MainScreens.Friends -> {
                    val viewModel = hiltViewModel<FriendsViewModel>()
                    FriendsScreen(navController, viewModel)
                }
                MainScreens.Visualizer -> {
                    val viewModel = hiltViewModel<VisualizerViewModel>()
                    VisualizerScreen(viewModel)
                }
                MainScreens.Problems -> {
                    val viewModel = hiltViewModel<ProblemsViewModel>()
                    ProblemsScreen(viewModel) { navController.navigateToProblemWebView(it) }
                }
                else -> Unit
            }
        }
    }
}

private fun NavController.navigateFromMain(destination: MainScreens) {
    navigate(destination) {
        popUpTo(MainScreens.Profile) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavController.navigateToProblemWebView(problem: Problem) {
    this.navigate(
        MainScreens.WebView(
            "https://codeforces.com/problemset/problem/${problem.contestId}/${problem.index}",
            problem.name
        )
    )
}
