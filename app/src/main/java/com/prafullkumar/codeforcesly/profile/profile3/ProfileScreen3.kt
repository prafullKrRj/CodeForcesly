package com.prafullkumar.codeforcesly.profile.profile3

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.prafullkumar.codeforcesly.common.model.userinfo.UserInfo

@Composable
fun ProfileScreen3(userInfo: UserInfo, onNavigate: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF42A5F5), Color(0xFF0D47A1))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Avatar with decorative background
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.White.copy(alpha = 0.2f), shape = CircleShape)
                    .padding(8.dp)
            ) {
                Image(
                    painter = rememberImagePainter(data = userInfo.avatar),
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Info
            Text(
                text = "${userInfo.firstName ?: ""} ${userInfo.lastName ?: ""}",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = "@${userInfo.handle}",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f))
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Stats section with cards
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                UserStatCard(
                    label = "Rank",
                    value = userInfo.rank ?: "-",
                    color = Color(0xFF29B6F6)
                )
                UserStatCard(
                    label = "Rating",
                    value = userInfo.rating.toString(),
                    color = Color(0xFFAB47BC)
                )
                UserStatCard(
                    label = "Max Rank",
                    value = userInfo.maxRank ?: "-",
                    color = Color(0xFFFF7043)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Organization and Location Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                userInfo.organization?.let {
                    Text(
                        text = "Organization: $it",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                    )
                }
                userInfo.city?.let {
                    Text(
                        text = "Location: ${userInfo.city}, ${userInfo.country ?: "Unknown"}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Navigation buttons
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onNavigate("submissions") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6))
                ) {
                    Text(text = "Submissions", color = Color.White)
                }
                Button(
                    onClick = { onNavigate("visualizers") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9575CD))
                ) {
                    Text(text = "Visualizers", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun UserStatCard(label: String, value: String, color: Color) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.6f)),
        modifier = Modifier.size(100.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 20.sp
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.8f)
                )
            )
        }
    }
}