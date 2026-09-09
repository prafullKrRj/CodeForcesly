package com.prafullkumar.codeforcesly.friends.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.prafullkumar.codeforcesly.MainScreens
import com.prafullkumar.codeforcesly.friends.data.local.Friend
import com.prafullkumar.codeforcesly.ui.theme.AppSpacing
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    navController: NavController,
    viewModel: FriendsViewModel,
    modifier: Modifier = Modifier
) {
    val friends by viewModel.allFriends.collectAsState()
    val showDialog by viewModel.dialogState.collectAsState()
    val addFriendState by viewModel.addFriendState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    if (friends.isEmpty()) {
        PullToRefreshBox(
            modifier = modifier.fillMaxSize(),
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refreshFriends
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                NoFriendsScreen(
                    onAddFriendsClick = { viewModel.showDialog() },
                    modifier = Modifier.fillMaxSize()
                )
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(AppSpacing.large)
                    )
                }
            }
        }
    } else {
        FriendsContent(
            friends = friends,
            navController = navController,
            onAddFriendClick = { viewModel.showDialog() },
            viewModel = viewModel,
            isRefreshing = isRefreshing
        )
    }

    if (showDialog) {
        AddFriendDialog(
            onDismiss = { viewModel.hideDialog() },
            onConfirm = { handle, name -> viewModel.addFriend(handle, name) },
            isLoading = addFriendState.isLoading,
            error = addFriendState.error,
            onClearError = viewModel::clearAddFriendError
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsContent(
    friends: List<Friend>,
    navController: NavController,
    onAddFriendClick: () -> Unit,
    viewModel: FriendsViewModel,
    isRefreshing: Boolean
) {
    val pullRoRefreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        state = pullRoRefreshState,
        onRefresh = {
            viewModel.refreshFriends()
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("People to watch")
                            Text(
                                "${friends.size} saved profiles",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddFriendClick,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Friend")
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = AppSpacing.screen),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        vertical = AppSpacing.small
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.small)
                ) {
                    items(items = friends, key = { it.handle }, contentType = { "friend" }) { friend ->
                        FriendCard(
                            friend = friend,
                            onDelete = { viewModel.deleteFriend(friend) },
                            onClick = {
                                navController.navigate(MainScreens.FriendDetail(friend.handle))
                            }
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun FriendCard(
    friend: Friend,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 124.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppSpacing.large),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.large),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
            ) {
                if (friend.avatar.isNotBlank()) {
                    AsyncImage(
                        model = friend.avatar,
                        contentDescription = "Avatar for ${friend.handle}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = friend.handle.take(2).uppercase(Locale.getDefault()),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.scrim.copy(alpha = 0.24f)
                                )
                            )
                        )
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.extraSmall)
            ) {
                Text(
                    text = friend.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = friend.handle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = friend.rank.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            },
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = AppSpacing.small, vertical = AppSpacing.extraSmall),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Text(
                        text = friend.rating.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Last active: ${formatTimeAgo(friend.lastActive)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.alpha(0.78f)
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun AddFriendDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    isLoading: Boolean = false,
    error: String? = null,
    onClearError: () -> Unit = {}
) {
    var handle by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Add profile",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = handle,
                    onValueChange = {
                        handle = it
                        onClearError()
                    },
                    label = { Text("Codeforces handle") },
                    isError = error != null,
                    supportingText = error?.let { message -> { Text(message) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        onClearError()
                    },
                    label = { Text("Display name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (handle.isNotBlank() && name.isNotBlank()) {
                                onConfirm(handle, name)
                            }
                        },
                        enabled = handle.isNotBlank() && name.isNotBlank() && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }
}

// Utility function to format time
fun formatTimeAgo(timestamp: Long): String {
    if (timestamp == 0L) return "Unknown"
    val now = System.currentTimeMillis() / 1000
    val diff = now - timestamp
    return when {
        diff < 60 -> "Just now"
        diff < 3600 -> "${diff / 60} minutes ago"
        diff < 86400 -> "${diff / 3600} hours ago"
        else -> "${diff / 86400} days ago"
    }
}
