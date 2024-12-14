package com.dev4life.imagelabeling.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dev4life.imagelabeling.R

@Composable
fun NavigationButton(
    navigateUp: () -> Unit,
) {
    val onClick: () -> Unit = navigateUp
    val icon = Icons.AutoMirrored.Rounded.ArrowBack
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(R.string.back_cd)
        )
    }
}