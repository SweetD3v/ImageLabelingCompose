package com.dev4life.imagelabeling.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

@Composable
fun RepeatOnResume(action: () -> Unit) {
    val owner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        owner.lifecycleScope.launch {
            owner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                action()
            }
        }
    }
}

fun <T> MutableState<T>.update(newState: T) {
    if (value != newState) {
        value = newState
    }
}