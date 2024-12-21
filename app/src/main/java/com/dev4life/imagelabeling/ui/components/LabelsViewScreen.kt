package com.dev4life.imagelabeling.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dev4life.imagelabeling.R
import com.dev4life.imagelabeling.states.LabelsGroupState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun LabelsViewScreen(
    labelName: String = stringResource(R.string.app_name),
    labelState: StateFlow<LabelsGroupState>,
    navigateUp: () -> Unit
) {
    LabelsScreen(
        labelName = labelName,
        labelState = labelState,
        navigateUp = navigateUp
    )
}