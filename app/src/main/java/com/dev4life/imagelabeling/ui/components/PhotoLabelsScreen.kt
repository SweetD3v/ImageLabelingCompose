package com.dev4life.imagelabeling.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev4life.imagelabeling.viewmodels.LabelsViewModel

@Composable
fun PhotoLabelsScreen(
    navigate: (route: String) -> Unit,
    paddingValues: PaddingValues,
    viewModel: LabelsViewModel = hiltViewModel(),
) {
    val state by viewModel.labelsState.collectAsStateWithLifecycle()
    val albumSize by remember { mutableIntStateOf(3) }
    val gridState = rememberLazyGridState()

    if (!state.isLoading) {
        Scaffold(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .fillMaxSize()
        ) {

            if (state.labels.isEmpty()) {
                EmptyLabels("No labels found")
            } else {
                LazyVerticalGrid(
                    state = gridState,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxSize(),
                    columns = GridCells.Fixed(albumSize),
                    contentPadding = PaddingValues(
                        top = it.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding() + 16.dp + 64.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = state.labels,
                        key = { item -> item.labelItems.firstOrNull()?.labelName ?: "" }
                    ) { item ->
                        LabelComponent(
                            labeledPhoto = item,
                            onItemClick = viewModel.onLabelClick(navigate),
                        )
                    }
                }
            }
        }
    }
}