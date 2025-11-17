package com.example.icsproject2easenetics.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.icsproject2easenetics.ui.viewmodels.ModuleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugScreen(
    onBack: () -> Unit,
    viewModel: ModuleViewModel = viewModel()
) {
    val modules by viewModel.modules.collectAsState()
    val moduleLessons by viewModel.moduleLessons.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllModules()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Debug - Data Loading") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (modules.isNotEmpty()) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Data Loading Status", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Modules: ${modules.size}")
                        Text("Module-Lessons Map: ${moduleLessons.size} entries")
                        Text("Total Lessons: ${moduleLessons.values.sumOf { it.size }}")
                        Text("Loading: $isLoading")
                    }
                }
            }

            if (modules.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("❌ NO MODULES LOADED", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Check Firebase connection and security rules")
                        }
                    }
                }
            }

            items(modules) { module ->
                val lessons = moduleLessons[module.moduleId] ?: emptyList()
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(module.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("ID: ${module.moduleId}")
                        Text("Expected Lessons: ${module.totalLessons}")
                        Text("Actual Lessons: ${lessons.size}")

                        if (lessons.isEmpty()) {
                            Text("❌ NO LESSONS LOADED", color = MaterialTheme.colorScheme.error)
                        } else {
                            lessons.forEach { lesson ->
                                Text("   ✅ ${lesson.lessonId}: ${lesson.title}")
                            }
                        }
                    }
                }
            }
        }
    }
}