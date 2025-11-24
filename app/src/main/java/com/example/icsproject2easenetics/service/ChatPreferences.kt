package com.example.icsproject2easenetics.service

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

// Extension property for DataStore
val Context.dataStore by preferencesDataStore(name = "chat_prefs")

class ChatPreferences(private val context: Context) {

    companion object {
        private val CHAT_HISTORY_KEY = stringSetPreferencesKey("chat_history")
    }

    // Save chat history
    suspend fun saveChatHistory(messages: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[CHAT_HISTORY_KEY] = messages.toSet()
        }
    }

    // Load chat history
    suspend fun loadChatHistory(): List<String> {
        val prefs = context.dataStore.data.first()
        return prefs[CHAT_HISTORY_KEY]?.toList() ?: emptyList()
    }
}
