package com.example.icsproject2easenetics.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.icsproject2easenetics.ui.components.LessonCard
import com.example.icsproject2easenetics.ui.viewmodels.ModuleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleLessonsScreen(
    moduleId: String,
    onBack: () -> Unit,
    onLessonClick: (String) -> Unit,
    viewModel: ModuleViewModel = viewModel()
) {
    val modules by viewModel.modules.collectAsState()
    val moduleLessons by viewModel.moduleLessons.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val currentModule = modules.find { it.moduleId == moduleId }
    val lessons = moduleLessons[moduleId] ?: emptyList()

    LaunchedEffect(moduleId) {
        if (lessons.isEmpty()) {
            viewModel.loadAllModules()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text(
                            text = currentModule?.title ?: "Module",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (currentModule?.description != null) {
                            Text(
                                text = currentModule.description,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text(
                    text = "Loading lessons...",
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else if (lessons.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No Lessons Available",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Lessons for this module will be available soon",
                            modifier = Modifier.padding(16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "${lessons.size} Lessons",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(lessons) { lesson ->
                    LessonCard(
                        title = lesson.title,
                        description = lesson.objective,
                        duration = "${lesson.duration} min",
                        progress = 0, // Will be loaded from user progress
                        category = getCategoryIcon(lesson.moduleId),
                        onClick = { onLessonClick(lesson.lessonId) }
                    )
                }
            }
        }
    }
}

private fun getCategoryIcon(moduleId: String): Int {
    return when (moduleId) {
        "module_1" -> 0 // Smartphone
        "module_2" -> 1 // Communication
        "module_3" -> 2 // M-Pesa
        "module_4" -> 3 // Safety
        "module_5" -> 4 // Government
        else -> 0
    }
}