package com.prafullkumar.codeforcesly.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.prafullkumar.codeforcesly.common.model.userinfo.UserInfo
import com.prafullkumar.codeforcesly.common.model.userstatus.UserStatus
import com.prafullkumar.codeforcesly.profile.profile4.ProfileScreen4
import com.prafullkumar.codeforcesly.profile.profile5.ProfileScreen5
import retrofit2.http.GET
import retrofit2.http.Query


sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Success(val user: UserInfo) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

// ProfileScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToSubmissions: () -> Unit,
    onNavigateToGraphs: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is ProfileUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ProfileUiState.Error -> {
                    Text("Error: ${state.message}")
                }

                is ProfileUiState.Success ->
                    ProfileScreen5 (userInfo = state.user, onNavigateToSubmissions = {})
            }
        }
    }
}

interface ProfileApiService {
    @GET("user.info")
    suspend fun getUserInfo(@Query("handles") handle: String): UserInfo

    @GET("user.status")
    abstract fun getSubmissions(page: Int, loadSize: Int): UserStatus
}