package com.dev4life.imagelabeling.data.labels

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.dev4life.imagelabeling.data.media.MediaItem

@Entity(
    tableName = "labels",
    foreignKeys = [ForeignKey(
        entity = MediaItem::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("labelCreatorId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class LabelItem(
    @PrimaryKey(autoGenerate = true) var lId: Long = 0,
    val labelCreatorId: Long,
    val labelName: String,
    val photoPath: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readString().toString()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(lId)
        dest.writeLong(labelCreatorId)
        dest.writeString(labelName)
        dest.writeString(photoPath)
    }

    companion object CREATOR : Parcelable.Creator<LabelItem> {
        override fun createFromParcel(parcel: Parcel): LabelItem {
            return LabelItem(parcel)
        }

        override fun newArray(size: Int): Array<LabelItem?> {
            return arrayOfNulls(size)
        }
    }

}

data class LabelledMedia(
    @Embedded val photo: MediaItem,
    @Relation(
        parentColumn = "id",
        entityColumn = "labelCreatorId"
    )
    val labels: List<LabelItem>
)