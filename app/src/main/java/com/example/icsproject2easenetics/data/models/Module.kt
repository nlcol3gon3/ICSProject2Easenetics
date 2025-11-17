package com.example.icsproject2easenetics.data.models

data class Module(
    val moduleId: String = "",
    val title: String = "",
    val description: String = "",
    val order: Int = 0,
    val icon: String = "",
    val totalLessons: Int = 0
) {
    constructor() : this("", "", "", 0, "", 0)
}