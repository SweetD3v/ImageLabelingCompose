package com.dev4life.imagelabeling.viewmodels

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev4life.imagelabeling.states.AlbumState
import com.dev4life.imagelabeling.data.repo.AlbumsRepo
import com.dev4life.imagelabeling.utils.RepeatOnResume
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val albumsRepo: AlbumsRepo
) : ViewModel() {

    private val _albumsState = MutableStateFlow(AlbumState())
    val albumsState = _albumsState.asStateFlow()

    @SuppressLint("ComposableNaming")
    @Composable
    fun attachToLifecycle() {
        RepeatOnResume {
            getAlbums()
        }
    }

    private fun getAlbums() {
        viewModelScope.launch {
            albumsRepo.getAlbums().collectLatest { result ->
                val data = result.takeIf { it.isNotEmpty() } ?: emptyList()
                val newAlbumState = AlbumState(albums = data.filter { !it.isPinned })
                if (albumsState.value != newAlbumState) {
                    _albumsState.emit(newAlbumState)
                }
            }
        }
    }
}