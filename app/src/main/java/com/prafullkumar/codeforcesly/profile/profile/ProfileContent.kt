package com.prafullkumar.codeforcesly.profile.profile

// ProfileContent.kt
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.prafullkumar.codeforcesly.R
import com.prafullkumar.codeforcesly.common.model.userinfo.UserInfo
import com.prafullkumar.codeforcesly.common.model.userstatus.SubmissionDto
import com.prafullkumar.codeforcesly.ui.theme.AppSpacing
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ProfileContent(
    userInfo: UserInfo,
    recentSubmissions: List<SubmissionDto> = emptyList(),
    showNavigateToSubmissions: Boolean = true,
    onNavigateToSubmissions: () -> Unit = {}
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
            .padding(AppSpacing.screen)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
        ) {
            // Submissions Preview Card
            if (showNavigateToSubmissions) {
                SubmissionsPreviewCard(recentSubmissions, onNavigateToSubmissions)
            }

            // Details Card
            InfoCard(
                title = "Details",
                content = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DetailRow("Organization", userInfo.organization ?: "-")
                        DetailRow(
                            "Location",
                            listOfNotNull(userInfo.city, userInfo.country)
                                .joinToString(", ")
                                .ifEmpty { "-" }
                        )
                        DetailRow("Contribution", "+${userInfo.contribution}")
                        DetailRow(
                            "Max Rating",
                            "${userInfo.maxRating} (${userInfo.maxRank ?: "-"})"
                        )
                        DetailRow(
                            "Registered",
                            userInfo.registrationTimeSeconds.takeIf { it > 0 }?.let {
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
fun ProfileHeader(userInfo: UserInfo) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.extraLarge)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.large),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val profileImage = userInfo.avatar.ifBlank { userInfo.titlePhoto }
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = userInfo.handle.take(2).uppercase(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (profileImage.isNotBlank()) {
                        AsyncImage(
                            model = profileImage,
                            contentDescription = "Profile photo for ${userInfo.handle}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Column {
                    Text(
                        text = userInfo.handle,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${userInfo.firstName ?: ""} ${userInfo.lastName ?: ""}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.small),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RankBadge(userInfo.rank, userInfo.rating)
                        Text(
                            text = if (userInfo.rating > 0) {
                                "Rating: ${userInfo.rating}"
                            } else {
                                "Unrated"
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubmissionsPreviewCard(
    submissions: List<SubmissionDto>,
    onViewAll: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.large)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = AppSpacing.small),
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

            if (submissions.isEmpty()) {
                Text(
                    text = "No recent submissions available yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.small)) {
                    submissions.forEach { submission ->
                        RecentSubmissionRow(submission)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentSubmissionRow(submission: SubmissionDto) {
    val verdict = submission.verdict?.replace('_', ' ') ?: "UNKNOWN"
    val (containerColor, contentColor) = when (submission.verdict) {
        "OK" -> MaterialTheme.colorScheme.primaryContainer to
            MaterialTheme.colorScheme.onPrimaryContainer
        "WRONG_ANSWER" -> MaterialTheme.colorScheme.errorContainer to
            MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant to
            MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = containerColor,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = submission.problem?.index ?: "?",
                modifier = Modifier.padding(horizontal = AppSpacing.small, vertical = AppSpacing.extraSmall),
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                fontWeight = FontWeight.Bold
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = submission.problem?.name ?: "Unknown problem",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Contest ${submission.contestId ?: "archive"}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = verdict,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            maxLines = 1
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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

@Composable
private fun RankBadge(rank: String?, rating: Int) {
    val colors = MaterialTheme.colorScheme
    val backgroundColor = when {
        rating >= 2100 -> colors.error
        rating >= 1900 -> colors.primary
        rating >= 1600 -> colors.tertiary
        rating >= 1400 -> colors.secondary
        else -> colors.onSurfaceVariant
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = backgroundColor.copy(alpha = 0.2f)
    ) {
        Text(
            text = rank ?: "Unrated",
            modifier = Modifier.padding(horizontal = AppSpacing.small, vertical = AppSpacing.extraSmall),
            color = backgroundColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun InfoCard(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.large)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = AppSpacing.large)
            )
            content()
        }
    }
}

@Composable
fun StatsCard(userInfo: UserInfo) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.large),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                value = userInfo.rating.takeIf { it > 0 }?.toString() ?: "—",
                label = "Current Rating",
                icon = Icons.Default.Star
            )
            StatItem(
                value = userInfo.maxRating.takeIf { it > 0 }?.toString() ?: "—",
                label = "Max Rating",
                icon = ImageVector.vectorResource(R.drawable.baseline_emoji_events_24)
            )
            StatItem(
                value = userInfo.friendOfCount.toString(),
                label = "Friends",
                icon = ImageVector.vectorResource(R.drawable.baseline_group_24)
            )
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
