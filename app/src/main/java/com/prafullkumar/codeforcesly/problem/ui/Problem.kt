package com.prafullkumar.codeforcesly.problem.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.prafullkumar.codeforcesly.R
import com.prafullkumar.codeforcesly.common.ErrorScreen
import com.prafullkumar.codeforcesly.problem.domain.model.Problem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemsScreen(
    viewModel: ProblemsViewModel,
    onProblemClick: (Problem) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val refreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        state = refreshState,
        onRefresh = viewModel::refreshState,
        isRefreshing = viewModel.isRefreshing
    ) {
        Scaffold(
            Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Codeforces Problems") },
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (uiState.isLoading) {
                    LoadingIndicator()
                } else if (uiState.error != null) {
                    ErrorScreen("Error in Loading try refreshing..") {
                        viewModel.refreshState()
                    }
                } else {
                    ProblemsContent(
                        problems = uiState.problems,
                        onProblemClick = onProblemClick,
                        modifier = Modifier.fillMaxSize(),
                        viewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search problems...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        ),
        singleLine = true
    )
}

@Composable
private fun FiltersRow(
    selectedRatingRange: ClosedRange<Int>,
    onRatingRangeChange: (ClosedRange<Int>) -> Unit,
    sortOrder: SortOrder,
    onSortOrderChange: (SortOrder) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RangeSlider(
            value = selectedRatingRange.start.toFloat()..selectedRatingRange.endInclusive.toFloat(),
            onValueChange = { range ->
                val start = range.start.coerceAtMost(range.endInclusive)
                val end = range.endInclusive.coerceAtLeast(range.start)
                onRatingRangeChange(start.toInt()..end.toInt())
            },
            valueRange = 800f..3500f,
            steps = 27,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = {
            onSortOrderChange(
                when (sortOrder) {
                    SortOrder.RATING_DESC -> SortOrder.RATING_ASC
                    SortOrder.RATING_ASC -> SortOrder.NAME_DESC
                    SortOrder.NAME_DESC -> SortOrder.NAME_ASC
                    SortOrder.NAME_ASC -> SortOrder.RATING_DESC
                }
            )
        }) {
            Icon(
                imageVector = when (sortOrder) {
                    SortOrder.RATING_DESC, SortOrder.NAME_DESC -> ImageVector.vectorResource(R.drawable.baseline_arrow_downward_24)
                    SortOrder.RATING_ASC, SortOrder.NAME_ASC -> ImageVector.vectorResource(R.drawable.baseline_arrow_upward_24)
                },
                contentDescription = "Sort"
            )
        }
    }
}

@Composable
private fun TagsRow(
    selectedTags: Set<String>,
    onTagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tags = listOf("dp", "graphs", "implementation", "math", "greedy", "strings")
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tags) { tag ->
            FilterChip(
                selected = selectedTags.contains(tag),
                onClick = { onTagClick(tag) },
                label = { Text(tag) },
                leadingIcon = if (selectedTags.contains(tag)) {
                    { Icon(Icons.Default.Check, contentDescription = null) }
                } else null
            )
        }
    }
}

@Composable
private fun ProblemsContent(
    problems: List<Problem>,
    onProblemClick: (Problem) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProblemsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    SearchBar(
        query = uiState.searchQuery,
        onQueryChange = viewModel::updateSearchQuery,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
    FiltersRow(
        selectedRatingRange = uiState.selectedRatingRange,
        onRatingRangeChange = viewModel::updateRatingRange,
        sortOrder = uiState.sortOrder,
        onSortOrderChange = viewModel::updateSortOrder,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
    TagsRow(
        selectedTags = uiState.selectedTags,
        onTagClick = viewModel::toggleTag,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(problems, key = {
            it.index + "-" + it.name + it.rating
        }) { problem ->
            ProblemCard(
                problem = problem,
                onClick = { onProblemClick(problem) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProblemCard(
    problem: Problem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.weight(.8f),
                    text = "${problem.index}. ${problem.name}",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    Modifier.weight(.2f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    problem.rating?.let {
                        Text(
                            text = it.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = getRatingColor(it)
                        )
                    }
                    IconButton(
                        modifier = Modifier.weight(1f),
                        onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(
                                if (isExpanded) R.drawable.baseline_keyboard_arrow_up_24
                                else R.drawable.baseline_keyboard_arrow_down_24
                            ),
                            contentDescription = "Expand"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isExpanded) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    problem.tags.forEach { tag ->
                        AssistChip(
                            onClick = { },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            ),
                            label = {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorMessage(error: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun getRatingColor(rating: Double): Color {
    return when {
        rating < 1200 -> Color.Gray
        rating < 1400 -> Color.Green
        rating < 1600 -> Color(0xFF03A89E) // Cyan
        rating < 1900 -> Color.Blue
        rating < 2100 -> Color(0xFFAA00AA) // Purple
        rating < 2400 -> Color(0xFFFF8C00) // Orange
        else -> Color.Red
    }
}