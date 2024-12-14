package com.dev4life.imagelabeling.data.media

import android.net.Uri
import android.os.Parcelable
import android.webkit.MimeTypeMap
import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.File

@Immutable
@Parcelize
@Entity(tableName = "media_item")
data class MediaItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,

    @ColumnInfo(name = "media_id")
    val mediaId: Long = 0L,
    @ColumnInfo(name = "bucket_id")
    var bucketId: Long = 0L,
    @ColumnInfo(name = "name")
    var label: String = "",
    @ColumnInfo(name = "album_name")
    var albumName: String = "",
    @ColumnInfo(name = "path")
    var path: String = "",
    @ColumnInfo(name = "uri")
    var uri: Uri = Uri.EMPTY,
    @ColumnInfo(name = "mime_type")
    var mimeType: String = "",
    @ColumnInfo(name = "date_added")
    var dateAdded: Long = 0L,
    @ColumnInfo(name = "size")
    var size: Long = 0L,
    @ColumnInfo(name = "duration")
    var duration: Long = 0L,
    @ColumnInfo(name = "orientation")
    var orientation: Int = 0,
    @ColumnInfo(name = "is_trashed")
    var isTrashed: Boolean = false,
    @ColumnInfo(name = "is_favourite")
    var isFavourite: Boolean = false,

    @ColumnInfo(name = "is_curated")
    var isCurated: Boolean = false,

    @ColumnInfo(name = "labelId") var labelId: Long = 0
) : Parcelable {

    override fun toString(): String {
        return "$id, $path, $dateAdded, $mimeType"
    }

    @IgnoredOnParcel
    @Ignore
    val isRaw: Boolean =
        mimeType.isNotBlank() && (mimeType.startsWith("image/x-") || mimeType.startsWith("image/vnd."))

    @IgnoredOnParcel
    @Ignore
    val fileExtension: String = label.substringAfterLast(".").removePrefix(".")

    companion object {
        fun createFromUri(uri: Uri): MediaItem? {
            if (uri.path == null) return null
            val extension = uri.toString().substringAfterLast(".")
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension).toString()
            var timestamp = 0L
            uri.path?.let { File(it) }?.let {
                timestamp = try {
                    it.lastModified()
                } catch (_: Exception) {
                    0L
                }
            }
            return MediaItem(
                label = uri.toString().substringAfterLast("/"),
                uri = uri,
                path = uri.path.toString(),
                bucketId = -99L,
                albumName = "",
                dateAdded = timestamp * 1000L,
                mimeType = mimeType,
                isFavourite = false,
                isTrashed = false,
                orientation = 0
            )
        }
    }
}