package com.example.icsproject2easenetics.extensions

fun String.cleanMarkdown(): String {
    var result = this

    // Handle headings - remove # but add newlines for separation
    result = Regex("""(#+)\s*(.*)""").replace(result) { match ->
        val headingText = match.groupValues[2]
        "\n$headingText\n"
    }

    // Handle bold
    result = result.replace(Regex("""\*\*(.*?)\*\*"""), "$1")

    // Handle bullet points
    result = result.replace(Regex("""\*\s+(.*)"""), "• $1")

    // Clean up extra newlines
    result = result.replace(Regex("""\n\s*\n"""), "\n\n")

    return result.trim()
}