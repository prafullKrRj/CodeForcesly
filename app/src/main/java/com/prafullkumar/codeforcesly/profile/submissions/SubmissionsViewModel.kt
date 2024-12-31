package com.prafullkumar.codeforcesly.profile.submissions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.prafullkumar.codeforcesly.common.SharedPrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SubmissionsViewModel @Inject constructor(
    private val repository: SubmissionsRepository,
    pref: SharedPrefManager
) : ViewModel() {


    val submissions = Pager(
        config = PagingConfig(
            pageSize = 30,
            prefetchDistance = 5,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { repository.getSubmissionsPagingSource(pref.getHandle() ?: "tourist") }
    ).flow.cachedIn(viewModelScope)

}