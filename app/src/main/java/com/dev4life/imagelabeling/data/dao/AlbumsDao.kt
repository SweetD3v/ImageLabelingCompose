package com.dev4life.imagelabeling.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dev4life.imagelabeling.data.albums.Album
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumsDao {
    @Query("SELECT * FROM albums ORDER BY timestamp DESC")
    fun getAllAlbums(): Flow<List<Album>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: Album)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albumsList: List<Album>)
}