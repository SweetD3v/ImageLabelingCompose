package com.dev4life.imagelabeling.utils

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.dev4life.imagelabeling.data.media.MediaItem
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
sealed class MediaViewItem : Parcelable {
    abstract val key: String

    data class Header(
        override val key: String,
        val text: String,
        val data: List<MediaItem>
    ) : MediaViewItem()

    @Parcelize
    sealed class MediaViewItem1 : MediaViewItem() {

        abstract val media: MediaItem

        data class Loaded(
            override val key: String,
            override val media: MediaItem,
        ) : MediaViewItem1()
    }
}

val Any.isHeaderKey: Boolean
    get() = this is String && this.startsWith("header")

val Any.isSmallHeaderKey: Boolean
    get() = this is String && isHeaderKey && !this.contains("big")

val Any.isBigHeaderKey: Boolean
    get() = this is String && isHeaderKey && this.contains("big")

val Any.isIgnoredKey: Boolean
    get() = this is String && this == "aboveGrid"