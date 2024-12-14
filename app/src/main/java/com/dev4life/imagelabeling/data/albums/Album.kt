package com.dev4life.imagelabeling.data.albums

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
@Entity(tableName = "albums")
data class Album(
    @PrimaryKey
    val id: Long = 0,
    val label: String,
    val pathToThumbnail: String,
    val timestamp: Long,
    var count: Long = 0,
    val selected: Boolean = false,
    val isPinned: Boolean = false,
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        return if (other !is Album) false
        else other.id == id
                && other.label == label
                && other.pathToThumbnail == pathToThumbnail
                && other.timestamp == timestamp
                && other.count == count
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + label.hashCode()
        result = 31 * result + pathToThumbnail.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + count.hashCode()
        result = 31 * result + selected.hashCode()
        result = 31 * result + isPinned.hashCode()
        return result
    }
}