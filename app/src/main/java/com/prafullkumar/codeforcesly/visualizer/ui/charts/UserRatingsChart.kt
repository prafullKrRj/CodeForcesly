package com.prafullkumar.codeforcesly.visualizer.ui.charts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.prafullkumar.codeforcesly.ui.theme.AppSpacing
import com.prafullkumar.codeforcesly.visualizer.ui.VisualizerData
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import kotlin.math.roundToInt

internal data class RatingChartBounds(
    val minValue: Double,
    val maxValue: Double
)

internal fun ratingChartBounds(ratings: List<Double>): RatingChartBounds? {
    val minimumRating = ratings.minOrNull() ?: return null
    val maximumRating = ratings.maxOrNull() ?: return null
    return RatingChartBounds(
        minValue = minimumRating - 200.0,
        maxValue = maxOf(maximumRating + 200.0, minimumRating + 1.0)
    )
}

@Composable
fun UserRatingGraph(
    modifier: Modifier = Modifier,
    visualizerData: VisualizerData
) {
    if (visualizerData.ratingGraphRating.isEmpty() || visualizerData.ratingGraphDates.isEmpty()) {
        return
    }
    val colors = MaterialTheme.colorScheme
    val ratings = visualizerData.ratingGraphRating
    val bounds = ratingChartBounds(ratings) ?: return
    val currentRating = ratings.last().roundToInt()
    val peakRating = ratings.maxOrNull()!!.roundToInt()
    val startingRating = ratings.first().roundToInt()
    val ratingChange = currentRating - startingRating
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RatingStat("Current", currentRating.toString())
            RatingStat("Peak", peakRating.toString())
            RatingStat(
                "Change",
                when {
                    ratingChange > 0 -> "+$ratingChange"
                    ratingChange < 0 -> ratingChange.toString()
                    else -> "—"
                },
                valueColor = when {
                    ratingChange > 0 -> colors.primary
                    ratingChange < 0 -> colors.error
                    else -> colors.onSurfaceVariant
                }
            )
        }
        LineChart(
            animationDelay = 0,
            modifier = modifier
                .fillMaxWidth()
                .height(220.dp)
                .semantics {
                    contentDescription = "Rating chart. Current $currentRating, peak $peakRating, change $ratingChange."
                },
            data = listOf(
                Line(
                    label = "Rating",
                    values = visualizerData.ratingGraphRating,
                    color = SolidColor(colors.primary),
                    firstGradientFillColor = colors.primaryContainer.copy(alpha = .7f),
                    secondGradientFillColor = colors.surfaceContainerLow,
                )
            ),
            labelProperties = LabelProperties(
                labels = visualizerData.ratingGraphDates,
                enabled = false,
                textStyle = TextStyle.Default
            ),
            indicatorProperties = HorizontalIndicatorProperties(
                enabled = true,
                count = IndicatorCount.CountBased(7),
                textStyle = TextStyle(color = colors.onSurfaceVariant),
                contentBuilder = { indicator ->
                    indicator.toInt().toString()
                },
            ),
            labelHelperProperties = LabelHelperProperties(
                textStyle = TextStyle(color = colors.onSurfaceVariant)
            ),
            minValue = bounds.minValue,
            maxValue = bounds.maxValue,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppSpacing.small),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = visualizerData.ratingGraphDates.first(),
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant
            )
            Text(
                text = visualizerData.ratingGraphDates.last(),
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RatingStat(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Column {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = valueColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
