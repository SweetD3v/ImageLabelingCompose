package com.dev4life.imagelabeling.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.database.MergeCursor
import android.os.Build
import android.provider.MediaStore
import com.dev4life.imagelabeling.data.media.MediaItem
import com.dev4life.imagelabeling.di.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun ContentResolver.query(
    mediaQuery: Query
): Cursor {
    return withContext(Dispatchers.IO) {
        return@withContext if (isOreoPlus()) {
            MergeCursor(
                arrayOf(
                    query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        mediaQuery.projection,
                        mediaQuery.bundle,
                        null
                    ),
                    query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        mediaQuery.projection,
                        mediaQuery.bundle,
                        null
                    )
                )
            )
        } else {
            MergeCursor(
                arrayOf(
                    query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        mediaQuery.projection,
                        null,
                        null,
                        null
                    ),
                    query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        mediaQuery.projection,
                        null,
                        null,
                        null
                    )
                )
            )
        }
    }
}

suspend fun ContentResolver.updateMedia(
    media: MediaItem,
    contentValues: ContentValues
): Boolean = withContext(Dispatchers.IO) {
    val selection = "${MediaStore.MediaColumns._ID} = ?"
    val selectionArgs = arrayOf(media.id.toString())
    val uri = if (media.mimeType.startsWith("image")) MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    else MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    return@withContext try {
        update(
            uri,
            contentValues,
            selection,
            selectionArgs
        ) > 0
    } catch (_: NullPointerException) {
        false
    }
}

//suspend fun ContentResolver.updateMediaExif(
//    media: MediaItem,
//    exifAttributes: ExifAttributes
//) = withContext(Dispatchers.IO) {
//    return@withContext try {
//        openFileDescriptor(media.uri, "rw").use { imagePfd ->
//            if (imagePfd != null) {
//                val exif = ExifInterface(imagePfd.fileDescriptor)
//                exifAttributes.writeExif(exif)
//                exif.saveAttributes()
//            }
//            true
//        }
//    } catch (e: java.lang.Exception) {
//        e.printStackTrace()
//        false
//    }
//}


@Throws(Exception::class)
fun Cursor.getMediaFromCursor(): MediaItem {
    val id: Long =
        getLong(getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
    val path: String =
        getString(getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
    val title: String =
        getString(getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
    val albumID: Long =
        getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID))
    val albumLabel: String = try {
        getString(getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME))
    } catch (_: Exception) {
        Build.MODEL
    }
    val takenTimestamp: Long? = try {
        getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_TAKEN))
    } catch (_: Exception) {
        null
    }
    val modifiedTimestamp: Long =
        getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED))
    val duration: Long = try {
        getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION))
    } catch (_: Exception) {
        0L
    }
    val orientation: Int =
        getInt(getColumnIndexOrThrow(MediaStore.MediaColumns.ORIENTATION))
    val mimeType: String =
        getString(getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE))
    val isFavorite: Int =
        getInt(getColumnIndexOrThrow(MediaStore.MediaColumns.IS_FAVORITE))
    val isTrashed: Int =
        getInt(getColumnIndexOrThrow(MediaStore.MediaColumns.IS_TRASHED))
    val contentUri = if (mimeType.contains("image"))
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    else
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    val uri = ContentUris.withAppendedId(
        contentUri,
        getLong(getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
    )
    val formattedDate = modifiedTimestamp.getDate(FULL_DATE_FORMAT)
    return MediaItem(
        id = id,
        label = title,
        uri = uri,
        path = path,
        bucketId = albumID,
        albumName = albumLabel,
        dateAdded = modifiedTimestamp,
//        takenTimestamp = takenTimestamp,
//        fullDate = formattedDate,
        duration = duration,
        isFavourite = isFavorite == 1,
        isTrashed = isTrashed == 1,
        orientation = orientation,
        mimeType = mimeType
    )
}