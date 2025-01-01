package com.prafullkumar.codeforcesly.visualizer.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.codeforcesly.common.Resource
import com.prafullkumar.codeforcesly.common.model.userrating.Rating
import com.prafullkumar.codeforcesly.common.model.userstatus.SubmissionDto
import com.prafullkumar.codeforcesly.visualizer.domain.UserData
import com.prafullkumar.codeforcesly.visualizer.domain.VisualizerRepository
import com.prafullkumar.codeforcesly.visualizer.ui.charts.getRandomMaterialColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class VisualizerData(
    var ratingGraphRating: List<Double> = mutableListOf(),
    var ratingGraphDates: List<String> = mutableListOf(),
    var tagsFrequency: Map<String, Int> = mutableMapOf(),
    var verdictFrequency: Map<String?, Pair<Int, Color>> = mutableMapOf(),
    var indexCounts: Map<String, Int> = mutableMapOf(),
    var languageFrequency: Map<String?, Pair<Int, Color>> = mutableMapOf()
)

@HiltViewModel
class VisualizerViewModel @Inject constructor(
    private val repository: VisualizerRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<Resource<UserData>>(Resource.Loading)
    val uiState = _uiState.asStateFlow()


    var visualizerData by mutableStateOf(VisualizerData())

    init {
        getUserData()
    }

    fun getUserData() {
        _uiState.update {
            Resource.Loading
        }
        viewModelScope.launch {
            try {
                val userData = repository.getUserData()
                visualizerData = VisualizerDataGenerator.getVisualizerData(
                    userData.submissions,
                    userData.ratings
                )
                _uiState.update {
                    Resource.Success(userData)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    Resource.Error("Error: ${e.message}")
                }
            }
        }
    }
}

object VisualizerDataGenerator {
    private val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())

    fun getVisualizerData(submissions: List<SubmissionDto>, ratings: List<Rating>): VisualizerData {
        return VisualizerData(
            ratingGraphRating = getRatingGraphData(ratings),
            ratingGraphDates = getRatingGraphDates(ratings),
            tagsFrequency = getTagsFrequency(submissions),
            verdictFrequency = getVerdictFrequency(submissions),
            indexCounts = getSolvedIndexes(submissions),
            languageFrequency = getLanguageFrequency(submissions)
        )
    }

    private fun getRatingGraphData(ratings: List<Rating>): List<Double> {
        return ratings.map { it.newRating.toDouble() }
    }

    private fun getRatingGraphDates(ratings: List<Rating>): List<String> {
        return ratings.map { dateFormat.format(Date(it.ratingUpdateTimeSeconds * 1000L)) }
    }

    private fun getTagsFrequency(userSubmissions: List<SubmissionDto>): Map<String, Int> {
        return userSubmissions.flatMap { it.problem?.tags ?: emptySet() }.groupingBy { it }
            .eachCount()
    }

    private fun getVerdictFrequency(userSubmissions: List<SubmissionDto>): Map<String?, Pair<Int, Color>> {
        return userSubmissions.groupBy { it.verdict }.mapValues { it.value.size }
            .map { (verdict, frequency) ->
                verdict to Pair(frequency, getRandomMaterialColor())
            }.toMap()
    }

    private fun getSolvedIndexes(submissions: List<SubmissionDto>): Map<String, Int> {
        return submissions.filter { it.problem?.index != null && it.problem.index.isNotBlank() }
            .groupingBy { it.problem?.index!! }.eachCount()
    }

    private fun getLanguageFrequency(userSubmissions: List<SubmissionDto>): Map<String?, Pair<Int, Color>> {
        return userSubmissions.groupBy { it.programmingLanguage }.mapValues { it.value.size }
            .map { (language, frequency) ->
                language to Pair(frequency, getRandomMaterialColor())
            }.toMap()
    }
}