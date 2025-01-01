package com.prafullkumar.codeforcesly.contests.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prafullkumar.codeforcesly.contests.domain.Contest
import java.time.Duration
import java.time.Instant
import java.util.Locale

@Composable
fun ContestsScreen(
    viewModel: ContestsViewModel,
    modifier: Modifier = Modifier
) {
    val contests by viewModel.contests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedTab by remember { mutableStateOf(ContestTab.UPCOMING) }

    Column(modifier = modifier.fillMaxSize()) {
        // Contest Tabs
        ContestTabs(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    ContestList(
                        contests = contests.filter {
                            when (selectedTab) {
                                ContestTab.UPCOMING -> it.phase == "BEFORE"
                                ContestTab.ONGOING -> it.phase == "CODING"
                                ContestTab.PAST -> it.phase == "FINISHED"
                            }
                        }
                    )
                }
            }
        }
    }
}

enum class ContestTab {
    UPCOMING, ONGOING, PAST
}

@Composable
fun ContestTabs(
    selectedTab: ContestTab,
    onTabSelected: (ContestTab) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        modifier = Modifier.fillMaxWidth(),
    ) {
        ContestTab.entries.forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = tab.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

@Composable
fun ContestList(contests: List<Contest>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(contests) { contest ->
            ContestCard(contest = contest)
        }
    }
}

@Composable
fun ContestCard(contest: Contest) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Contest Name
            Text(
                text = contest.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Contest Type and Participants
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ContestChip(
                    text = contest.type,
                    color = getContestTypeColor(contest.type)
                )
                Text(
                    text = "${contest.participants} participants",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time Information
            ContestTimeInfo(contest)
        }
    }
}

@Composable
fun ContestChip(
    text: String,
    color: Color
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.1f),
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun ContestTimeInfo(contest: Contest) {
    val now = Instant.now().epochSecond
    val startTime = Instant.ofEpochSecond(contest.startTimeSeconds)
    val duration = Duration.ofSeconds(contest.durationSeconds.toLong())

    Column {
        when (contest.phase) {
            "BEFORE" -> {
                val timeUntilStart = Duration.ofSeconds(contest.startTimeSeconds - now)
                TimeInfoRow(
                    label = "Starts in",
                    value = timeUntilStart.toString()// formatDuration(timeUntilStart)
                )
            }

            "CODING" -> {
                val timeLeft = Duration.ofSeconds(
                    contest.startTimeSeconds + contest.durationSeconds - now
                )
                TimeInfoRow(
                    label = "Time left",
                    value = timeLeft.toString() // formatDuration(timeLeft)
                )
            }

            else -> {
                TimeInfoRow(
                    label = "Duration",
                    value = duration.toString() // formatDuration(duration)
                )
            }
        }

        TimeInfoRow(
            label = "Start time",
            value = startTime.toString() // formatDateTime(startTime)
        )
    }
}

@Composable
fun TimeInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

fun getContestTypeColor(type: String): Color {
    return when (type.lowercase(Locale.ROOT)) {
        "cf" -> Color(0xFF1976D2)      // Blue
        "ioi" -> Color(0xFF388E3C)     // Green
        "icpc" -> Color(0xFFF57C00)    // Orange
        else -> Color(0xFF7B1FA2)      // Purple
    }
}

//fun formatDuration(duration: Duration): String {
//    val days = duration.toDays()
//    val hours = duration.toHoursPart()
//    val minutes = duration.toMinutesPart()
//
//    return when {
//        days > 0 -> "${days}d ${hours}h"
//        hours > 0 -> "${hours}h ${minutes}m"
//        else -> "${minutes}m"
//    }
//}

fun formatDateTime(instant: Instant): String {
    // Implement date time formatting
    return instant.toString()
}