package com.dev4life.imagelabeling.data.impl

import android.content.ContentResolver
import com.dev4life.imagelabeling.data.albums.Album
import com.dev4life.imagelabeling.data.dao.AlbumsDao
import com.dev4life.imagelabeling.data.repo.AlbumsRepo
import com.dev4life.imagelabeling.data.loaders.getAlbums
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlbumsRepoImpl @Inject constructor(
    private val contentResolver: ContentResolver,
    private val albumsDao: AlbumsDao
) : AlbumsRepo {
    override suspend fun loadAlbums(): List<Album> = contentResolver.getAlbums()

    override fun getAlbums(): Flow<List<Album>> {
        return albumsDao.getAllAlbums()
    }

    override suspend fun insertAlbum(album: Album) {
        albumsDao.insertAlbum(album)
    }

    override suspend fun insertAlbums(albums: List<Album>) {
        albumsDao.insertAlbums(albums)
    }
}