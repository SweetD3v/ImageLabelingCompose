package com.dev4life.imagelabeling.di

import android.content.Context
import androidx.room.Room
import com.dev4life.imagelabeling.data.dao.MediaDao
import com.dev4life.imagelabeling.data.MediaDatabase
import com.dev4life.imagelabeling.data.dao.AlbumsDao
import com.dev4life.imagelabeling.data.dao.LabelsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    fun provideMediaDao(mediaDatabase: MediaDatabase): MediaDao {
        return mediaDatabase.mediaDao()
    }

    @Provides
    fun provideAlbumsDao(mediaDatabase: MediaDatabase): AlbumsDao {
        return mediaDatabase.albumsDao()
    }

    @Provides
    fun provideLabelsDao(mediaDatabase: MediaDatabase): LabelsDao {
        return mediaDatabase.labelsDao()
    }

    @Provides
    @Singleton
    fun provideMediaDatabase(@ApplicationContext context: Context): MediaDatabase {
        return Room.databaseBuilder(
            context = context,
            MediaDatabase::class.java,
            "media_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
