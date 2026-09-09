package com.prafullkumar.codeforcesly.problem.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prafullkumar.codeforcesly.R
import com.prafullkumar.codeforcesly.common.ErrorScreen
import com.prafullkumar.codeforcesly.problem.domain.model.Problem
import com.prafullkumar.codeforcesly.ui.theme.AppSpacing

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
                    title = {
                        Column {
                            Text("Problem lab")
                            Text(
                                text = if (uiState.isLoading) "Loading public problemset…"
                                else "${uiState.problems.size} problems ready",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (uiState.isLoading && uiState.problems.isEmpty()) {
                    LoadingIndicator()
                } else if (uiState.error != null && uiState.problems.isEmpty()) {
                    ErrorScreen(
                        message = uiState.error ?: "Could not load public problems",
                        onRetry = {
                        viewModel.refreshState()
                        }
                    )
                } else {
                    ProblemsContent(
                        uiState = uiState,
                        onProblemClick = onProblemClick,
                        modifier = Modifier.fillMaxSize(),
                        viewModel
                    )
                    if (uiState.isLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
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
        trailingIcon = if (query.isNotBlank()) {
            {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                }
            }
        } else null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
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
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
        ) {
            Text(
                text = "Rating ${selectedRatingRange.start} – ${selectedRatingRange.endInclusive}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.weight(1f))
            FilterChip(
                selected = false,
                onClick = {
                    onSortOrderChange(
                        when (sortOrder) {
                            SortOrder.RATING_DESC -> SortOrder.RATING_ASC
                            SortOrder.RATING_ASC -> SortOrder.NAME_DESC
                            SortOrder.NAME_DESC -> SortOrder.NAME_ASC
                            SortOrder.NAME_ASC -> SortOrder.RATING_DESC
                        }
                    )
                },
                label = { Text(sortOrder.label()) },
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            if (sortOrder == SortOrder.RATING_ASC || sortOrder == SortOrder.NAME_ASC) {
                                R.drawable.baseline_arrow_upward_24
                            } else {
                                R.drawable.baseline_arrow_downward_24
                            }
                        ),
                        contentDescription = null
                    )
                }
            )
        }
        RangeSlider(
            value = selectedRatingRange.start.toFloat()..selectedRatingRange.endInclusive.toFloat(),
            onValueChange = { range ->
                val start = range.start.coerceAtMost(range.endInclusive)
                val end = range.endInclusive.coerceAtLeast(range.start)
                onRatingRangeChange(start.toInt()..end.toInt())
            },
            valueRange = 800f..3500f,
            steps = 27
        )
    }
}

private fun SortOrder.label(): String = when (this) {
    SortOrder.RATING_ASC -> "Rating ↑"
    SortOrder.RATING_DESC -> "Rating ↓"
    SortOrder.NAME_ASC -> "Name A–Z"
    SortOrder.NAME_DESC -> "Name Z–A"
}

@Composable
private fun TagsRow(
    tags: List<String>,
    selectedTags: Set<String>,
    onTagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
    ) {
        items(items = tags, key = { it }, contentType = { "tag" }) { tag ->
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
    uiState: ProblemsUiState,
    onProblemClick: (Problem) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProblemsViewModel
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 360.dp),
        modifier = modifier,
        contentPadding = PaddingValues(vertical = AppSpacing.small),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.small),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
    ) {
        if (uiState.recommendedProblems.isNotEmpty() || uiState.isRecommendationsLoading) {
            item(key = "recommendations", span = { GridItemSpan(maxLineSpan) }) {
                RecommendationsStrip(
                    problems = uiState.recommendedProblems,
                    isLoading = uiState.isRecommendationsLoading,
                    onProblemClick = onProblemClick
                )
            }
        }
        item(key = "search", span = { GridItemSpan(maxLineSpan) }) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.screen)
            )
        }
        item(key = "filters", span = { GridItemSpan(maxLineSpan) }) {
            FiltersRow(
                selectedRatingRange = uiState.selectedRatingRange,
                onRatingRangeChange = viewModel::updateRatingRange,
                sortOrder = uiState.sortOrder,
                onSortOrderChange = viewModel::updateSortOrder,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.screen)
            )
        }
        val hasActiveFilters = uiState.searchQuery.isNotBlank() ||
            uiState.selectedTags.isNotEmpty() ||
            uiState.selectedRatingRange != (800..3500) ||
            uiState.sortOrder != SortOrder.RATING_DESC
        if (hasActiveFilters) {
            item(key = "clear-filters", span = { GridItemSpan(maxLineSpan) }) {
                TextButton(
                    onClick = viewModel::clearFilters,
                    modifier = Modifier.padding(horizontal = AppSpacing.screen)
                ) {
                    Text("Clear filters")
                }
            }
        }
        item(key = "tags", span = { GridItemSpan(maxLineSpan) }) {
            TagsRow(
                tags = uiState.popularTags,
                selectedTags = uiState.selectedTags,
                onTagClick = viewModel::toggleTag,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.screen)
            )
        }
        if (uiState.problems.isEmpty()) {
            item(key = "empty-problems", span = { GridItemSpan(maxLineSpan) }) {
                EmptyProblemsState(
                    query = uiState.searchQuery,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )
            }
        } else {
            items(
                items = uiState.problems,
                key = { problem -> "${problem.contestId ?: "archive"}-${problem.index}" },
                contentType = { "problem" }
            ) { problem ->
                ProblemCard(
                    problem = problem,
                    isSolved = "${problem.contestId ?: "archive"}-${problem.index}" in uiState.solvedProblemKeys,
                    onClick = { onProblemClick(problem) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.screen / 2)
                )
            }
        }
    }
}

