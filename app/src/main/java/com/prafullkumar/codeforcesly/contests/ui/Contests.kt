package com.prafullkumar.codeforcesly.contests.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prafullkumar.codeforcesly.MainScreens
import com.prafullkumar.codeforcesly.common.ErrorScreen
import com.prafullkumar.codeforcesly.contests.domain.models.contest.Contest
import com.prafullkumar.codeforcesly.ui.theme.AppSpacing
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContestsScreen(
    viewModel: ContestsViewModel,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val contests by viewModel.contests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { ContestTab.entries.size }
    )
    val selectedTab = ContestTab.entries[pagerState.currentPage]
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var nowSeconds by remember { mutableStateOf(Instant.now().epochSecond) }
    LaunchedEffect(Unit) {
        while (isActive) {
            nowSeconds = Instant.now().epochSecond
            delay(1_000)
        }
    }
    val contestsByTab = remember(contests, searchQuery) {
        mapOf(
            ContestTab.UPCOMING to filterContests(
                contests.asSequence()
                    .filter { it.phase.equals("BEFORE", ignoreCase = true) }
                    .sortedBy { it.startTimeSeconds }
                    .toList(),
                searchQuery
            ),
            ContestTab.ONGOING to filterContests(
                contests.asSequence()
                    .filter { it.phase.equals("CODING", ignoreCase = true) }
                    .sortedBy { it.startTimeSeconds }
                    .toList(),
                searchQuery
            ),
            ContestTab.PAST to filterContests(
                contests.asSequence()
                    .filter { it.phase.equals("FINISHED", ignoreCase = true) }
                    .sortedByDescending { it.startTimeSeconds }
                    .toList(),
                searchQuery
            ),
        )
    }
    val refreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = viewModel::refresh,
        state = refreshState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Contest desk")
                            Text(
                                "${contests.size} public contests",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                ContestTabs(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        scope.launch { pagerState.animateScrollToPage(tab.ordinal) }
                    }
                )
                ContestSearchField(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.screen, vertical = AppSpacing.small)
                )
                Box(modifier = Modifier.fillMaxSize()) {
                    if (isLoading && contests.isEmpty()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else if (error != null && contests.isEmpty()) {
                        ErrorScreen(
                            message = error ?: "Could not load contests",
                            onRetry = viewModel::refresh
                        )
                    } else {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            val filteredContests = contestsByTab[ContestTab.entries[page]].orEmpty()
                            ContestList(filteredContests, navController, nowSeconds)
                        }
                        if (isLoading) {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.TopCenter)
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class ContestTab {
    UPCOMING, ONGOING, PAST
}

internal fun filterContests(contests: List<Contest>, query: String): List<Contest> {
    val normalizedQuery = query.trim()
    if (normalizedQuery.isEmpty()) return contests
    return contests.filter { contest ->
        contest.name.contains(normalizedQuery, ignoreCase = true) ||
            contest.type.contains(normalizedQuery, ignoreCase = true)
    }
}

@Composable
private fun ContestSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        singleLine = true,
        placeholder = { Text("Search contests") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = if (query.isNotBlank()) {
            {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear contest search")
                }
            }
        } else null
    )
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
                        text = tab.label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

private val ContestTab.label: String
    get() = name.lowercase().replaceFirstChar { it.titlecase() }

@Composable
fun ContestList(
    contests: List<Contest>,
    navController: NavController,
    nowSeconds: Long
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppSpacing.screen),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
    ) {
        items(items = contests, key = { it.id }, contentType = { "contest" }) { contest ->
            ContestCard(
                contest = contest,
                navController = navController,
                nowSeconds = nowSeconds
            )
        }
        if (contests.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppSpacing.extraLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Nothing here yet", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Try another contest tab or pull to refresh.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ContestCard(
    contest: Contest,
    navController: NavController,
    nowSeconds: Long
) {
    val isFinished = contest.phase.equals("FINISHED", ignoreCase = true)
    val cardContent: @Composable ColumnScope.() -> Unit = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.large)
        ) {
            Text(
                text = contest.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(AppSpacing.small))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ContestChip(text = contest.type)
                Text(
                    text = contest.participants?.let { "$it participants" }
                        ?: contest.phase.displayLabel(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.large))
            ContestTimeInfo(contest, nowSeconds)
        }
    }
    if (isFinished) {
        Card(
            onClick = { navController.navigate(MainScreens.ContestDetailScreen(contest.id)) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            content = cardContent
        )
    } else {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            content = cardContent
        )
    }
}

@Composable
fun ContestChip(
    text: String
) {
    val colors = MaterialTheme.colorScheme
    val (containerColor, contentColor) = when (text.lowercase(Locale.ROOT)) {
        "cf" -> colors.primaryContainer to colors.onPrimaryContainer
        "ioi" -> colors.secondaryContainer to colors.onSecondaryContainer
        else -> colors.tertiaryContainer to colors.onTertiaryContainer
    }
    Surface(
        shape = MaterialTheme.shapes.small,
        color = containerColor,
        contentColor = contentColor,
        modifier = Modifier.padding(end = AppSpacing.small)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = AppSpacing.small, vertical = AppSpacing.extraSmall)
        )
    }
}

@Composable
fun ContestTimeInfo(contest: Contest, now: Long) {
    val startTime = Instant.ofEpochSecond(contest.startTimeSeconds)
    val duration = Duration.ofSeconds(contest.durationSeconds.toLong())

    Column {
        when (contest.phase.uppercase(Locale.ROOT)) {
            "BEFORE" -> {
                val timeUntilStart = Duration.ofSeconds(contest.startTimeSeconds - now)
                TimeInfoRow(
                    label = "Starts in",
                    value = formatDuration(timeUntilStart)
                )
            }

            "CODING" -> {
                val timeLeft = Duration.ofSeconds(
                    contest.startTimeSeconds + contest.durationSeconds - now
                )
                TimeInfoRow(
                    label = "Time left",
                    value = formatDuration(timeLeft)
                )
            }

            else -> {
                TimeInfoRow(
                    label = "Duration",
                    value = formatDuration(duration)
                )
            }
        }

        TimeInfoRow(
            label = "Start time",
            value = formatDateTime(startTime)
        )
    }
}

private fun String.displayLabel(): String = when (uppercase(Locale.ROOT)) {
    "BEFORE" -> "Upcoming"
    "CODING" -> "Live now"
    "FINISHED" -> "Finished"
    else -> replace('_', ' ').lowercase(Locale.ROOT).replaceFirstChar { it.titlecase(Locale.ROOT) }
}

fun formatDateTime(instant: Instant): String {
    val formatter = DateTimeFormatter
        .ofPattern("MMM dd, yyyy HH:mm")
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}

fun formatDuration(duration: Duration): String {
    val seconds = duration.seconds.coerceAtLeast(0)
    val days = seconds / (24 * 3600)
    val hours = (seconds % (24 * 3600)) / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60

    return buildString {
        if (days > 0) append("${days}d ")
        if (hours > 0 || days > 0) append("${hours}h ")
        if (minutes > 0 || hours > 0 || days > 0) append("${minutes}m ")
        append("${remainingSeconds}s")
    }.trim()
}

@Composable
fun TimeInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.extraSmall),
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
