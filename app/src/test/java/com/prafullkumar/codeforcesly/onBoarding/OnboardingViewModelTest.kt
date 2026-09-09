package com.prafullkumar.codeforcesly.onBoarding

import com.prafullkumar.codeforcesly.onBoarding.data.local.UserEntity
import com.prafullkumar.codeforcesly.onBoarding.domain.OnBoardingRepo
import com.prafullkumar.codeforcesly.onBoarding.ui.OnboardingState
import com.prafullkumar.codeforcesly.onBoarding.ui.OnboardingViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

class OnboardingViewModelTest {
    private val repository = object : OnBoardingRepo {
        override suspend fun fetchAndStoreUser(handle: String): Result<UserEntity> =
            error("Not used for validation tests")
    }

    @Test
    fun rejectsHandlesContainingSpacesBeforeNetworkCall() {
        val viewModel = OnboardingViewModel(repository)

        viewModel.validateAndStoreHandle("bad profile")

        assertEquals(
            OnboardingState.Error("Handle cannot contain spaces"),
            viewModel.uiState.value
        )
    }

    @Test
    fun rejectsHandlesLongerThanCodeforcesLimit() {
        val viewModel = OnboardingViewModel(repository)

        viewModel.validateAndStoreHandle("a".repeat(25))

        assertEquals(
            OnboardingState.Error("Codeforces handles are limited to 24 characters"),
            viewModel.uiState.value
        )
    }
}
