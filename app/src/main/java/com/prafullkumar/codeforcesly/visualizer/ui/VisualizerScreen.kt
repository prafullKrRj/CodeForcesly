package com.prafullkumar.codeforcesly.visualizer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.prafullkumar.codeforcesly.common.ErrorScreen
import com.prafullkumar.codeforcesly.common.Resource
import com.prafullkumar.codeforcesly.visualizer.ui.charts.CodeforcesCharts

@Composable
fun VisualizerScreen(viewModel: VisualizerViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is Resource.Loading -> {
                CircularProgressIndicator()
            }

            is Resource.Success -> {
                CodeforcesCharts(
                    viewModel.visualizerData
                )
            }

            is Resource.Error -> {
                ErrorScreen((uiState as Resource.Error).message, onRetry = viewModel::getUserData)
            }
        }
    }
}