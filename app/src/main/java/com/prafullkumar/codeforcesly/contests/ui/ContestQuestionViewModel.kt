package com.prafullkumar.codeforcesly.contests.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.codeforcesly.common.Resource
import com.prafullkumar.codeforcesly.contests.data.ContestsApiService
import com.prafullkumar.codeforcesly.contests.domain.models.contestDetails.Contest
import com.prafullkumar.codeforcesly.problem.domain.model.Problem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ContestQuestionViewModel @Inject constructor(
    private val apiService: ContestsApiService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<List<Problem>>>(Resource.Loading)
    val state = _state.asStateFlow()

    var contestDetails by mutableStateOf<Contest>(Contest())
    var contestId by mutableStateOf(savedStateHandle.get<Int>("contestId"))

    init {
        load()
    }

    fun load() {
        _state.value = Resource.Loading
        val contestId = savedStateHandle.get<Int>("contestId")
        if (contestId == null || contestId <= 0) {
            _state.value = Resource.Error("Contest could not be identified")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getParticularContestProblems(getUrl(contestId))
                if (response.status.lowercase() == "ok") {
                    _state.update { Resource.Success(normalizeContestProblems(response.result.problems)) }
                    contestDetails = response.result.contest
                } else {
                    _state.update { Resource.Error("Error Loading") }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { Resource.Error(e.message ?: "Error") }
            }
        }
    }

    private fun getUrl(contestId: Int) =
        "https://codeforces.com/api/contest.standings?contestId=$contestId"
}

internal fun normalizeContestProblems(problems: List<Problem>): List<Problem> =
    problems.map { problem -> problem.copy(tags = problem.tags.orEmpty()) }
