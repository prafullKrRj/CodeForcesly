package com.prafullkumar.codeforcesly.common

import kotlinx.coroutines.CancellationException

/**
 * Like runCatching, but keeps structured-concurrency cancellation intact.
 */
suspend fun <T> runCatchingCancellable(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Result.failure(e)
}
