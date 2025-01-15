package com.prafullkumar.codeforcesly.visualizer.ui.charts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.prafullkumar.codeforcesly.visualizer.ui.VisualizerData
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line

@Composable
fun UserRatingGraph(
    modifier: Modifier = Modifier,
    visualizerData: VisualizerData
) {
    if (visualizerData.ratingGraphRating.isEmpty() || visualizerData.ratingGraphDates.isEmpty()) {
        return
    }
    Column {
        LineChart(
            animationDelay = 20,
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp),
            data = listOf(
                Line(
                    label = "Rating",
                    values = visualizerData.ratingGraphRating,
                    color = SolidColor(Color(0xFF23af92)),
                    firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                    secondGradientFillColor = Color.Transparent,
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
                contentBuilder = { indicator ->
                    indicator.toInt().toString()
                },
            )
        )
    }
}