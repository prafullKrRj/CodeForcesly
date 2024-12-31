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
import com.prafullkumar.codeforcesly.onBoarding.ui.OnboardingScreen
import com.prafullkumar.codeforcesly.problem.ui.ProblemsScreen
import com.prafullkumar.codeforcesly.profile.profile.ProfileScreen
import com.prafullkumar.codeforcesly.profile.submissions.SubmissionsScreen
import com.prafullkumar.codeforcesly.profile.submissions.SubmissionsViewModel
import com.prafullkumar.codeforcesly.visualizer.VisualizerScreen

// Navigation.kt
sealed class Screen(val route: String) {
    object Auth {
        data object Login : Screen("login")
    }

    object Main {
        data object Profile : Screen("profile")
        data object Contests : Screen("contests")
        data object Friends : Screen("friends")
        data object Problems : Screen("problems")
        data object Visualizer : Screen(route = "visualizer")
        data object Submissions : Screen("submissions")
    }
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
        startDestination = if (isAuthenticated) "main" else "auth"
    ) {
        // Auth Navigation Graph
        navigation(startDestination = Screen.Auth.Login.route, route = "auth") {
            composable(Screen.Auth.Login.route) {
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
        navigation(startDestination = Screen.Main.Profile.route, route = "main") {
            composable(Screen.Main.Profile.route) {
                MainScreen(
                    navController = navController,
                    startDestination = Screen.Main.Profile.route
                )
            }
            composable(Screen.Main.Contests.route) {
                MainScreen(
                    navController = navController,
                    startDestination = Screen.Main.Contests.route
                )
            }
            composable(Screen.Main.Friends.route) {
                MainScreen(
                    navController = navController,
                    startDestination = Screen.Main.Friends.route
                )
            }
            composable(Screen.Main.Problems.route) {
                MainScreen(
                    navController = navController,
                    startDestination = Screen.Main.Problems.route
                )
            }
            composable(Screen.Main.Visualizer.route) {
                MainScreen(
                    navController = navController,
                    startDestination = Screen.Main.Visualizer.route
                )
            }
            composable(Screen.Main.Submissions.route) {
                SubmissionsScreen(hiltViewModel<SubmissionsViewModel>()) {
                    navController.popBackStack()
                }
            }
        }
    }
}

// MainScreen.kt
@Composable
fun MainScreen(navController: NavController, startDestination: String) {
    val context = LocalContext.current
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = startDestination == Screen.Main.Profile.route,
                    onClick = { navController.navigate(Screen.Main.Profile.route) }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            ImageVector.vectorResource(R.drawable.baseline_timeline_24),
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text("Visualizer") },
                    selected = startDestination == Screen.Main.Visualizer.route,
                    onClick = { navController.navigate(Screen.Main.Visualizer.route) }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            ImageVector.vectorResource(R.drawable.baseline_event_note_24),
                            contentDescription = "Contests"
                        )
                    },
                    label = { Text("Contests") },
                    selected = startDestination == Screen.Main.Contests.route,
                    onClick = { navController.navigate(Screen.Main.Contests.route) }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            ImageVector.vectorResource(R.drawable.baseline_group_24),
                            contentDescription = "Friends"
                        )
                    },
                    label = { Text("Friends") },
                    selected = startDestination == Screen.Main.Friends.route,
                    onClick = { navController.navigate(Screen.Main.Friends.route) }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            ImageVector.vectorResource(R.drawable.baseline_code_24),
                            contentDescription = "Problems"
                        )
                    },
                    label = { Text("Problems") },
                    selected = startDestination == Screen.Main.Problems.route,
                    onClick = { navController.navigate(Screen.Main.Problems.route) }
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
                Screen.Main.Profile.route -> ProfileScreen(onNavigateToSubmissions = {
                    navController.navigate(Screen.Main.Submissions.route)
                })

                Screen.Main.Contests.route -> ContestsScreen(hiltViewModel())
                Screen.Main.Friends.route -> FriendsScreen(
                    navController,
                    viewModel = hiltViewModel()
                )

                Screen.Main.Visualizer.route -> VisualizerScreen()
                Screen.Main.Problems.route -> ProblemsScreen(viewModel = hiltViewModel()) {

                }
            }
        }
    }
}