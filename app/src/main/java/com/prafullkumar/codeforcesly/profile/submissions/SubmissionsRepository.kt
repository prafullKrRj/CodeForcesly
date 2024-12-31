package com.prafullkumar.codeforcesly.profile.submissions

import androidx.paging.PagingSource
import com.prafullkumar.codeforcesly.common.model.userstatus.SubmissionDto

interface SubmissionsRepository {
    fun getSubmissionsPagingSource(handle: String): PagingSource<Int, SubmissionDto>
}