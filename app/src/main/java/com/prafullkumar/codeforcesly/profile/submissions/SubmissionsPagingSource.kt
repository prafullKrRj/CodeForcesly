package com.prafullkumar.codeforcesly.profile.submissions

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.prafullkumar.codeforcesly.common.model.userstatus.SubmissionDto
import com.prafullkumar.codeforcesly.profile.profile.ProfileApiService

class SubmissionsPagingSource(
    private val api: ProfileApiService,
    private val handle: String
) : PagingSource<Int, SubmissionDto>() {

    override fun getRefreshKey(state: PagingState<Int, SubmissionDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SubmissionDto> {
        return try {
            val page = params.key ?: 1
            Log.d(
                "SubmissionsPagingSource",
                "Requesting page: $page, loadSize: ${params.loadSize}, handle: $handle"
            )
            val response = api.getUserSubmissions(
                handle = handle,
                from = page,
                count = params.loadSize
            )
            Log.d("SubmissionsPagingSource", "Response: ${response.result}")
            LoadResult.Page(
                data = response.result,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.result.isEmpty()) null else page + (params.loadSize / 10)
            )
        } catch (e: Exception) {
            Log.e("SubmissionsPagingSource", "Error: ${e.message}")
            LoadResult.Error(e)
        }
    }
}