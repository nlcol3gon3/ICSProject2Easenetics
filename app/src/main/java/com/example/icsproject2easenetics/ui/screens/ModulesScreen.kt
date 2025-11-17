package com.example.icsproject2easenetics.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.icsproject2easenetics.ui.components.AccessibleButton
import com.example.icsproject2easenetics.utils.AccessibilityManager
import com.example.icsproject2easenetics.ui.components.ModuleCard
import com.example.icsproject2easenetics.ui.viewmodels.ModuleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModulesScreen(
    onBack: () -> Unit,
    onModuleClick: (String) -> Unit,
    viewModel: ModuleViewModel = viewModel()
) {
    val modules by viewModel.modules.collectAsState()
    val moduleLessons by viewModel.moduleLessons.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllModules()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Learning Modules",
                        style = AccessibilityManager.getScaledTitleLarge(), // CHANGED
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    // Add refresh button
                    IconButton(
                        onClick = { viewModel.loadAllModules() },
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Filled.Refresh, "Refresh")
                        }
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
                    text = "Loading modules...",
                    style = AccessibilityManager.getScaledBodyMedium(), // CHANGED
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else if (errorMessage != null) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error Loading Data",
                            style = AccessibilityManager.getScaledTitleMedium(), // CHANGED
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = errorMessage ?: "An error occurred",
                            style = AccessibilityManager.getScaledBodyMedium(), // CHANGED
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = TextAlign.Center
                        )
                        AccessibleButton(onClick = { viewModel.loadAllModules() }) { // CHANGED
                            Text("Try Again")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (modules.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No Modules Available",
                                    style = AccessibilityManager.getScaledTitleLarge(), // CHANGED
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Check your internet connection or try again later",
                                    style = AccessibilityManager.getScaledBodyMedium(), // CHANGED
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = TextAlign.Center
                                )
                                AccessibleButton(onClick = { viewModel.loadAllModules() }) { // CHANGED
                                    Text("Retry Loading")
                                }
                            }
                        }
                    }
                } else {
                    items(modules) { module ->
                        val lessonCount = moduleLessons[module.moduleId]?.size ?: 0
                        ModuleCard(
                            module = module,
                            lessonCount = lessonCount,
                            onClick = {
                                onModuleClick(module.moduleId)
                            }
                        )
                    }
                }
            }
        }
    }
}