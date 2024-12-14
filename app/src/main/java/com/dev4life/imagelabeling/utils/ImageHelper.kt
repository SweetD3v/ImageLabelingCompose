package com.dev4life.imagelabeling.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import android.os.Parcelable
import com.bumptech.glide.load.Key
import kotlinx.parcelize.Parcelize
import java.nio.ByteBuffer
import java.security.MessageDigest

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "GALLERY_PREFS")

object Glide {
    private val DISK_CACHE_SIZE = longPreferencesKey("disk_cache_size")

    @Composable
    fun rememberDiskCacheSize() =
        rememberPreference(key = DISK_CACHE_SIZE, defaultValue = 150)

    fun getDiskCacheSize(context: Context) =
        context.dataStore.data.map { it[DISK_CACHE_SIZE] ?: 150 }

    private val CACHED_SCREEN_COUNT = floatPreferencesKey("cached_screen_count")

    @Composable
    fun rememberCachedScreenCount() =
        rememberPreference(key = CACHED_SCREEN_COUNT, defaultValue = 80f)

    fun getCachedScreenCount(context: Context) =
        context.dataStore.data.map { it[CACHED_SCREEN_COUNT] ?: 80f }

    private val MAX_IMAGE_SIZE = intPreferencesKey("max_image_size")

    @Composable
    fun rememberMaxImageSize() =
        rememberPreference(key = MAX_IMAGE_SIZE, defaultValue = 4096)
}

@Composable
fun <T> rememberPreference(
    key: Preferences.Key<T>,
    defaultValue: T,
): MutableState<T> {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val state by remember {
        context.dataStore.data
            .map { it[key] ?: defaultValue }
    }.collectAsStateWithLifecycle(initialValue = defaultValue)

    return remember(state) {
        object : MutableState<T> {
            override var value: T
                get() = state
                set(value) {
                    coroutineScope.launch {
                        context.dataStore.edit {
                            it[key] = value
                        }
                    }
                }

            override fun component1() = value
            override fun component2(): (T) -> Unit = { value = it }
        }
    }
}

@Parcelize
data class MediaKey(val id: Long, val timestamp: Long, val mimeType: String, val orientation: Int): Key, Parcelable {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        val data = ByteBuffer.allocate(20).putLong(id).putLong(timestamp).putInt(orientation)
        messageDigest.update(data)
        messageDigest.update(mimeType.toByteArray(Key.CHARSET))
    }
}