package com.dev4life.imagelabeling.utils

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.dev4life.imagelabeling.data.media.MediaItem
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
sealed class LabelViewItem : Parcelable {
    abstract val key: String

    data class Header(
        override val key: String,
        val text: String,
        val data: List<MediaItem>
    ) : LabelViewItem()

    @Parcelize
    sealed class LabelViewItem1 : LabelViewItem() {

        abstract val media: MediaItem

        data class Loaded(
            override val key: String,
            override val media: MediaItem,
        ) : LabelViewItem1()
    }
}