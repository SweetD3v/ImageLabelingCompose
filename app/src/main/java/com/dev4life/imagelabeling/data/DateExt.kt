package com.dev4life.imagelabeling.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DateExt(val month: String, val day: Int, val year: Int): Parcelable