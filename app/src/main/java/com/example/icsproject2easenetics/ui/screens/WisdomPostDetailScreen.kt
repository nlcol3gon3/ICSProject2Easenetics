package com.example.icsproject2easenetics.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.icsproject2easenetics.ui.components.WisdomPostCard
import com.example.icsproject2easenetics.utils.AccessibilityManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WisdomPostDetailScreen(
    postId: String,
    onBack: () -> Unit,
    currentUserId: String,
    currentUserName: String
) {
    var commentText by remember { mutableStateOf("") }

    val viewModel: com.example.icsproject2easenetics.ui.viewmodels.WisdomSharingViewModel = viewModel()
    val posts by viewModel.wisdomPosts.collectAsState()
    val comments by viewModel.selectedPostComments.collectAsState()

    // Start real-time comments listener when screen opens
    LaunchedEffect(postId) {
        println("🔄 WisdomPostDetailScreen: Starting real-time comments listener for post $postId")
        viewModel.startCommentsListener(postId)
    }

    // Stop real-time comments listener when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            println("🛑 WisdomPostDetailScreen: Stopping real-time comments listener")
            viewModel.stopCommentsListener()
        }
    }

    val currentPost = posts.find { it.postId == postId }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Wisdom Post",
                        style = AccessibilityManager.getScaledTitleLarge(),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // Cleanup is handled by DisposableEffect
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        if (currentPost == null) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Post not found",
                        style = AccessibilityManager.getScaledTitleMedium()
                    )
                    Text(
                        text = "The post might have been deleted or there's a connection issue.",
                        style = AccessibilityManager.getScaledBodyMedium(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Button(onClick = onBack) {
                        Text("Go Back")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                // Post content
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        WisdomPostCard(
                            post = currentPost,
                            currentUserId = currentUserId,
                            onLikeClick = { postId ->
                                viewModel.likePost(postId, currentUserId)
                                // No need to refresh - real-time listener will update automatically
                            },
                            onCommentClick = { /* Already in comments */ },
                            onDeleteClick = { postId ->
                                viewModel.deletePost(postId, currentUserId)
                                onBack()
                            }
                        )
                    }

                    // Comments section
                    item {
                        Text(
                            text = "Comments (${comments.size})",
                            style = AccessibilityManager.getScaledTitleMedium(),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }

                    if (comments.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "💬 No comments yet",
                                        style = AccessibilityManager.getScaledBodyMedium(),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Be the first to share your thoughts!",
                                        style = AccessibilityManager.getScaledBodySmall(),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(comments) { comment ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = comment.userName,
                                            style = AccessibilityManager.getScaledBodyMedium(),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = comment.getDate().toLocaleString(),
                                            style = AccessibilityManager.getScaledBodySmall(),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = comment.content,
                                        style = AccessibilityManager.getScaledBodyMedium()
                                    )
                                }
                            }
                        }
                    }
                }

                // Comment input
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = commentText,
                                onValueChange = { commentText = it },
                                placeholder = { Text("Add a comment...") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (commentText.isNotBlank()) {
                                        println("🔄 WisdomPostDetailScreen: Adding comment to post $postId")
                                        viewModel.addComment(
                                            postId = postId,
                                            userId = currentUserId,
                                            userName = currentUserName,
                                            content = commentText
                                        )
                                        commentText = "" // Clear input immediately
                                        // No need to manually refresh - real-time listener will update automatically
                                    }
                                },
                                enabled = commentText.isNotBlank()
                            ) {
                                Icon(Icons.Default.Send, "Send Comment")
                            }
                        }
                        // Hint text
                        if (commentText.isBlank()) {
                            Text(
                                text = "💡 Your comment will appear instantly for everyone",
                                style = AccessibilityManager.getScaledBodySmall(),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}