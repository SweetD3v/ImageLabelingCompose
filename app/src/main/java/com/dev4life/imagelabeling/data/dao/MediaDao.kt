package com.dev4life.imagelabeling.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.dev4life.imagelabeling.data.labels.LabelledMedia
import com.dev4life.imagelabeling.data.media.MediaItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM media_item ORDER BY date_added DESC")
    fun getAllMedia(): Flow<List<MediaItem>>

    @Query("SELECT * FROM media_item WHERE is_curated = 0 ORDER BY date_added DESC")
    fun getUnlabeledMedia(): List<MediaItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(mediaItem: MediaItem)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMedia(mediaItem: MediaItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMedia(mediaList: List<MediaItem>)

    @Query("DELETE FROM media_item WHERE id IN (:ids)")
    suspend fun deleteMediaByIds(ids: List<Long>)

    @Transaction
    @Query("SELECT * FROM media_item")
    fun getPhotosWithLabels(): Flow<List<LabelledMedia>>
}