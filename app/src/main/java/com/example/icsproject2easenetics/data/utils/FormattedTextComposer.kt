package com.example.icsproject2easenetics.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun FormattedTextComposer(text: String) {
    val lines = text.split('\n')
    var currentListItems = mutableListOf<String>()

    Column {
        lines.forEachIndexed { index, line ->
            val trimmedLine = line.trim()

            when {
                // Skip empty lines at the beginning
                index == 0 && trimmedLine.isEmpty() -> Unit

                // Headers
                trimmedLine.startsWith("# ") -> {
                    if (currentListItems.isNotEmpty()) {
                        renderListItems(currentListItems)
                        currentListItems.clear()
                    }
                    Text(
                        text = trimmedLine.removePrefix("# "),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Subheaders
                trimmedLine.startsWith("## ") -> {
                    if (currentListItems.isNotEmpty()) {
                        renderListItems(currentListItems)
                        currentListItems.clear()
                    }
                    Text(
                        text = trimmedLine.removePrefix("## "),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                // List items
                trimmedLine.startsWith("- ") || trimmedLine.startsWith("• ") -> {
                    currentListItems.add(trimmedLine.removePrefix("- ").removePrefix("• "))
                }

                // Empty line (paragraph break)
                trimmedLine.isEmpty() -> {
                    if (currentListItems.isNotEmpty()) {
                        renderListItems(currentListItems)
                        currentListItems.clear()
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Regular text with potential bold formatting
                else -> {
                    if (currentListItems.isNotEmpty()) {
                        renderListItems(currentListItems)
                        currentListItems.clear()
                    }
                    if (trimmedLine.contains("**")) {
                        BoldFormattedText(trimmedLine)
                    } else {
                        Text(
                            text = trimmedLine,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }
        }

        if (currentListItems.isNotEmpty()) {
            renderListItems(currentListItems)
        }
    }
}

@Composable
private fun renderListItems(items: List<String>) {
    Column(modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)) {
        items.forEach { item ->
            Row(Modifier.padding(vertical = 2.dp)) {
                Text(
                    text = "• ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (item.contains("**")) {
                    BoldFormattedText(item)
                } else {
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun BoldFormattedText(text: String) {
    val annotatedString = buildAnnotatedString {
        var currentText = text
        while (currentText.contains("**")) {
            val startIndex = currentText.indexOf("**")
            val beforeBold = currentText.substring(0, startIndex)

            // Add normal text before bold
            append(beforeBold)

            // Find the end of bold section
            val afterStart = currentText.substring(startIndex + 2)
            val endIndex = afterStart.indexOf("**")

            if (endIndex != -1) {
                val boldText = afterStart.substring(0, endIndex)

                // Add bold text
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(boldText)
                }

                // Update current text
                currentText = afterStart.substring(endIndex + 2)
            } else {
                append("**$afterStart")
                currentText = ""
            }
        }

        if (currentText.isNotEmpty()) {
            append(currentText)
        }
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}