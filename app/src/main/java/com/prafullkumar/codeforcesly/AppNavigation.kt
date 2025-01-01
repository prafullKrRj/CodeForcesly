package com.prafullkumar.codeforcesly

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.prafullkumar.codeforcesly.common.SharedPrefManager
import com.prafullkumar.codeforcesly.contests.ui.ContestsScreen
import com.prafullkumar.codeforcesly.friends.ui.FriendsScreen
import com.prafullkumar.codeforcesly.friends.ui.friendDetailScren.FriendDetailScreen
import com.prafullkumar.codeforcesly.onBoarding.ui.OnboardingScreen
import com.prafullkumar.codeforcesly.problem.ui.ProblemsScreen
import com.prafullkumar.codeforcesly.profile.profile.ProfileScreen
import com.prafullkumar.codeforcesly.profile.submissions.SubmissionsScreen
import com.prafullkumar.codeforcesly.profile.submissions.SubmissionsViewModel
import com.prafullkumar.codeforcesly.visualizer.ui.VisualizerScreen
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
    data object Friends : MainScreens

    @Serializable
    data class FriendDetail(val handle: String) : MainScreens

    @Serializable
    data object Problems : MainScreens

    @Serializable
    data object Visualizer : MainScreens

    @Serializable
    data object Submissions : MainScreens
}

// AppNavigation.kt
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val pref = LocalContext.current.getSharedPreferences(
        SharedPrefManager.SHARED_PREF_NAME,
        Context.MODE_PRIVATE
    )
    val isAuthenticated = pref.getBoolean(SharedPrefManager.LOGGED_IN, false)

    NavHost(
        modifier = Modifier
            .fillMaxSize()
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
                    navController.navigate("main") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            }
        }

        // Main Navigation Graph
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
                SubmissionsScreen(hiltViewModel<SubmissionsViewModel>()) {
                    navController.popBackStack()
                }
            }
            composable<MainScreens.FriendDetail> {
                FriendDetailScreen(hiltViewModel(), navController)
            }
        }
    }
}

// MainScreen.kt
@Composable
fun MainScreen(navController: NavController, startDestination: Any) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = startDestination == MainScreens.Profile,
                    onClick = { navController.navigate(MainScreens.Profile) }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            ImageVector.vectorResource(R.drawable.baseline_timeline_24),
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text("Visualizer") },
                    selected = startDestination == MainScreens.Visualizer,
                    onClick = { navController.navigate(MainScreens.Visualizer) }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            ImageVector.vectorResource(R.drawable.baseline_event_note_24),
                            contentDescription = "Contests"
                        )
                    },
                    label = { Text("Contests") },
                    selected = startDestination == MainScreens.Contests,
                    onClick = { navController.navigate(MainScreens.Contests) }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            ImageVector.vectorResource(R.drawable.baseline_group_24),
                            contentDescription = "Friends"
                        )
                    },
                    label = { Text("Friends") },
                    selected = startDestination == MainScreens.Friends,
                    onClick = { navController.navigate(MainScreens.Friends) }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            ImageVector.vectorResource(R.drawable.baseline_code_24),
                            contentDescription = "Problems"
                        )
                    },
                    label = { Text("Problems") },
                    selected = startDestination == MainScreens.Problems,
                    onClick = { navController.navigate(MainScreens.Problems) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues), contentAlignment = Alignment.Center
        ) {
            when (startDestination) {
                MainScreens.Profile -> ProfileScreen(onNavigateToSubmissions = {
                    navController.navigate(MainScreens.Submissions)
                })

                MainScreens.Contests -> ContestsScreen(hiltViewModel())
                MainScreens.Friends -> FriendsScreen(
                    navController,
                    viewModel = hiltViewModel()
                )

                MainScreens.Visualizer -> VisualizerScreen(hiltViewModel())
                MainScreens.Problems -> ProblemsScreen(viewModel = hiltViewModel()) {

                }
            }
        }
    }
}