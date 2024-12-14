package com.dev4life.imagelabeling.di

import com.dev4life.imagelabeling.data.impl.AlbumsRepoImpl
import com.dev4life.imagelabeling.data.impl.LabelsRepoImpl
import com.dev4life.imagelabeling.data.impl.MediaRepoImpl
import com.dev4life.imagelabeling.data.repo.AlbumsRepo
import com.dev4life.imagelabeling.data.repo.LabelsRepo
import com.dev4life.imagelabeling.data.repo.MediaRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindMediaRepo(impl: MediaRepoImpl): MediaRepo

    @Binds
    abstract fun bindAlbumsRepo(impl: AlbumsRepoImpl): AlbumsRepo

    @Binds
    abstract fun bindLabelsRepo(impl: LabelsRepoImpl): LabelsRepo
}