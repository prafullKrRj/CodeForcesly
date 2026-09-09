package com.prafullkumar.codeforcesly.profile.submissions

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CancellationException
import com.prafullkumar.codeforcesly.common.model.userstatus.SubmissionDto
import com.prafullkumar.codeforcesly.profile.profile.ProfileApiService

class SubmissionsPagingSource(
    private val api: ProfileApiService,
    private val handle: String
) : PagingSource<Int, SubmissionDto>() {

    override fun getRefreshKey(state: PagingState<Int, SubmissionDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.let { page ->
                page.prevKey?.plus(page.data.size)
                    ?: page.nextKey?.minus(page.data.size)
            }
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SubmissionDto> {
        return try {
            val offset = params.key ?: 1
            val response = api.getUserSubmissions(
                handle = handle,
                from = offset,
                count = params.loadSize
            )
            check(response.status == "OK") {
                "Codeforces response: ${response.status.ifBlank { "UNKNOWN" }}"
            }
            val data = response.result
            LoadResult.Page(
                data = data,
                prevKey = if (offset == 1) null else maxOf(1, offset - params.loadSize),
                nextKey = if (data.size < params.loadSize) null else offset + data.size
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
