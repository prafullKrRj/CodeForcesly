package com.prafullkumar.codeforcesly.profile.submissions

import com.prafullkumar.codeforcesly.profile.profile.ProfileApiService
import javax.inject.Inject

class SubmissionsRepositoryImpl @Inject constructor(
    private val api: ProfileApiService
) : SubmissionsRepository {
    override fun getSubmissionsPagingSource(handle: String) = SubmissionsPagingSource(api, handle)
}