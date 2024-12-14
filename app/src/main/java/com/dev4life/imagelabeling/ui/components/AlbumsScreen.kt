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
import com.dev4life.imagelabeling.viewmodels.AlbumsViewModel

@Composable
fun AlbumsScreen(
    paddingValues: PaddingValues,
    viewModel: AlbumsViewModel = hiltViewModel(),
) {
    val state by viewModel.albumsState.collectAsStateWithLifecycle()
    val albumSize by remember { mutableIntStateOf(2) }
    val gridState = rememberLazyGridState()

    Scaffold(
        modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
    ) {
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
                items = state.albums,
                key = { item -> item.toString() }
            ) { item ->
                AlbumComponent(
                    album = item,
                    onItemClick = null
                )
            }
        }
    }
}