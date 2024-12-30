package com.prafullkumar.codeforcesly.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Data Classes
data class CodeforcesUser(
    val handle: String,
    val rating: Int,
    val maxRating: Int,
    val rank: String,
    val maxRank: String,
    val contribution: Int,
    val friendOfCount: Int,
    val avatar: String,
    val titlePhoto: String
)

data class Submission(
    val problemName: String,
    val verdict: String,
    val language: String,
    val timestamp: Long
)

// API Interface
interface CodeforcesApi {
    @GET("user.info")
    suspend fun getUserInfo(@Query("handles") handle: String): CodeforcesUser

    @GET("user.status")
    suspend fun getUserSubmissions(@Query("handle") handle: String): List<Submission>
}

class ProfileViewModel : ViewModel() {
    private val _userState = MutableStateFlow<CodeforcesUser?>(null)
    val userState: StateFlow<CodeforcesUser?> = _userState

    private val client = Retrofit.Builder()
        .baseUrl("https://codeforces.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CodeforcesApi::class.java)

    private val _submissions = MutableStateFlow<List<Submission>>(emptyList())
    val submissions: StateFlow<List<Submission>> = _submissions

    init {
        fetchUserData("tourist")
    }
    fun fetchUserData(handle: String) {
        viewModelScope.launch {
            try {
                val user = client.getUserInfo(handle)
                _userState.value = user
                val submissions = client.getUserSubmissions(handle)
                _submissions.value = submissions
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val userState by viewModel.userState.collectAsState()
    val submissions by viewModel.submissions.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Profile Header
        ProfileHeader(userState)

        Spacer(modifier = Modifier.height(24.dp))

        // Statistics Cards
        StatisticsSection(userState)

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Submissions
        SubmissionsSection(submissions)

        Spacer(modifier = Modifier.height(24.dp))

        // Rating Graph
        RatingGraph()
    }
}

@Composable
fun ProfileHeader(user: CodeforcesUser?) {
    user?.let {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.avatar)
                        .build(),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                // User Info
                Text(
                    text = user.handle,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = user.rank,
                    style = MaterialTheme.typography.titleMedium,
                    color = getRankColor(user.rank)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RatingInfo("Current", user.rating)
                    RatingInfo("Maximum", user.maxRating)
                }
            }
        }
    }
}

@Composable
fun StatisticsSection(user: CodeforcesUser?) {
    user?.let {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatCard(
                title = "Contribution",
                value = user.contribution.toString(),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            StatCard(
                title = "Friends",
                value = user.friendOfCount.toString(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SubmissionsSection(submissions: List<Submission>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Recent Submissions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            submissions.take(5).forEach { submission ->
                SubmissionItem(submission)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
@Composable
fun SubmissionItem(submission: Submission) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = submission.problemName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Verdict: ${submission.verdict}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (submission.verdict == "OK") Color.Green else Color.Red
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Language: ${submission.language}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Timestamp: ${submission.timestamp}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
@Composable
fun RatingGraph() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // Implement MPAndroidChart or other charting library for rating visualization
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RatingInfo(label: String, rating: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = rating.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = getRatingColor(rating)
        )
    }
}

fun getRankColor(rank: String): Color {
    return when (rank.toLowerCase()) {
        "newbie" -> Color(0xFF808080)
        "pupil" -> Color(0xFF008000)
        "specialist" -> Color(0xFF03A89E)
        "expert" -> Color(0xFF0000FF)
        "candidate master" -> Color(0xFF8B00FF)
        "master" -> Color(0xFFFF8C00)
        "international master" -> Color(0xFFFF8C00)
        "grandmaster" -> Color(0xFFFF0000)
        "international grandmaster" -> Color(0xFFFF0000)
        "legendary grandmaster" -> Color(0xFFFF0000)
        else -> Color.Gray
    }
}

fun getRatingColor(rating: Int): Color {
    return when {
        rating < 1200 -> Color(0xFF808080)
        rating < 1400 -> Color(0xFF008000)
        rating < 1600 -> Color(0xFF03A89E)
        rating < 1900 -> Color(0xFF0000FF)
        rating < 2100 -> Color(0xFF8B00FF)
        rating < 2300 -> Color(0xFFFF8C00)
        rating < 2400 -> Color(0xFFFF8C00)
        rating < 2600 -> Color(0xFFFF0000)
        rating < 3000 -> Color(0xFFFF0000)
        else -> Color(0xFFFF0000)
    }
}