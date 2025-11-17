package com.example.icsproject2easenetics.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.icsproject2easenetics.utils.AccessibilityManager

@Composable
fun AccessibleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: androidx.compose.material3.ButtonColors = ButtonDefaults.buttonColors(),
    content: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(AccessibilityManager.getButtonHeight())
            .widthIn(min = AccessibilityManager.getButtonMinWidth()),
        enabled = enabled,
        contentPadding = PaddingValues(horizontal = 16.dp),
        colors = colors
    ) {
        content()
    }
}