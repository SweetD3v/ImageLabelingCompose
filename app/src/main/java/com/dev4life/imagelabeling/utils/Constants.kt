package com.dev4life.imagelabeling.utils

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

const val STANDARD_DATE_FORMAT = "d MMM, yyyy"
const val WEEKLY_DATE_FORMAT = "EEEE"
const val DEFAULT_DATE_FORMAT = "EEE, MMMM d"
const val EXTENDED_DATE_FORMAT = "EEE, MMM d, yyyy"
const val FULL_DATE_FORMAT = "EEEE, MMMM d, yyyy, hh:mm a"
const val HEADER_DATE_FORMAT = "MMMM d, yyyy\n" + "h:mm a"
const val EXIF_DATE_FORMAT = "MMMM d, yyyy • h:mm a"

/**
 * Value in ms
 */
const val DEFAULT_LOW_VELOCITY_SWIPE_DURATION = 150

/**
 * Smooth enough at 300ms
 */
const val DEFAULT_NAVIGATION_ANIMATION_DURATION = 300

/**
 * Syncs with status bar fade in/out
 */
const val DEFAULT_TOP_BAR_ANIMATION_DURATION = 1000

/**
 * MAX Image Size in Media Preview
 * Android LIMIT: 4096x4096 [16MP]
 */
const val MAX_IMAGE_SIZE = 4096

object Animation {

    val enterAnimation = fadeIn(tween(DEFAULT_LOW_VELOCITY_SWIPE_DURATION))
    val exitAnimation = fadeOut(tween(DEFAULT_LOW_VELOCITY_SWIPE_DURATION))

    val navigateInAnimation = fadeIn(tween(DEFAULT_NAVIGATION_ANIMATION_DURATION))
    val navigateUpAnimation = fadeOut(tween(DEFAULT_NAVIGATION_ANIMATION_DURATION))

    fun enterAnimation(durationMillis: Int): EnterTransition =
        fadeIn(tween(durationMillis))

    fun exitAnimation(durationMillis: Int): ExitTransition =
        fadeOut(tween(durationMillis))

}