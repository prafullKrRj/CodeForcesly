package com.prafullkumar.codeforcesly.visualizer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.prafullkumar.codeforcesly.common.ErrorScreen
import com.prafullkumar.codeforcesly.common.Resource
import com.prafullkumar.codeforcesly.visualizer.ui.charts.CodeforcesCharts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualizerScreen(viewModel: VisualizerViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing = viewModel.isRefreshing
    val refreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        state = refreshState,
        onRefresh = { viewModel.getUserData(forceRefresh = true) }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Progress lab")
                            Text(
                                "Your Codeforces signals, in one place",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                when (val state = uiState) {
                    is Resource.Loading -> CircularProgressIndicator()
                    is Resource.Success -> CodeforcesCharts(viewModel.visualizerData)
                    is Resource.Error -> ErrorScreen(
                        message = state.message.ifBlank { "Could not load your public stats" },
                        onRetry = { viewModel.getUserData(forceRefresh = true) }
                    )
                }
            }
        }
    }
}
