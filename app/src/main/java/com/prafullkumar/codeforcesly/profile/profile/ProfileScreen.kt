package com.prafullkumar.codeforcesly.profile.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.prafullkumar.codeforcesly.common.ErrorScreen
import com.prafullkumar.codeforcesly.common.model.userinfo.UserInfo
import com.prafullkumar.codeforcesly.common.model.userinfo.UserInfoResponse
import com.prafullkumar.codeforcesly.common.model.userstatus.UserStatus
import retrofit2.http.GET
import retrofit2.http.Query


sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Success(val user: UserInfo) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToSubmissions: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val refreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = viewModel.isRefreshing,
        onRefresh = viewModel::refreshState,
        state = refreshState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Profile") },
                    actions = {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    }
                )
            },
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
                        ErrorScreen(
                            message = "Error Loading try refreshing..",
                            onRetry = viewModel::getUserInformation
                        )
                    }

                    is ProfileUiState.Success ->
                        ProfileContent(
                            state.user,
                            onNavigateToSubmissions = onNavigateToSubmissions
                        )
                }
            }
        }
    }
}

interface ProfileApiService {
    @GET("user.info")
    suspend fun getUserInfo(@Query("handles") handle: String): UserInfoResponse

    @GET("user.status")
    suspend fun getUserSubmissions(
        @Query("handle") handle: String,
        @Query("from") from: Int,
        @Query("count") count: Int
    ): UserStatus
}