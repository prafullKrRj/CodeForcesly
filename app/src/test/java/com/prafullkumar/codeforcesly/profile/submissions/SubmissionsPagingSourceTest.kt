package com.prafullkumar.codeforcesly.profile.submissions

import com.prafullkumar.codeforcesly.common.model.userinfo.UserInfoResponse
import com.prafullkumar.codeforcesly.common.model.userstatus.SubmissionDto
import com.prafullkumar.codeforcesly.common.model.userstatus.UserStatus
import com.prafullkumar.codeforcesly.profile.profile.ProfileApiService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SubmissionsPagingSourceTest {
    @Test
    fun loadUsesSubmissionOffsetsWithoutOverlappingPages() = runBlocking {
        val requests = mutableListOf<Pair<Int, Int>>()
        val api = object : ProfileApiService {
            override suspend fun getUserSubmissions(
                handle: String,
                from: Int,
                count: Int
            ): UserStatus {
                requests += from to count
                return UserStatus(
                    result = List(count) { SubmissionDto(id = from + it, contestId = 1) },
                    status = "OK"
                )
            }

            override suspend fun getUserInfo(handle: String): UserInfoResponse =
                error("Not used by paging source")
        }
        val source = SubmissionsPagingSource(api, "tourist")

        val first = source.load(
            androidx.paging.PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 30,
                placeholdersEnabled = false
            )
        )
        assertTrue(first is androidx.paging.PagingSource.LoadResult.Page)
        require(first is androidx.paging.PagingSource.LoadResult.Page)

        val second = source.load(
            androidx.paging.PagingSource.LoadParams.Append(
                key = first.nextKey ?: error("First page must have next key"),
                loadSize = 30,
                placeholdersEnabled = false
            )
        )
        assertTrue(second is androidx.paging.PagingSource.LoadResult.Page)
        assertEquals(listOf(1 to 30, 31 to 30), requests)
    }

    @Test
    fun nonOkResponseRemainsRetryableAsPagingError() = runBlocking {
        val api = object : ProfileApiService {
            override suspend fun getUserSubmissions(
                handle: String,
                from: Int,
                count: Int
            ): UserStatus = UserStatus(emptyList(), "FAILED")

            override suspend fun getUserInfo(handle: String): UserInfoResponse =
                error("Not used by paging source")
        }

        val result = SubmissionsPagingSource(api, "tourist").load(
            androidx.paging.PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 30,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is androidx.paging.PagingSource.LoadResult.Error)
        require(result is androidx.paging.PagingSource.LoadResult.Error)
        assertEquals("Codeforces response: FAILED", result.throwable.message)
    }
}
