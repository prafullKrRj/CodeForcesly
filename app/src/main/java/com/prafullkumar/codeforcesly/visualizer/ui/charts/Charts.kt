package com.prafullkumar.codeforcesly.visualizer.ui.charts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prafullkumar.codeforcesly.visualizer.ui.VisualizerData
import com.prafullkumar.codeforcesly.ui.theme.AppSpacing


@Composable
fun CodeforcesCharts(
    visualizerData: VisualizerData
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 360.dp),
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppSpacing.screen),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.medium),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.medium)
    ) {
        item(
            key = "summary",
            contentType = "summary",
            span = { GridItemSpan(maxLineSpan) }
        ) {
            ProgressSummary(visualizerData)
        }
        if (visualizerData.ratingGraphRating.isNotEmpty()) {
            item(key = "ratings", contentType = "graph") {
                GraphSection(
                    title = "Rating trajectory",
                    supportingText = "How your contest rating has moved"
                ) {
                    UserRatingGraph(visualizerData = visualizerData)
                }
            }
        }
        if (visualizerData.tagsFrequency.isNotEmpty()) {
            item(key = "tags", contentType = "graph") {
                GraphSection(
                    title = "Problem topics",
                    supportingText = "Tags across your submissions"
                ) {
                    UserTagsDoughnutChart(visualizerData = visualizerData)
                }
            }
        }
        if (visualizerData.verdictFrequency.isNotEmpty()) {
            item(key = "verdicts", contentType = "graph") {
                GraphSection(
                    title = "Submission outcomes",
                    supportingText = "Where your attempts are landing"
                ) {
                    UserVerdictsGraph(visualizerData = visualizerData)
                }
            }
        }
        if (visualizerData.indexCounts.isNotEmpty()) {
            item(key = "index", contentType = "graph") {
                GraphSection(
                    title = "Solved by index",
                    supportingText = "Your solved volume by problem position"
                ) {
                    QuestionSolvedByIndexColumnChart(visualizerData = visualizerData)
                }
            }
        }
        if (visualizerData.languageFrequency.isNotEmpty()) {
            item(key = "languages", contentType = "graph") {
                GraphSection(
                    title = "Languages",
                    supportingText = "Languages used in submissions"
                ) {
                    UserLanguagesGraph(visualizerData = visualizerData)
                }
            }
        }
    }
}

@Composable
private fun ProgressSummary(data: VisualizerData) {
    val solved = data.indexCounts.values.sum()
    val submissions = data.verdictFrequency.values.sum()
    val accepted = data.verdictFrequency["OK"] ?: 0
    val peakRating = data.ratingGraphRating.maxOrNull()?.toInt() ?: 0
    val acceptanceRate = if (submissions == 0) {
        "—"
    } else {
        "${(accepted * 100f / submissions).toInt()}%"
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.extraLarge)) {
            Text(
                text = "Your progress at a glance",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            if (data.latestContestName.isNotBlank()) {
                Text(
                    text = "Latest contest · ${data.latestContestName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = AppSpacing.extraSmall)
                )
                data.latestRatingDelta?.let { delta ->
                    val sign = if (delta > 0) "+" else ""
                    Text(
                        text = "Rating change $sign$delta · rank ${data.latestRank ?: "—"}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f),
                        modifier = Modifier.padding(top = AppSpacing.extraSmall)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppSpacing.large),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
            ) {
                Metric("Solved", solved.toString(), Modifier.weight(1f))
                Metric("Attempts", submissions.toString(), Modifier.weight(1f))
                Metric("Peak", if (peakRating == 0) "—" else peakRating.toString(), Modifier.weight(1f))
                Metric("Accepted", acceptanceRate, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Metric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f)
        )
    }
}

@Composable
fun GraphSection(
    title: String,
    supportingText: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.extraLarge)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = AppSpacing.extraSmall, bottom = AppSpacing.large)
            )
            content()
        }
    }
}
