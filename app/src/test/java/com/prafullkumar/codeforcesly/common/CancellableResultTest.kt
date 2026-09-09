package com.prafullkumar.codeforcesly.common

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test

class CancellableResultTest {
    @Test
    fun cancellationIsNotConvertedIntoFailure() = runBlocking {
        val cancellation = CancellationException("navigation away")

        try {
            runCatchingCancellable<Unit> { throw cancellation }
            fail("Cancellation must propagate")
        } catch (actual: CancellationException) {
            assertSame(cancellation, actual)
        }
    }
}
