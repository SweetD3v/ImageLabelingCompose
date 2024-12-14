package com.dev4life.imagelabeling.data.repo

import com.dev4life.imagelabeling.data.albums.Album
import kotlinx.coroutines.flow.Flow

interface AlbumsRepo {
    suspend fun loadAlbums(): List<Album>
    fun getAlbums(): Flow<List<Album>>
    suspend fun insertAlbum(album: Album)
    suspend fun insertAlbums(albums: List<Album>)
}