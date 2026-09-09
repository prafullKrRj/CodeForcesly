package com.prafullkumar.codeforcesly.visualizer.ui.charts

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.prafullkumar.codeforcesly.ui.theme.AppSpacing
import com.prafullkumar.codeforcesly.visualizer.ui.VisualizerData
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserLanguagesGraph(
    modifier: Modifier = Modifier,
    visualizerData: VisualizerData
) {
    if (visualizerData.languageFrequency.isEmpty()) return
    val colors = MaterialTheme.colorScheme
    val palette = remember(colors) {
        listOf(
            colors.primary,
            colors.secondary,
            colors.tertiary,
            colors.error,
            colors.inversePrimary,
            colors.primaryContainer
        )
    }
    val sortedEntries = remember(visualizerData.languageFrequency) {
        visualizerData.languageFrequency.entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            .map { it.key to it.value }
    }
    val entries = remember(sortedEntries) {
        buildList {
            addAll(sortedEntries.take(MAX_VISIBLE_ENTRIES))
            val otherFrequency = sortedEntries.drop(MAX_VISIBLE_ENTRIES).sumOf { it.second }
            if (otherFrequency > 0) add("Other" to otherFrequency)
        }
    }
    var pieData by remember(entries, palette) {
        mutableStateOf(entries.mapIndexed { index, (language, frequency) ->
            Pie(
                label = language,
                data = frequency.toDouble(),
                color = palette[index % palette.size],
                selectedColor = colors.inversePrimary
            )
        })
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PieChart(
            modifier = modifier
                .size(220.dp)
                .semantics {
                    contentDescription = "Programming languages chart. " + entries.joinToString {
                        "${it.first} ${it.second}"
                    }
                },
            data = pieData,
            onPieClick = {
                val pieIndex = pieData.indexOf(it)
                pieData = pieData.mapIndexed { mapIndex, pie ->
                    pie.copy(selected = pieIndex == mapIndex)
                }
            },
            selectedScale = 1.12f,
            scaleAnimEnterSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            colorAnimEnterSpec = tween(300),
            colorAnimExitSpec = tween(300),
            scaleAnimExitSpec = tween(300),
            spaceDegreeAnimExitSpec = tween(300),
            style = Pie.Style.Fill
        )
        val slicesByLanguage = pieData.associateBy { it.label }
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppSpacing.large),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.large),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.small),
            maxItemsInEachRow = 2
        ) {
            entries.forEach { (language, frequency) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                slicesByLanguage[language]?.color ?: colors.primary,
                                shape = MaterialTheme.shapes.small
                            )
                    )
                    Text(
                        text = "$language  $frequency",
                        modifier = Modifier.padding(start = AppSpacing.small),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

private const val MAX_VISIBLE_ENTRIES = 8
