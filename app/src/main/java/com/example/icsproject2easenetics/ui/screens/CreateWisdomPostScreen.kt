package com.example.icsproject2easenetics.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.icsproject2easenetics.data.models.WisdomCategory
import com.example.icsproject2easenetics.utils.AccessibilityManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWisdomPostScreen(
    onBack: () -> Unit,
    onPostCreated: () -> Unit,
    currentUserId: String,
    currentUserName: String,
    currentUserEmail: String
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(WisdomCategory.ADVICE) }

    val viewModel: com.example.icsproject2easenetics.ui.viewmodels.WisdomSharingViewModel = viewModel()
    val isLoading by viewModel.isLoading.collectAsState()
    val postSuccess by viewModel.postSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Navigate back when post is successfully created
    LaunchedEffect(postSuccess) {
        if (postSuccess) {
            println("✅ CreateWisdomPostScreen: Post created successfully, navigating back")
            onPostCreated()
        }
    }

    // Log when screen opens for debugging
    LaunchedEffect(Unit) {
        println("🎯 CreateWisdomPostScreen: Screen opened for user: $currentUserName ($currentUserEmail)")
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Share Your Wisdom",
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
                    IconButton(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                createPost(viewModel, title, content, selectedCategory, currentUserId, currentUserName, currentUserEmail)
                            } else {
                                println("❌ CreateWisdomPostScreen: Validation failed - title or content empty")
                            }
                        },
                        enabled = !isLoading && title.isNotBlank() && content.isNotBlank()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Default.Check, "Create Post")
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error message
            if (!errorMessage.isNullOrEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Error Creating Post",
                            style = AccessibilityManager.getScaledTitleMedium(),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = errorMessage!!,
                            modifier = Modifier.padding(top = 8.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Success message (shown briefly before navigation)
            if (postSuccess) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "✅ Post Created Successfully!",
                            style = AccessibilityManager.getScaledTitleMedium(),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Your wisdom is now shared with the community",
                            modifier = Modifier.padding(top = 8.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Category selection
            Text(
                text = "What type of wisdom are you sharing?",
                style = AccessibilityManager.getScaledTitleMedium(),
                fontWeight = FontWeight.Bold
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WisdomCategory.values().forEach { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedCategory = category
                                println("📝 Category selected: $category")
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category }
                        )
                        Text(
                            text = when (category) {
                                WisdomCategory.ADVICE -> "💡 Advice & Tips"
                                WisdomCategory.EXPERIENCE -> "📖 Personal Experience"
                                WisdomCategory.HISTORICAL_EVENT -> "🕰️ Historical Event"
                                WisdomCategory.ENCOURAGEMENT -> "🌟 Words of Encouragement"
                                WisdomCategory.LIFE_LESSON -> "🎓 Life Lesson"
                            },
                            style = AccessibilityManager.getScaledBodyMedium(),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // Title input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                placeholder = { Text("e.g., My experience with smartphones") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = title.isBlank() && content.isNotBlank()
            )

            // Content input
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Your wisdom") },
                placeholder = { Text("Share your experience, advice, or story...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                maxLines = 10,
                isError = content.isBlank() && title.isNotBlank()
            )

            // Validation hint
            if (title.isBlank() || content.isBlank()) {
                Text(
                    text = "✏️ Please fill in both title and content to share your wisdom",
                    style = AccessibilityManager.getScaledBodySmall(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // "Post Your Hekima" Button
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        createPost(viewModel, title, content, selectedCategory, currentUserId, currentUserName, currentUserEmail)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading && title.isNotBlank() && content.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = "Hekima",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "POST YOUR HEKIMA",
                            style = AccessibilityManager.getScaledTitleMedium(),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Tips card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💡 Sharing Tips",
                        style = AccessibilityManager.getScaledTitleMedium(),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• Be clear and specific\n• Share from personal experience\n• Keep it positive and encouraging\n• Your wisdom can inspire others!",
                        style = AccessibilityManager.getScaledBodyMedium(),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Debug info (only show in development)
            if (false) { // Set to true for debugging
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🔍 Debug Info",
                            style = AccessibilityManager.getScaledBodySmall(),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "User: $currentUserName\nID: $currentUserId\nEmail: $currentUserEmail",
                            style = AccessibilityManager.getScaledBodySmall()
                        )
                    }
                }
            }
        }
    }
}

// Helper function to handle post creation
private fun createPost(
    viewModel: com.example.icsproject2easenetics.ui.viewmodels.WisdomSharingViewModel,
    title: String,
    content: String,
    selectedCategory: WisdomCategory,
    currentUserId: String,
    currentUserName: String,
    currentUserEmail: String
) {
    println("🔄 CreateWisdomPostScreen: Creating post...")
    println("   Title: '$title'")
    println("   Content: '$content'")
    println("   Category: $selectedCategory")
    println("   User: $currentUserName ($currentUserId)")

    viewModel.createWisdomPost(
        userId = currentUserId,
        userName = currentUserName,
        userEmail = currentUserEmail,
        title = title,
        content = content,
        category = selectedCategory
    )
}