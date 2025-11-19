package com.example.icsproject2easenetics.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.icsproject2easenetics.data.models.WisdomPost
import com.example.icsproject2easenetics.utils.AccessibilityManager
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WisdomPostCard(
    post: WisdomPost,
    currentUserId: String,
    onLikeClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with user info and menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = post.userName,
                        style = AccessibilityManager.getScaledTitleMedium(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${post.getDate().toLocaleString()} • ${post.category.name.replace('_', ' ')}",
                        style = AccessibilityManager.getScaledBodySmall(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (post.userId == currentUserId) {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, "More options")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    showMenu = false
                                    onDeleteClick(post.postId)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Post content
            Text(
                text = post.title,
                style = AccessibilityManager.getScaledTitleLarge(),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.content,
                style = AccessibilityManager.getScaledBodyLarge(),
                lineHeight = AccessibilityManager.getScaledBodyLarge().lineHeight
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onLikeClick(post.postId) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (currentUserId in post.likes) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (currentUserId in post.likes) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${post.likes.size}",
                        style = AccessibilityManager.getScaledBodyMedium()
                    )
                }

                Button(
                    onClick = { onCommentClick(post.postId) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Text("Comment", style = AccessibilityManager.getScaledBodyMedium())
                }
            }
        }
    }
}

// Helper extension for Date formatting
fun Date.toLocaleString(): String {
    return android.icu.text.SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", java.util.Locale.getDefault()).format(this)
}