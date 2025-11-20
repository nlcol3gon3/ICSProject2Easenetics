package com.example.icsproject2easenetics.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lightbulb
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
fun WisdomSharingScreen(
    onBack: () -> Unit,
    onCreatePost: () -> Unit,
    onPostClick: (String) -> Unit,
    currentUserId: String,
    currentUserName: String = "Friend"
) {
    val viewModel: com.example.icsproject2easenetics.ui.viewmodels.WisdomSharingViewModel = viewModel()
    val posts by viewModel.wisdomPosts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // DEBUG: Log posts count and ensure listener is active
    LaunchedEffect(posts) {
        println("🔄 WisdomSharingScreen: Currently showing ${posts.size} posts")
        if (posts.isEmpty()) {
            println("⚠️ WisdomSharingScreen: No posts showing - checking if listener is active")
        } else {
            posts.forEachIndexed { index, post ->
                println("📄 Post ${index + 1}: '${post.title}' by ${post.userName}")
            }
        }
    }

    // Ensure real-time listener is active when screen is focused
    LaunchedEffect(Unit) {
        println("🎯 WisdomSharingScreen: Screen launched - ensuring real-time listener is active")
        viewModel.refreshPosts() // Force restart listener if needed
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "HEKIMA SHARING",
                        style = AccessibilityManager.getScaledTitleLarge(),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onCreatePost) {
                        Icon(Icons.Default.Add, "Create Post")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreatePost,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Share Wisdom")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Welcome message
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = "Wisdom",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Karibu $currentUserName!",
                        style = AccessibilityManager.getScaledTitleLarge(),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Share your wisdom, experiences, and advice with the community. Your knowledge is valuable!",
                        style = AccessibilityManager.getScaledBodyMedium(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    // DEBUG: Show posts count
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📊 Showing ${posts.size} posts",
                        style = AccessibilityManager.getScaledBodySmall(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Posts list
            if (isLoading && posts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading wisdom posts...")
                    }
                }
            } else if (posts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = "No posts",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No wisdom shared yet",
                            style = AccessibilityManager.getScaledTitleMedium()
                        )
                        Text(
                            text = "Be the first to share your wisdom with the community!",
                            style = AccessibilityManager.getScaledBodyMedium()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                println("🔄 User manually refreshing posts")
                                viewModel.refreshPosts()
                            }
                        ) {
                            Text("Refresh Posts")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onCreatePost) {
                            Text("Share Your First Post")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(posts) { post ->
                        WisdomPostCard(
                            post = post,
                            currentUserId = currentUserId,
                            onLikeClick = { postId ->
                                viewModel.likePost(postId, currentUserId)
                            },
                            onCommentClick = { postId ->
                                onPostClick(postId)
                            },
                            onDeleteClick = { postId ->
                                viewModel.deletePost(postId, currentUserId)
                            }
                        )
                    }
                }
            }
        }
    }
}