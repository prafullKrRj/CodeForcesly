package com.prafullkumar.codeforcesly.visualizer.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.codeforcesly.common.Resource
import com.prafullkumar.codeforcesly.common.model.userrating.Rating
import com.prafullkumar.codeforcesly.common.model.userstatus.SubmissionDto
import com.prafullkumar.codeforcesly.visualizer.domain.VisualizerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class VisualizerData(
    val ratingGraphRating: List<Double> = emptyList(),
    val ratingGraphDates: List<String> = emptyList(),
    val latestContestName: String = "",
    val latestRatingDelta: Int? = null,
    val latestRank: Int? = null,
    val tagsFrequency: Map<String, Int> = emptyMap(),
    val verdictFrequency: Map<String, Int> = emptyMap(),
    val indexCounts: Map<String, Int> = emptyMap(),
    val languageFrequency: Map<String, Int> = emptyMap()
)

@HiltViewModel
class VisualizerViewModel @Inject constructor(
    private val repository: VisualizerRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<Resource<Unit>>(Resource.Loading)
    val uiState = _uiState.asStateFlow()
    private var loadJob: Job? = null


    var visualizerData by mutableStateOf(VisualizerData())
    var isRefreshing by mutableStateOf(false)

    init {
        getUserData()
    }

    fun getUserData(forceRefresh: Boolean = false) {
        loadJob?.cancel()
        val hasExistingData = visualizerData != VisualizerData()
        isRefreshing = forceRefresh && hasExistingData
        if (!hasExistingData) {
            _uiState.update {
                Resource.Loading
            }
        }
        loadJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val userData = repository.getUserData(forceRefresh)
                visualizerData = VisualizerDataGenerator.getVisualizerData(
                    userData.submissions,
                    userData.ratings
                )
                _uiState.update {
                    Resource.Success(Unit)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (!hasExistingData) {
                    _uiState.update {
                        Resource.Error("Error: ${e.message}")
                    }
                }
            } finally {
                isRefreshing = false
            }
        }
    }
}

object VisualizerDataGenerator {
    fun getVisualizerData(submissions: List<SubmissionDto>, ratings: List<Rating>): VisualizerData {
        val chronologicalRatings = ratings.sortedBy { it.ratingUpdateTimeSeconds }
        val latestRating = chronologicalRatings.lastOrNull()
        val chartRatings = sampleRatings(chronologicalRatings)
        return VisualizerData(
            ratingGraphRating = getRatingGraphData(chartRatings),
            ratingGraphDates = getRatingGraphDates(chartRatings),
            latestContestName = latestRating?.contestName.orEmpty(),
            latestRatingDelta = latestRating?.let { it.newRating - it.oldRating },
            latestRank = latestRating?.rank,
            tagsFrequency = getTagsFrequency(submissions),
            verdictFrequency = getVerdictFrequency(submissions),
            indexCounts = getSolvedIndexes(submissions),
            languageFrequency = getLanguageFrequency(submissions)
        )
    }

    private fun sampleRatings(ratings: List<Rating>): List<Rating> {
        if (ratings.size <= MAX_RATING_POINTS) return ratings
        val bucketSize = (ratings.size + MAX_RATING_POINTS - 1) / MAX_RATING_POINTS
        return ratings.chunked(bucketSize).flatMap { bucket ->
            val indexedRatings = bucket.withIndex()
            listOf(
                indexedRatings.minBy { it.value.newRating },
                indexedRatings.maxBy { it.value.newRating }
            ).distinctBy { it.index }
                .sortedBy { it.index }
                .map { it.value }
        }
    }

    private fun getRatingGraphData(ratings: List<Rating>): List<Double> {
        return ratings.map { it.newRating.toDouble() }
    }

    private fun getRatingGraphDates(ratings: List<Rating>): List<String> {
        val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        return ratings.map { dateFormat.format(Date(it.ratingUpdateTimeSeconds * 1000L)) }
    }

    private fun getTagsFrequency(userSubmissions: List<SubmissionDto>): Map<String, Int> {
        return userSubmissions.flatMap { it.problem?.tags ?: emptySet() }.groupingBy { it }
            .eachCount()
    }

    private fun getVerdictFrequency(userSubmissions: List<SubmissionDto>): Map<String, Int> {
        return userSubmissions.groupingBy { it.verdict ?: "Unknown" }.eachCount()
    }

    private fun getSolvedIndexes(submissions: List<SubmissionDto>): Map<String, Int> {
        return submissions.asSequence()
            .filter { it.verdict == "OK" }
            .mapNotNull { submission ->
                val problem = submission.problem ?: return@mapNotNull null
                val index = problem.index?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val problemKey = "${problem.contestId ?: "archive"}-$index"
                problemKey to index
            }
            .distinctBy { it.first }
            .groupingBy { it.second }
            .eachCount()
            .toSortedMap()
    }

    private fun getLanguageFrequency(userSubmissions: List<SubmissionDto>): Map<String, Int> {
        return userSubmissions.groupingBy { it.programmingLanguage ?: "Unknown" }.eachCount()
    }

    private const val MAX_RATING_POINTS = 240
}
