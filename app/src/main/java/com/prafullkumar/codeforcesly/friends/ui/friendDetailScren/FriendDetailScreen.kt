package com.prafullkumar.codeforcesly.friends.ui.friendDetailScren

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prafullkumar.codeforcesly.MainScreens
import com.prafullkumar.codeforcesly.common.ErrorScreen
import com.prafullkumar.codeforcesly.profile.profile.ProfileContent
import com.prafullkumar.codeforcesly.profile.submissions.SubmissionCard
import com.prafullkumar.codeforcesly.visualizer.ui.charts.CodeforcesCharts
import com.prafullkumar.codeforcesly.ui.theme.AppSpacing
import kotlinx.coroutines.launch

enum class Tabs {
    PROFILE, SUBMISSIONS, VISUALIZER
}

private val Tabs.label: String
    get() = when (this) {
        Tabs.PROFILE -> "Profile"
        Tabs.SUBMISSIONS -> "Submissions"
        Tabs.VISUALIZER -> "Progress"
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendDetailScreen(viewModel: FriendDetailViewModel, navController: NavController) {
    val scope = rememberCoroutineScope()

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(viewModel.handle ?: "Friend")
                        Text(
                            text = "Public profile",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navController::popBackStack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        val tabs = Tabs.entries.toTypedArray()
        val pagerState = rememberPagerState(initialPage = 0) {
            3
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(text = { Text(tab.label) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        })
                }
            }
            Box(Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 1040.dp)
                        .align(Alignment.Center),
                ) { page ->
                    LaunchedEffect(page) {
                        if (page != 0) viewModel.loadData()
                    }
                    when (tabs[page]) {
                        Tabs.PROFILE -> FriendInfoScreen(viewModel)
                        Tabs.SUBMISSIONS -> SubmissionsContent(viewModel, navController)
                        Tabs.VISUALIZER -> VisualizerContent(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun VisualizerContent(viewModel: FriendDetailViewModel) {
    val visualizerState by viewModel.visualizerData.collectAsState()
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val state = visualizerState) {
            is FriendsDetailState.Error -> {
                ErrorScreen(state.message, onRetry = viewModel::loadData)
            }

            FriendsDetailState.Loading -> {
                CircularProgressIndicator()
            }

            is FriendsDetailState.Success -> {
                CodeforcesCharts(state.data)
            }

            else -> {
                Text("No progress data yet.")
            }
        }
    }
}

@Composable
private fun SubmissionsContent(viewModel: FriendDetailViewModel, navController: NavController) {
    val submissionsState by viewModel.submissions.collectAsState()
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val state = submissionsState) {
            is FriendsDetailState.Error -> {
                ErrorScreen(state.message, onRetry = viewModel::loadData)
            }

            FriendsDetailState.Loading -> {
                CircularProgressIndicator()
            }

            is FriendsDetailState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.medium),
                    contentPadding = PaddingValues(AppSpacing.screen),
                ) {
                    items(
                        items = state.data,
                        key = { "${it.contestId}-${it.id}" },
                        contentType = { "submission" },
                    ) { submission ->
                        SubmissionCard(submission, toSubmission = {
                            navController.navigate(
                                MainScreens.WebView(
                                    url = "https://codeforces.com/contest/${submission.contestId}/submission/${submission.id}",
                                    title = "Submission",
                                )
                            )
                        })
                    }
                }
            }

            else -> {
                Text("No submissions found.")
            }
        }
    }
}

@Composable
private fun FriendInfoScreen(viewModel: FriendDetailViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val friendsData = uiState) {
            is FriendsDetailState.Loading -> {
                CircularProgressIndicator()
            }

            is FriendsDetailState.Success -> {
                ProfileContent(
                    showNavigateToSubmissions = false, userInfo = friendsData.data
                )
            }

            is FriendsDetailState.Error -> {
                ErrorScreen(friendsData.message, onRetry = viewModel::getFriendInfo)
            }

            else -> {
                Text("No profile data found.")
            }
        }
    }

}
