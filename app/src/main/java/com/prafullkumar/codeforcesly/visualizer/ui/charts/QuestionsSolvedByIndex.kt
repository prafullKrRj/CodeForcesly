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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.prafullkumar.codeforcesly.visualizer.ui.VisualizerData
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DrawStyle

@Composable
fun QuestionSolvedByIndexColumnChart(
    modifier: Modifier = Modifier, visualizerData: VisualizerData
) {
    val data = visualizerData.indexCounts.map { (index, count) ->
        Bars(
            label = index,
            values = listOf(
                Bars.Data(
                    label = "Questions Solved",
                    value = count.toDouble(),
                    color = Brush.radialGradient(listOf(Color(0xFF23af92), Color(0xFF2BC0A1)))
                )
            ),
        )
    }

    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.Center
    ) {
        ColumnChart(
            modifier = modifier
                .padding(horizontal = 22.dp)
                .width((data.size * 50).dp)
                .height(300.dp),
            data = data,
            barProperties = BarProperties(
                spacing = 3.dp, thickness = 20.dp, style = DrawStyle.Fill
            ),
            animationSpec = tween(250),

            )
    }
}