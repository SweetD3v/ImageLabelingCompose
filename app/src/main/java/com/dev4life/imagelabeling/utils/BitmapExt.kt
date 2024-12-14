package com.dev4life.imagelabeling.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun String.pathToBitmap(): Bitmap {
    val op = BitmapFactory.Options().apply {
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }
    return BitmapFactory.decodeFile(this, op) ?: Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888)
}

fun Bitmap.scaleBitmapTo(maxWidth: Int, maxHeight: Int): Bitmap {
    var width = this.width
    var height = this.height
    if (width > height) {
        // landscape
        val ratio = width.toFloat() / maxWidth
        width = maxWidth
        height = (height / ratio).toInt()
    } else if (height > width) {
        // portrait
        val ratio = height.toFloat() / maxHeight
        height = maxHeight
        width = (width / ratio).toInt()
    } else {
        // square
        height = maxHeight
        width = maxWidth
    }
    return Bitmap.createScaledBitmap(this, width, height, true)
}