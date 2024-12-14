package com.dev4life.imagelabeling.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dev4life.imagelabeling.data.albums.Album
import com.dev4life.imagelabeling.data.dao.AlbumsDao
import com.dev4life.imagelabeling.data.dao.LabelsDao
import com.dev4life.imagelabeling.data.dao.MediaDao
import com.dev4life.imagelabeling.data.labels.LabelItem
import com.dev4life.imagelabeling.data.media.MediaItem

@Database(
    entities = [MediaItem::class, Album::class, LabelItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DataTypeConverters::class)
abstract class MediaDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
    abstract fun albumsDao(): AlbumsDao
    abstract fun labelsDao(): LabelsDao
}