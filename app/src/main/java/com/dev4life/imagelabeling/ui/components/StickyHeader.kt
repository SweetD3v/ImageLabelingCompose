package com.dev4life.imagelabeling.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StickyHeader(
    date: String,
    showAsBig: Boolean = false
) {
    val smallModifier = Modifier
        .padding(
            horizontal = 16.dp,
            vertical = 24.dp
        )
        .fillMaxWidth()
    val bigModifier = Modifier
        .padding(horizontal = 16.dp)
        .padding(top = 80.dp)
    val bigTextStyle = MaterialTheme.typography.headlineMedium.copy(
        fontWeight = FontWeight.Bold
    )
    val smallTextStyle = MaterialTheme.typography.titleMedium
    Row(
        modifier = if (showAsBig) bigModifier else smallModifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = date,
            style = if (showAsBig) bigTextStyle else smallTextStyle,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}