package com.dev4life.imagelabeling.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun TwoLinedToolbarTitle(
    albumName: String,
    dateHeader: String = ""
) {
    Column {
        Text(
            text = albumName,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        if (dateHeader.isNotEmpty()) {
            Text(
                modifier = Modifier,
                text = dateHeader.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}