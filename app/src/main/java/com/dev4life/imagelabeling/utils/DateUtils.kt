package com.dev4life.imagelabeling.utils

import android.text.format.DateFormat
import android.text.format.DateUtils
import com.dev4life.imagelabeling.data.DateExt
import com.dev4life.imagelabeling.data.media.MediaItem
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Long.getDate(
    format: CharSequence = STANDARD_DATE_FORMAT,
    weeklyFormat: CharSequence = WEEKLY_DATE_FORMAT,
    extendedFormat: CharSequence = EXTENDED_DATE_FORMAT,
    stringToday: String = "Today",
    stringYesterday: String = "Yesterday"
): String {
    val currentDate = Calendar.getInstance(Locale.US)
    currentDate.timeInMillis = System.currentTimeMillis()
    val mediaDate = Calendar.getInstance(Locale.US)
    mediaDate.timeInMillis = this * 1000L
    val different: Long = System.currentTimeMillis() - mediaDate.timeInMillis
    val secondsInMilli: Long = 1000
    val minutesInMilli = secondsInMilli * 60
    val hoursInMilli = minutesInMilli * 60
    val daysInMilli = hoursInMilli * 24

    val daysDifference = different / daysInMilli

    return when {
        mediaDate.timeInMillis.isToday() -> {
            stringToday
        }

        mediaDate.timeInMillis.isYesterday() -> {
            stringYesterday
        }

        else -> {
            if (daysDifference.toInt() in 2..5) {
                DateFormat.format(weeklyFormat, mediaDate).toString()
            } else {
                if (currentDate.get(Calendar.YEAR) > mediaDate.get(Calendar.YEAR)) {
                    DateFormat.format(extendedFormat, mediaDate).toString()
                } else DateFormat.format(format, mediaDate).toString()
            }
        }
    }
}

fun Long.isYesterday(): Boolean {
    val d = Date(this)
    return DateUtils.isToday(d.time + DateUtils.DAY_IN_MILLIS)
}

fun Long.isToday(): Boolean {
    val d = Date(this)
    return DateUtils.isToday(d.time)
}

fun List<MediaItem>.dateHeader(albumId: Long): String =
    if (albumId != -1L) {
        val startDate: DateExt = last().dateAdded.getDateExt()
        val endDate: DateExt = first().dateAdded.getDateExt()
        getDateHeader(startDate, endDate)
    } else ""

fun getDateHeader(startDate: DateExt, endDate: DateExt): String {
    return if (startDate.year == endDate.year) {
        if (startDate.month == endDate.month) {
            if (startDate.day == endDate.day) {
                "${startDate.month} ${startDate.day}, ${startDate.year}"
            } else "${startDate.month} ${startDate.day} - ${endDate.day}, ${startDate.year}"
        } else
            "${startDate.month} ${startDate.day} - ${endDate.month} ${endDate.day}, ${startDate.year}"
    } else {
        "${startDate.month} ${startDate.day}, ${startDate.year} - ${endDate.month} ${endDate.day}, ${endDate.year}"
    }
}

fun Long.getDateExt(): DateExt {
    val mediaDate = Calendar.getInstance(Locale.US)
    mediaDate.timeInMillis = this * 1000L
    return DateExt(
        month = mediaDate.getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, Locale.US)!!,
        day = mediaDate.get(Calendar.DAY_OF_MONTH),
        year = mediaDate.get(Calendar.YEAR)
    )
}