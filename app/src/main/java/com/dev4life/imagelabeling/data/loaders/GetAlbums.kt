package com.dev4life.imagelabeling.data.loaders

import android.content.ContentResolver
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import com.dev4life.imagelabeling.data.MediaOrder
import com.dev4life.imagelabeling.data.OrderType
import com.dev4life.imagelabeling.data.albums.Album
import com.dev4life.imagelabeling.di.Query
import com.dev4life.imagelabeling.utils.isOreoPlus
import com.dev4life.imagelabeling.utils.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun ContentResolver.getAlbums(
    query: Query = Query.AlbumQuery(), mediaOrder: MediaOrder = MediaOrder.Date(
        OrderType.Descending
    )
): List<Album> {
    val cr = this
    return withContext(Dispatchers.IO) {
        val albums = ArrayList<Album>()
        val bundle = query.bundle ?: Bundle()
        val albumQuery = if (isOreoPlus()) {
            query.copy(
                bundle = bundle.apply {
                    putInt(
                        ContentResolver.QUERY_ARG_SORT_DIRECTION,
                        ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
                    )
                    putStringArray(
                        ContentResolver.QUERY_ARG_SORT_COLUMNS,
                        arrayOf(MediaStore.MediaColumns.DATE_MODIFIED)
                    )
                }
            )
        } else {
            query
        }
        with(query(albumQuery)) {
            moveToFirst()
            while (!isAfterLast) {
                try {
                    val id = getLong(getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID))
                    val label: String? = try {
                        getString(getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                    } catch (e: Exception) {
                        Build.MODEL
                    }
                    val thumbnailPath =
                        getString(getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                    val thumbnailDate =
                        getLong(getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED))
                    val album =
                        Album(id, label ?: Build.MODEL, thumbnailPath, thumbnailDate, count = 1)
                    val currentAlbum = albums.find { albm -> albm.id == id }
                    if (currentAlbum == null)
                        albums.add(album)
                    else {
                        val i = albums.indexOf(currentAlbum)
                        albums[i].count++
                        if (albums[i].timestamp <= thumbnailDate) {
                            album.count = albums[i].count
                            albums[i] = album
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                moveToNext()
            }
            close()
        }
        return@withContext mediaOrder.sortAlbums(albums)
    }
}