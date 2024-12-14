package com.dev4life.imagelabeling.states

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.dev4life.imagelabeling.data.albums.Album
import com.dev4life.imagelabeling.data.labels.LabelItem
import com.dev4life.imagelabeling.data.media.MediaItem
import com.dev4life.imagelabeling.utils.LabelViewItem
import com.dev4life.imagelabeling.utils.MediaViewItem
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class MediaState(
    val media: List<MediaItem> = emptyList(),
    val mappedMedia: List<MediaViewItem> = emptyList(),
    val dateHeader: String = "",
    val error: String = "",
    val isLoading: Boolean = true
) : Parcelable

data class CurateLabelsState(
    val curationState: CurationState = CurationState.Idle,
)


@Immutable
@Parcelize
data class AlbumState(
    val albums: List<Album> = emptyList(),
    val error: String = ""
) : Parcelable

@Immutable
data class LabelState(
    val labels: SnapshotStateList<GroupedLabelItem> = mutableStateListOf(),
    val isLoading: Boolean = false
)

data class GroupedLabelItem(val name: String, val labelItems: List<LabelItem>)

data class LabelProcessState(
    val counts: Int = 0,
    val total: Int = 0,
    val isCurating: Boolean = false
)

@Immutable
@Parcelize
data class LabelsGroupState(
    val media: List<MediaItem> = emptyList(),
    val mappedMedia: List<LabelViewItem> = emptyList(),
    val dateHeader: String = "",
    val error: String = "",
    val isLoading: Boolean = true
) : Parcelable