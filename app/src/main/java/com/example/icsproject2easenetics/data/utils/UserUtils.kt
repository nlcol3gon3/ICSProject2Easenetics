package com.example.icsproject2easenetics.utils

import com.google.firebase.auth.FirebaseUser

fun extractUserName(currentUser: FirebaseUser?): String {
    return when {
        currentUser?.displayName?.isNotEmpty() == true -> currentUser.displayName!!
        currentUser?.email?.isNotEmpty() == true -> {
            currentUser.email!!.split("@").first().split(".").first()
                .replaceFirstChar { it.uppercase() }
        }
        else -> "User"
    }
}