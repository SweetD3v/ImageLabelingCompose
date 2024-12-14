package com.dev4life.imagelabeling.data.repo

import com.dev4life.imagelabeling.data.labels.LabelledMedia
import com.dev4life.imagelabeling.data.media.MediaItem
import kotlinx.coroutines.flow.Flow

interface MediaRepo {
    suspend fun insertMedia(media: MediaItem)
    suspend fun insertAllMedia(mediaList: List<MediaItem>)
    suspend fun updateMedia(media: MediaItem)
    fun getAllMedia(): Flow<List<MediaItem>>
    fun getUnlabeledMedia(): List<MediaItem>
    suspend fun deleteMediaByIds(ids: List<Long>)

    fun getPhotosWithLabels(): Flow<List<LabelledMedia>>
}