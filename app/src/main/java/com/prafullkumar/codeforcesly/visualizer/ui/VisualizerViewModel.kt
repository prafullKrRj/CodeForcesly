package com.prafullkumar.codeforcesly.visualizer.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.codeforcesly.common.Resource
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


@HiltViewModel
class VisualizerViewModel @Inject constructor(
    private val repository: VisualizerRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<Resource<UserData>>(Resource.Loading)
    val uiState = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())

    var ratingGraphRating: List<Double> = mutableListOf()
    var ratingGraphDates: List<String> = mutableListOf()
    var tagsFrequency: Map<String, Int> = mutableMapOf()
    var verdictFrequency: Map<String?, Pair<Int, Color>> = mutableMapOf()
    var indexCounts: Map<String, Int> = mutableMapOf()
    var languageFrequency: Map<String?, Pair<Int, Color>> = mutableMapOf()

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
                ratingGraphRating = userData.ratings.map { it.newRating.toDouble() }
                ratingGraphDates =
                    userData.ratings.map { dateFormat.format(Date(it.ratingUpdateTimeSeconds * 1000L)) }
                getTagsFrequency(userData.submissions)
                getVerdictFrequency(userData.submissions)
                getSolvedIndexes(userData.submissions)
                getLanguageFrequency(userData.submissions)
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

    private fun getTagsFrequency(userSubmissions: List<SubmissionDto>) {
        tagsFrequency =
            userSubmissions.flatMap { it.problem?.tags ?: emptySet() }.groupingBy { it }.eachCount()
    }

    private fun getVerdictFrequency(userSubmissions: List<SubmissionDto>) {
        verdictFrequency = userSubmissions.groupBy { it.verdict }.mapValues { it.value.size }
            .map { (verdict, frequency) ->
                verdict to Pair(frequency, getRandomMaterialColor())
            }.toMap()
    }

    private fun getSolvedIndexes(submissions: List<SubmissionDto>) {
        indexCounts =
            submissions.filter { it.problem?.index != null && it.problem.index.isNotBlank() }
                .groupingBy { it.problem?.index!! }.eachCount()
    }

    private fun getLanguageFrequency(userSubmissions: List<SubmissionDto>) {
        languageFrequency =
            userSubmissions.groupBy { it.programmingLanguage }.mapValues { it.value.size }
                .map { (language, frequency) ->
                    language to Pair(frequency, getRandomMaterialColor())
                }.toMap()
    }
}