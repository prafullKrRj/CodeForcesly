package com.prafullkumar.codeforcesly.profile.profile2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.prafullkumar.codeforcesly.common.model.userinfo.UserInfo

@Composable
fun ProfileScreen2(result: UserInfo) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
    ) {
        // Top Profile Section
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberImagePainter(result.avatar),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = result.handle,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "${result.firstName} ${result.lastName}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFDBEAFE)
                        ) {
                            Text(
                                text = result.rank ?: "",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color(0xFF1E40AF)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Rating: ${result.rating} (max: ${result.maxRating})",
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // Tabs
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 }
            ) {
                Text("Details", modifier = Modifier.padding(16.dp))
            }
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 }
            ) {
                Text("Submissions", modifier = Modifier.padding(16.dp))
            }
        }

        when (selectedTab) {
            0 -> DetailsTab(result)
            1 -> SubmissionsTab()
        }
    }
}

@Composable
private fun DetailsTab(result: UserInfo) {
    Surface(
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Profile Details",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Organization",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = result.organization ?: "",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Location",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${result.city}, ${result.country}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Column {
                    Text(
                        text = "Contribution",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "+${result.contribution}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF16A34A)
                    )
                }
            }

            Button(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("View Rating Graph")
            }
        }
    }
}

@Composable
private fun SubmissionsTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(3) { i ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Problem ${i + 1}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Submitted 2h ago",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFDCFCE7)
                    ) {
                        Text(
                            text = "Accepted",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color(0xFF166534)
                        )
                    }
                }
            }
        }
    }
}