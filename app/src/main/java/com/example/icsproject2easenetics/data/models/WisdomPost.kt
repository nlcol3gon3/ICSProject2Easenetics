package com.example.icsproject2easenetics.data.models

import com.google.firebase.Timestamp
import java.util.Date

// Define WisdomCategory FIRST
enum class WisdomCategory {
    ADVICE,
    EXPERIENCE,
    HISTORICAL_EVENT,
    ENCOURAGEMENT,
    LIFE_LESSON
}

data class WisdomPost(
    val postId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val title: String = "",
    val content: String = "",
    val category: WisdomCategory = WisdomCategory.ADVICE,
    val timestamp: Timestamp = Timestamp.now(),
    val likes: List<String> = emptyList(),
    val isPublic: Boolean = true
) {
    // Add constructor that handles Firestore field mapping
    constructor() : this("", "", "", "", "", "", WisdomCategory.ADVICE, Timestamp.now(), emptyList(), true)

    // Helper to convert to Date for UI
    fun getDate(): Date {
        return timestamp.toDate()
    }

    // Helper method to convert to Firestore-compatible map
    fun toFirestoreMap(): Map<String, Any> {
        return mapOf(
            "postId" to postId,
            "userId" to userId,
            "userName" to userName,
            "userEmail" to userEmail,
            "title" to title,
            "content" to content,
            "category" to category.name,
            "timestamp" to timestamp,
            "likes" to likes,
            "isPublic" to isPublic
        )
    }
}
data class WisdomComment(
    val commentId: String = "",
    val postId: String = "",
    val userId: String = "",
    val userName: String = "",
    val content: String = "",
    val timestamp: Timestamp = Timestamp.now()
) {
    constructor() : this("", "", "", "", "", Timestamp.now())

    // Helper to convert to Date for UI
    fun getDate(): Date {
        return timestamp.toDate()
    }
}