package com.prafullkumar.codeforcesly

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.prafullkumar.codeforcesly.contests.ContestsScreen
import com.prafullkumar.codeforcesly.contests.ContestsViewModel
import com.prafullkumar.codeforcesly.friends.FriendsScreen
import com.prafullkumar.codeforcesly.friends.FriendsViewModel
import com.prafullkumar.codeforcesly.friends.data.FriendsDatabase
import com.prafullkumar.codeforcesly.friends.data.FriendsRepository
import com.prafullkumar.codeforcesly.login.OnboardingScreen
import com.prafullkumar.codeforcesly.login.OnboardingViewModel
import com.prafullkumar.codeforcesly.login.data.UserDatabase
import com.prafullkumar.codeforcesly.login.data.UserRepository
import com.prafullkumar.codeforcesly.problem.ProblemsScreen
import com.prafullkumar.codeforcesly.problem.ProblemsViewModel
import com.prafullkumar.codeforcesly.problem.data.ProblemsRepository

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
    }
}

// AppNavigation.kt
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Simulating auth state, replace with actual auth logic
    var isAuthenticated by remember { mutableStateOf(false) }

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
                    OnboardingViewModel(
                        UserRepository(
                            userDao = UserDatabase.getDatabase(LocalContext.current).userDao()
                        )
                    )
                ) {
                    isAuthenticated = true
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
        }
    }
}

// MainScreen.kt
@Composable
fun MainScreen(navController: NavController, startDestination: String) {
    val context = LocalContext.current
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = startDestination == Screen.Main.Profile.route,
                    onClick = { navController.navigate(Screen.Main.Profile.route) }
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
                Screen.Main.Profile.route -> ProfileScreen()
                Screen.Main.Contests.route -> ContestsScreen(ContestsViewModel())
                Screen.Main.Friends.route -> FriendsScreen(
                    navController,
                    FriendsViewModel(
                        FriendsRepository(
                            friendDao = FriendsDatabase.getDatabase(context).friendDao(),
                        )
                    )
                )

                Screen.Main.Problems.route -> ProblemsScreen(
                    ProblemsViewModel(
                        ProblemsRepository(
                            context
                        )
                    )
                ) {

                }
            }
        }
    }
}

@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Profile Screen", style = MaterialTheme.typography.headlineMedium)
    }
}
