package com.dev4life.imagelabeling.data

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter

class DataTypeConverters {
    @TypeConverter
    fun fromSource(source: Uri): String {
        return source.toString()
    }

    @TypeConverter
    fun toSource(source: String): Uri {
        return source.toUri()
    }
}