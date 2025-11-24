package com.example.icsproject2easenetics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ShapeSequenceViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShapeSequenceViewModel::class.java)) {
            return ShapeSequenceViewModel(
                shapeSequenceRepository = com.example.icsproject2easenetics.data.repositories.ShapeSequenceRepository(),
                gameProgressRepository = com.example.icsproject2easenetics.data.repositories.GameProgressRepository()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}