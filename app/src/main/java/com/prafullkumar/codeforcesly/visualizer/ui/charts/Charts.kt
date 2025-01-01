package com.prafullkumar.codeforcesly.visualizer.ui.charts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.prafullkumar.codeforcesly.common.model.userrating.Rating
import com.prafullkumar.codeforcesly.common.model.userstatus.SubmissionDto
import com.prafullkumar.codeforcesly.visualizer.ui.VisualizerViewModel
import kotlin.random.Random


@Composable
fun CodeforcesCharts(
    submissions: List<SubmissionDto>,
    ratings: List<Rating>,
    viewModel: VisualizerViewModel
) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item("ratings") {
            GraphSection(title = "User Ratings") {
                UserRatingGraph(ratings, viewModel = viewModel)
            }
        }
        item("tags") {
            GraphSection(title = "Tags Frequency") {
                UserTagsDoughnutChart(viewModel = viewModel)
            }
        }
        item("verdicts") {
            GraphSection(title = "User Verdicts") {
                UserVerdictsGraph(viewModel = viewModel)
            }
        }
        item("index") {
            GraphSection(title = "Questions Solved by Index") {
                QuestionSolvedByIndexColumnChart(viewModel = viewModel)
            }
        }
        item("languages") {
            GraphSection(title = "Languages Used") {
                UserLanguagesGraph(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun GraphSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        content()
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = Color.Gray
        )
    }
}

fun getRandomMaterialColor(): Color {
    var red: Int
    var green: Int
    var blue: Int
    do {
        red = Random.nextInt(256)
        green = Random.nextInt(256)
        blue = Random.nextInt(256)
    } while ((red == 0 && green == 0 && blue == 0) || (red == 255 && green == 255 && blue == 255))
    return Color(red, green, blue, alpha = Random.nextInt(256))
}