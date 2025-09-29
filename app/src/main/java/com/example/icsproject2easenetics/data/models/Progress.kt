package com.example.icsproject2easenetics.data.models

data class UserProgress(
    val progressId: String = "",
    val userId: String = "",
    val lessonId: String = "",
    var completed: Boolean = false,
    var score: Int = 0,
    var timeSpent: Int = 0,
    val lastAccessed: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", false, 0, 0, System.currentTimeMillis())
}