@Composable
private fun RecommendationsStrip(
    problems: List<Problem>,
    isLoading: Boolean,
    onProblemClick: (Problem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = AppSpacing.small)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AppSpacing.screen),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Practice next", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Matched to your rating and accepted topics",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.width(20.dp),
                    strokeWidth = 2.dp
                )
            }
        }
        if (problems.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = AppSpacing.screen),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.small),
                modifier = Modifier.padding(top = AppSpacing.small)
            ) {
                items(
                    items = problems,
                    key = { problem -> "recommendation-${problem.contestId ?: "archive"}-${problem.index}" },
                    contentType = { "problem-recommendation" }
                ) { problem ->
                    Card(
                        onClick = { onProblemClick(problem) },
                        modifier = Modifier.width(224.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(AppSpacing.medium)) {
                            Text(
                                text = problem.index,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = problem.name,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = AppSpacing.extraSmall)
                            )
                            Text(
                                text = "${problem.rating?.toInt() ?: "Unrated"} • ${problem.tags.orEmpty().firstOrNull() ?: "practice"}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(top = AppSpacing.small)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyProblemsState(query: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(AppSpacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (query.isBlank()) "No problems in this range" else "No matching problems",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Try widening rating range or clearing filters.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = AppSpacing.small)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProblemCard(
    problem: Problem,
    onClick: () -> Unit,
    isSolved: Boolean = false,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.large)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Text(
                            text = problem.index,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                    if (isSolved) {
                        Surface(
                            modifier = Modifier.padding(start = AppSpacing.small),
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = AppSpacing.small, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.width(16.dp)
                                )
                                Text(
                                    text = "Solved",
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(start = AppSpacing.extraSmall)
                                )
                            }
                        }
                    }
                }
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = AppSpacing.small),
                    text = problem.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                problem.rating?.let {
                    Text(
                        text = it.toInt().toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = getRatingColor(it),
                        modifier = Modifier.padding(end = AppSpacing.small)
                    )
                }
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            if (isExpanded) R.drawable.baseline_keyboard_arrow_up_24
                            else R.drawable.baseline_keyboard_arrow_down_24
                        ),
                        contentDescription = if (isExpanded) "Hide tags" else "Show tags"
                    )
                }
            }

            Text(
                text = "${problem.type ?: "Problem"}  •  ${problem.tags.orEmpty().size} tags",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = AppSpacing.small)
            )

            if (isExpanded) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    problem.tags.orEmpty().forEach { tag ->
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = tag,
                                modifier = Modifier.padding(
                                    horizontal = AppSpacing.small,
                                    vertical = AppSpacing.extraSmall
                                ),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
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
    val colors = MaterialTheme.colorScheme
    return when {
        rating < 1200 -> colors.onSurfaceVariant
        rating < 1400 -> colors.secondary
        rating < 1600 -> colors.tertiary
        rating < 1900 -> colors.primary
        rating < 2100 -> colors.primary
        rating < 2400 -> colors.error
        else -> colors.error
    }
}
