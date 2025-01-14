package com.prafullkumar.codeforcesly.contests.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.prafullkumar.codeforcesly.common.ErrorScreen
import com.prafullkumar.codeforcesly.common.Resource
import com.prafullkumar.codeforcesly.navigateToProblemWebView
import com.prafullkumar.codeforcesly.problem.ui.ProblemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContestQuestions(
    viewModel: ContestQuestionViewModel = hiltViewModel(),
    navController: NavController,
) {
    val contestState by viewModel.state.collectAsState()
    when (val state = contestState) {
        is Resource.Error -> {
            ErrorScreen(state.message)
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
                            if (viewModel.contestDetails.name.isNullOrEmpty()) {
                                Text(viewModel.contestId.toString())
                            } else {
                                Text(viewModel.contestDetails.name!!)
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = navController::popBackStack) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = ""
                                )
                            }
                        },
                    )
                }
            ) {
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(it),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.data) { problem ->
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