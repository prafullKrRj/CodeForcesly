package com.prafullkumar.codeforcesly.profile.profile5

// ProfileScreen.kt
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prafullkumar.codeforcesly.R
import com.prafullkumar.codeforcesly.common.model.userinfo.UserInfo
import com.prafullkumar.codeforcesly.profile.profile4.DetailRow
import com.prafullkumar.codeforcesly.profile.profile4.InfoCard
import com.prafullkumar.codeforcesly.profile.profile4.ProfileHeader
import com.prafullkumar.codeforcesly.profile.profile4.StatsCard
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen5(
    userInfo: UserInfo,
    onNavigateToSubmissions: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Profile Header
        ProfileHeader(userInfo)

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Submissions Preview Card
            SubmissionsPreviewCard(onViewAll = onNavigateToSubmissions)

            // Details Card
            InfoCard(
                title = "Details",
                content = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DetailRow("Organization", userInfo.organization ?: "-")
                        DetailRow("Location", "${userInfo.city ?: ""}, ${userInfo.country ?: ""}")
                        DetailRow("Contribution", "+${userInfo.contribution}")
                        DetailRow(
                            "Max Rating",
                            "${userInfo.maxRating} (${userInfo.maxRank ?: "-"})"
                        )
                        DetailRow(
                            "Registered",
                            userInfo.registrationTimeSeconds?.let {
                                Instant.ofEpochSecond(it)
                                    .atZone(ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                            } ?: "-"
                        )
                    }
                }
            )

            // Stats Card
            StatsCard(userInfo)
        }
    }
}

@Composable
private fun SubmissionsPreviewCard(onViewAll: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Submissions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onViewAll) {
                    Text("View All")
                    Icon(
                        ImageVector.vectorResource(R.drawable.baseline_chevron_right_24),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Preview of last 3 submissions
            /* repeat(3) {
                 SubmissionItem(
                     problemName = "Problem ${it + 1}",
                     verdict = "Accepted",
                     timestamp = "2h ago",
                     modifier = Modifier.padding(vertical = 4.dp)
                 )
             }*/
        }
    }
}

// SubmissionsScreen.kt
/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmissionsScreen(
    viewModel: SubmissionsViewModel,
    onNavigateBack: () -> Unit
) {
    val submissionsState = viewModel.submissions.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Submissions") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(false),
            onRefresh = { submissionsState.refresh() }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(
                    count = submissionsState.itemCount,
                    key = { it }
                ) { index ->
                    val submission = submissionsState[index]
                    submission?.let {
                        SubmissionItem(
                            problemName = it.problemName,
                            verdict = it.verdict,
                            timestamp = it.getFormattedTime(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

//                when (submissionsState.loadState.append) {
//                    is LoadState.Loading -> {
//                        item { LoadingIndicator() }
//                    }
//                    is LoadState.Error -> {
//                        item { ErrorItem { submissionsState.retry() } }
//                    }
//                    else -> {}
//                }
            }
        }
    }
}

@Composable
fun SubmissionItem(
    problemName: String,
    verdict: String,
    timestamp: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = problemName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            VerdictChip(verdict)
        }
    }
}

@Composable
private fun VerdictChip(verdict: String) {
    val (backgroundColor, textColor) = when (verdict.lowercase()) {
        "accepted" -> Color(0xFF4CAF50) to Color.White
        "wrong answer" -> Color(0xFFF44336) to Color.White
        "time limit exceeded" -> Color(0xFFFF9800) to Color.Black
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = backgroundColor.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = verdict,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
@HiltViewModel
// SubmissionsViewModel.kt
class SubmissionsViewModel @Inject constructor(
    private val repository: SubmissionsRepository
) : ViewModel() {

    val submissions = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 5,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { repository.getSubmissionsPagingSource() }
    ).flow.cachedIn(viewModelScope)
}

// SubmissionsRepository.kt
class SubmissionsRepository @Inject constructor(
    private val api: ProfileApiService
) {
    fun getSubmissionsPagingSource() = SubmissionsPagingSource(api)
}

// SubmissionsPagingSource.kt
class SubmissionsPagingSource(
    private val api: ProfileApiService
) : PagingSource<Int, Submission>() {

    override fun getRefreshKey(state: PagingState<Int, Submission>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Submission> {
        return try {
            val page = params.key ?: 1
            val response = api.getSubmissions(page, params.loadSize)

            LoadResult.Page(
                data = response,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

// Submission.kt
data class Submission(
    val id: Long,
    val problemName: String,
    val verdict: String,
    val submissionTimeSeconds: Long
) {
    fun getFormattedTime(): String {
        val submissionTime = Instant.ofEpochSecond(submissionTimeSeconds)
        val now = Instant.now()
        val diffSeconds = now.epochSecond - submissionTimeSeconds

        return when {
            diffSeconds < 60 -> "Just now"
            diffSeconds < 3600 -> "${diffSeconds / 60}m ago"
            diffSeconds < 86400 -> "${diffSeconds / 3600}h ago"
            else -> submissionTime
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        }
    }
}*/