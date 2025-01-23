package com.prafullkumar.codeforcesly.visualizer.ui.charts

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prafullkumar.codeforcesly.visualizer.ui.VisualizerData
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserLanguagesGraph(
    modifier: Modifier = Modifier,
    visualizerData: VisualizerData
) {
    var pieData = visualizerData.languageFrequency.map { (language, frequency) ->
        Pie(
            label = language,
            data = frequency.first.toDouble(),
            color = frequency.second,
            selectedColor = Color.Green
        )
    }
    LazyRow(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .defaultMinSize(minHeight = 200.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            PieChart(
                modifier = modifier.size(200.dp),
                data = pieData,
                onPieClick = {
                    val pieIndex = pieData.indexOf(it)
                    pieData =
                        pieData.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex) }
                },
                selectedScale = 1.2f,
                scaleAnimEnterSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
                ),
                colorAnimEnterSpec = tween(300),
                colorAnimExitSpec = tween(300),
                scaleAnimExitSpec = tween(300),
                spaceDegreeAnimExitSpec = tween(300),
                style = Pie.Style.Fill
            )
        }
        item {
            FlowRow(
                modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 4
            ) {
                visualizerData.languageFrequency.forEach { (language, frequency) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(
                                    frequency.second,
                                    shape = MaterialTheme.shapes.small
                                )
                        )
                        Text(
                            text = "$language: ${frequency.first}",
                            modifier = Modifier.padding(start = 8.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

    }
}