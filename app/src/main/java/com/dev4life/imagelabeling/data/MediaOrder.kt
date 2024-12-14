package com.dev4life.imagelabeling.data

import com.dev4life.imagelabeling.data.albums.Album
import com.dev4life.imagelabeling.data.media.MediaItem


sealed class MediaOrder(private val orderType: OrderType) {
    class Label(orderType: OrderType) : MediaOrder(orderType)
    class Date(orderType: OrderType) : MediaOrder(orderType)

    fun copy(orderType: OrderType): MediaOrder {
        return when (this) {
            is Date -> Date(orderType)
            is Label -> Label(orderType)
        }
    }

    fun sortMedia(media: List<MediaItem>): List<MediaItem> {
        return when (orderType) {
            OrderType.Ascending -> {
                when (this) {
                    is Date -> media.sortedBy { it.dateAdded }
                    is Label -> media.sortedBy { it.label.lowercase() }
                }
            }

            OrderType.Descending -> {
                when (this) {
                    is Date -> media.sortedByDescending { it.dateAdded }
                    is Label -> media.sortedByDescending { it.label.lowercase() }
                }
            }
        }
    }

    fun sortAlbums(albums: List<Album>): List<Album> {
        return when (orderType) {
            OrderType.Ascending -> {
                when (this) {
                    is Date -> albums.sortedBy { it.timestamp }
                    is Label -> albums.sortedBy { it.label.lowercase() }
                }
            }

            OrderType.Descending -> {
                when (this) {
                    is Date -> albums.sortedByDescending { it.timestamp }
                    is Label -> albums.sortedByDescending { it.label.lowercase() }
                }
            }
        }
    }
}