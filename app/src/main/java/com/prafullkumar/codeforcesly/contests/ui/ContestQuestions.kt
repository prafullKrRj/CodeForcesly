package com.prafullkumar.codeforcesly.contests.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.prafullkumar.codeforcesly.common.ErrorScreen
import com.prafullkumar.codeforcesly.common.Resource
import com.prafullkumar.codeforcesly.navigateToProblemWebView
import com.prafullkumar.codeforcesly.problem.ui.ProblemCard
import com.prafullkumar.codeforcesly.ui.theme.AppSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContestQuestions(
    contestId: Int,
    viewModel: ContestQuestionViewModel = hiltViewModel(),
    navController: NavController,
) {
    val contestState by viewModel.state.collectAsState()
    when (val state = contestState) {
        is Resource.Error -> {
            ErrorScreen(state.message, onRetry = viewModel::load)
        }

        Resource.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is Resource.Success -> {
            Scaffold(
                Modifier.fillMaxSize(), topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    viewModel.contestDetails.name
                                        ?: "Contest $contestId",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = "${state.data.size} problems · tap to open",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = navController::popBackStack) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                    )
                }
            ) {
                Box(Modifier.fillMaxSize()) {
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .widthIn(max = 1040.dp)
                            .align(Alignment.Center)
                            .padding(it),
                        contentPadding = PaddingValues(AppSpacing.screen),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.medium),
                    ) {
                        items(
                            items = state.data,
                            key = { "${it.contestId ?: contestId}-${it.index}" },
                            contentType = { "problem" },
                        ) { problem ->
                            ProblemCard(
                                problem = problem,
                                onClick = {
                                    navController.navigateToProblemWebView(problem)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
