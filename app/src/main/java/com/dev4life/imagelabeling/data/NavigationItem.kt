package com.dev4life.imagelabeling.data

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val name: String,
    val route: String,
    val icon: ImageVector,
)