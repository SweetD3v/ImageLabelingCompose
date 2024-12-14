package com.dev4life.imagelabeling.data.impl

import com.dev4life.imagelabeling.data.dao.MediaDao
import com.dev4life.imagelabeling.data.labels.LabelledMedia
import com.dev4life.imagelabeling.data.media.MediaItem
import com.dev4life.imagelabeling.data.repo.MediaRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MediaRepoImpl @Inject constructor(private val mediaDao: MediaDao) : MediaRepo {
    override suspend fun insertMedia(media: MediaItem) = mediaDao.insertMedia(media)
    override fun getAllMedia(): Flow<List<MediaItem>> = mediaDao.getAllMedia()
    override suspend fun insertAllMedia(mediaList: List<MediaItem>) =
        mediaDao.insertAllMedia(mediaList)

    override fun getUnlabeledMedia(): List<MediaItem> {
        return mediaDao.getUnlabeledMedia()
    }

    override suspend fun updateMedia(media: MediaItem) {
        mediaDao.updateMedia(media)
    }

    override suspend fun deleteMediaByIds(ids: List<Long>) = mediaDao.deleteMediaByIds(ids)

    override fun getPhotosWithLabels(): Flow<List<LabelledMedia>> = mediaDao.getPhotosWithLabels()
}