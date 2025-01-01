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
import com.prafullkumar.codeforcesly.visualizer.ui.VisualizerViewModel
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserTagsDoughnutChart(
    modifier: Modifier = Modifier,
    viewModel: VisualizerViewModel
) {
    if (viewModel.tagsFrequency.isEmpty()) return
    var doughnutData = viewModel.tagsFrequency.map { (tag, frequency) ->
        Pie(
            label = tag,
            data = frequency.toDouble(),
            color = getRandomMaterialColor(),
            selectedColor = Color.Green
        )
    }
    LazyRow(
        Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 200.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item("tags") {
            PieChart(
                modifier = modifier
                    .size(200.dp),
                data = doughnutData,
                onPieClick = {
                    println("Pie Clicked: $it")
                    val pieIndex = doughnutData.indexOf(it)
                    doughnutData =
                        doughnutData.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex) }
                },
                selectedScale = 1.2f,
                scaleAnimEnterSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
                ),
                colorAnimEnterSpec = tween(300),
                colorAnimExitSpec = tween(300),
                scaleAnimExitSpec = tween(300),
                spaceDegreeAnimExitSpec = tween(300),
                style = Pie.Style.Stroke(42.dp)
            )
        }
        item("tagsFrequency") {
            FlowRow(
                modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 4
            ) {
                viewModel.tagsFrequency.forEach { (tag, frequency) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(
                                    doughnutData.find { it.label == tag }?.color ?: Color.Gray,
                                    shape = MaterialTheme.shapes.small
                                )
                        )
                        Text(
                            text = "$tag: $frequency",
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
