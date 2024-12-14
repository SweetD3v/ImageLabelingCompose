package com.dev4life.imagelabeling.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.dev4life.imagelabeling.states.MediaState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun TimelineScreen(
    paddingValues: PaddingValues,
    mediaState: StateFlow<MediaState>
) {
    MediaScreen(
        paddingValues = paddingValues,
        mediaState = mediaState
    )
}