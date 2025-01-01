package com.prafullkumar.codeforcesly.profile.profile

// ProfileContent.kt
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.prafullkumar.codeforcesly.R
import com.prafullkumar.codeforcesly.common.model.userinfo.UserInfo
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ProfileContent(
    userInfo: UserInfo,
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Submissions Preview Card
            if (showNavigateToSubmissions) {
                SubmissionsPreviewCard(onNavigateToSubmissions)
            }

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
                            userInfo.registrationTimeSeconds.let {
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
        tonalElevation = 3.dp,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = userInfo.titlePhoto,
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentScale = ContentScale.Crop
                )

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
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RankBadge(userInfo.rank, userInfo.rating)
                        Text(
                            text = "Rating: ${userInfo.rating}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
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
    val backgroundColor = when {
        rating >= 2400 -> Color(0xFFFF0000)
        rating >= 2100 -> Color(0xFFFF8C00)
        rating >= 1900 -> Color(0xFFAA00AA)
        rating >= 1600 -> Color(0xFF0000FF)
        rating >= 1400 -> Color(0xFF03A89E)
        else -> Color(0xFF808080)
    }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = backgroundColor.copy(alpha = 0.2f)
    ) {
        Text(
            text = rank ?: "Unrated",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}

@Composable
fun StatsCard(userInfo: UserInfo) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                value = userInfo.rating.toString(),
                label = "Current Rating",
                icon = Icons.Default.Star
            )
            StatItem(
                value = userInfo.maxRating.toString(),
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
