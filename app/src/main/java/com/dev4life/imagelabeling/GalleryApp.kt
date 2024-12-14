package com.dev4life.imagelabeling

import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GalleryApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        instance = this
    }

    companion object {
        @Volatile
        private var instance: GalleryApp? = null

        fun getInstance(): GalleryApp {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = GalleryApp()
                    }
                }
            }
            return instance!!
        }
    }
}