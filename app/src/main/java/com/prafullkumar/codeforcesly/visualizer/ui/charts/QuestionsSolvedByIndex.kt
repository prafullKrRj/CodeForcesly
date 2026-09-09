package com.prafullkumar.codeforcesly.visualizer.ui.charts

import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import com.prafullkumar.codeforcesly.ui.theme.AppSpacing
import com.prafullkumar.codeforcesly.visualizer.ui.VisualizerData
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DrawStyle

@Composable
fun QuestionSolvedByIndexColumnChart(
    modifier: Modifier = Modifier, visualizerData: VisualizerData
) {
    if (visualizerData.indexCounts.isEmpty()) return
    val colors = MaterialTheme.colorScheme
    val data = remember(visualizerData.indexCounts, colors) {
        visualizerData.indexCounts.toSortedMap().map { (index, count) ->
            Bars(
                label = index,
                values = listOf(
                    Bars.Data(
                        label = "Questions Solved",
                        value = count.toDouble(),
                        color = Brush.verticalGradient(
                            listOf(colors.primary, colors.primaryContainer)
                        )
                    )
                ),
            )
        }
    }

    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.Center
    ) {
        ColumnChart(
            modifier = modifier
                .padding(horizontal = AppSpacing.extraLarge)
                .width(maxOf(data.size * 56, 280).dp)
                .height(300.dp)
                .semantics {
                    contentDescription = "Solved problems by index chart. " +
                        data.joinToString {
                            "${it.label} ${it.values.firstOrNull()?.value?.toInt() ?: 0}"
                        }
                },
            data = data,
            barProperties = BarProperties(
                spacing = 3.dp, thickness = 20.dp, style = DrawStyle.Fill
            ),
            animationSpec = tween(250),

            )
    }
}
