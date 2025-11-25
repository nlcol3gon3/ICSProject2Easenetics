package com.example.icsproject2easenetics.utils

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp  // Add this import

object MarkdownParser {

    fun parseMarkdown(text: String): String {
        return text
            .replace(Regex("""\*\*(.*?)\*\*"""), "$1") // Remove **bold** markers
            .replace(Regex("""#+\s*(.*)"""), "$1")     // Remove # headings
            .replace(Regex("""\* (.*)"""), "• $1")     // Convert * bullets to •
            .trim()
    }

    fun parseToAnnotatedString(text: String): androidx.compose.ui.text.AnnotatedString {
        return buildAnnotatedString {
            val lines = text.split("\n")

            lines.forEachIndexed { index, line ->
                if (index > 0) append("\n")

                when {
                    line.startsWith("### ") -> {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)) {
                            append(line.removePrefix("### "))
                        }
                    }
                    line.startsWith("## ") -> {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)) {
                            append(line.removePrefix("## "))
                        }
                    }
                    line.startsWith("# ") -> {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp)) {
                            append(line.removePrefix("# "))
                        }
                    }
                    line.startsWith("**") && line.endsWith("**") -> {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(line.removePrefix("**").removeSuffix("**"))
                        }
                    }
                    line.startsWith("* ") -> {
                        append("• ")
                        append(line.removePrefix("* "))
                    }
                    else -> {
                        append(line)
                    }
                }
            }
        }
    }
}