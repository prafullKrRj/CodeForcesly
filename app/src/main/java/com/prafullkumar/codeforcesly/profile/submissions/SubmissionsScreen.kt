package com.prafullkumar.codeforcesly.profile.submissions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.prafullkumar.codeforcesly.MainScreens
import com.prafullkumar.codeforcesly.R
import com.prafullkumar.codeforcesly.common.ErrorScreen
import com.prafullkumar.codeforcesly.common.model.userstatus.SubmissionDto
import com.prafullkumar.codeforcesly.ui.theme.AppSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmissionsScreen(
    viewModel: SubmissionsViewModel,
    navController: NavController,
) {
    val submissionsState = viewModel.submissions.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Submissions")
                        Text(
                            "${submissionsState.itemCount} loaded · newest first",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navController::popBackStack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        SubmissionContent(submissionsState, padding, navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubmissionContent(
    submissionsState: LazyPagingItems<SubmissionDto>,
    padding: PaddingValues,
    navController: NavController
) {
    val refreshLoadState = submissionsState.loadState.refresh
    androidx.compose.material3.pulltorefresh.PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = refreshLoadState is LoadState.Loading && submissionsState.itemCount > 0,
        onRefresh = submissionsState::refresh
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (refreshLoadState is LoadState.Error && submissionsState.itemCount == 0) {
                ErrorScreen(
                    message = "Could not load submissions",
                    onRetry = submissionsState::retry
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 1040.dp)
                        .align(Alignment.TopCenter),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.small),
                    contentPadding = PaddingValues(AppSpacing.screen),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (refreshLoadState is LoadState.Loading) {
                        item {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    items(
                        count = submissionsState.itemCount,
                        key = { index ->
                            submissionsState.peek(index)?.let { "${it.contestId}-${it.id}" }
                                ?: "placeholder-$index"
                        },
                        contentType = { "submission" }
                    ) { index ->
                        val submission = submissionsState[index]
                        submission?.let {
                            SubmissionCard(submission = it, toSubmission = {
                                navController.navigate(
                                    MainScreens.WebView(
                                        url = "https://codeforces.com/contest/${submission.contestId}/submission/${submission.id}",
                                        title = "Submission",
                                    )
                                )
                            })
                        }
                    }

                    when (submissionsState.loadState.append) {
                        is LoadState.Loading -> item { CircularProgressIndicator() }
                        is LoadState.Error -> item {
                            ErrorScreen(
                                message = "Could not load more submissions",
                                onRetry = submissionsState::retry
                            )
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubmissionCard(
    submission: SubmissionDto,
    modifier: Modifier = Modifier,
    toSubmission: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        onClick = { expanded = !expanded },
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        )
    ) {
        Column(
            modifier = Modifier
                .padding(AppSpacing.large)
        ) {
            // Problem Title and Rating Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                submission.problem?.name?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = if (expanded) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                submission.problem?.rating?.let { RatingBadge(rating = it) }
            }

            Spacer(modifier = Modifier.height(AppSpacing.small))

            // Contest and Time Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Contest #${submission.contestId}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = formatTimeAgo(submission.creationTimeSeconds?.toLong() ?: 0),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.medium))

            // Verdict Status

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                submission.verdict?.let { VerdictChip(verdict = it) }
                TextButton(onClick = toSubmission) { Text("Open") }
            }


            // Expanded Content
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = AppSpacing.medium)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                    Spacer(modifier = Modifier.height(AppSpacing.medium))

                    // Programming Language
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_code_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(AppSpacing.small))
                        submission.programmingLanguage?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Memory Usage
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_code_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(AppSpacing.small))
                        Text(
                            text = formatMemory(submission.memoryConsumedBytes?.toLong() ?: 0),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Time Usage
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_timeline_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(AppSpacing.small))
                        Text(
                            text = "${submission.timeConsumedMillis} ms",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Problem Tags
                    if (submission.problem?.tags?.isNotEmpty() == true) {
                        Spacer(modifier = Modifier.height(AppSpacing.medium))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.extraSmall),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.extraSmall)
                        ) {
                            submission.problem.tags.forEach { tag ->
                                TagChip(tag = tag)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RatingBadge(rating: Int) {
    val colors = MaterialTheme.colorScheme
    val backgroundColor = when {
        rating < 1200 -> colors.onSurfaceVariant
        rating < 1400 -> colors.secondary
        rating < 1600 -> colors.tertiary
        rating < 1900 -> colors.primary
        rating < 2100 -> colors.primary
        else -> colors.error
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = backgroundColor.copy(alpha = 0.16f),
        modifier = Modifier.padding(start = AppSpacing.small)
    ) {
        Text(
            text = "$rating",
            style = MaterialTheme.typography.labelMedium,
            color = backgroundColor,
            modifier = Modifier.padding(horizontal = AppSpacing.small, vertical = AppSpacing.extraSmall)
        )
    }
}

@Composable
fun VerdictChip(verdict: String) {
    val (backgroundColor, textColor) = when (verdict) {
        "OK" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "WRONG_ANSWER" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        "TIME_LIMIT_EXCEEDED" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = backgroundColor,
        modifier = Modifier
            .wrapContentWidth()
    ) {
        Text(
            text = verdict.replace("_", " "),
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun TagChip(tag: String) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
        modifier = Modifier.height(24.dp)
    ) {
        Text(
            text = tag,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// Utility functions
fun formatTimeAgo(timeSeconds: Long): String {
    val now = System.currentTimeMillis() / 1000
    val diff = now - timeSeconds
    return when {
        diff < 60 -> "just now"
        diff < 3600 -> "${diff / 60}m ago"
        diff < 86400 -> "${diff / 3600}h ago"
        else -> "${diff / 86400}d ago"
    }
}

fun formatMemory(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> "${bytes / (1024 * 1024)} MB"
    }
}
