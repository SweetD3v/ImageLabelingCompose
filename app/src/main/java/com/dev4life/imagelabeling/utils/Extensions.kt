package com.dev4life.imagelabeling.utils

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.DisplayMetrics
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.core.content.ContextCompat
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.log10

val photoExtensions: Array<String>
    get() = arrayOf(
        ".jpg",
        ".png",
        ".jpeg",
        ".bmp",
        ".webp",
        ".heic",
        ".heif",
        ".apng",
        ".avif"
    )
val videoExtensions: Array<String>
    get() = arrayOf(
        ".mp4",
        ".mkv",
        ".webm",
        ".avi",
        ".3gp",
        ".mov",
        ".m4v",
        ".3gpp"
    )

val rawExtensions: Array<String>
    get() = arrayOf(
        ".dng",
        ".orf",
        ".nef",
        ".arw",
        ".rw2",
        ".cr2",
        ".cr3"
    )

fun String.isVideo() = videoExtensions.any { endsWith(it, true) }

fun String.isImage() = photoExtensions.any { endsWith(it, true) }

fun String.isRawFast() = rawExtensions.any { endsWith(it, true) }

fun String.isPng() = endsWith(".png", true)

fun String.isApng() = endsWith(".apng", true)

fun String.isJpg() = endsWith(".jpg", true) or endsWith(".jpeg", true)

fun String.isWebP() = endsWith(".webp", true)

fun String.isGif() = endsWith(".gif", true)

fun String.isSvg() = endsWith(".svg", true)

fun String.isPortrait() = getFilenameFromPath().contains(
    "portrait",
    true
) && File(this).parentFile?.name?.startsWith("img_", true) == true


fun Long.formatSize(): String {
    if (this <= 0) {
        return "0 B"
    }

    val units = arrayOf("B", "kB", "MB", "GB", "TB")
    val digitGroups = (log10(toDouble()) / log10(1024.0)).toInt()
    return "${
        DecimalFormat("#,##0.#").format(
            this / Math.pow(
                1024.0, digitGroups.toDouble()
            )
        )
    } ${units[digitGroups]}"
}

fun formatDuration(durationInMillis: Long): String {
    val hours = (durationInMillis / (1000 * 60 * 60)) % 24
    val minutes = (durationInMillis / (1000 * 60)) % 60
    val seconds = (durationInMillis / 1000) % 60

    val pattern = if (hours > 0) "HH:mm:ss" else "mm:ss"
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())

    return formatter.format(durationInMillis)
}

fun <N : Number> N.getFormattedDuration(forceShowHours: Boolean = false): String {
    val sb = StringBuilder(8)
    val value = this.toLong()

    val hours = value / 3600
    val minutes = value % 3600 / 60
    val seconds = value % 60

    if (value >= 3600) {
        sb.append(String.format(Locale.getDefault(), "%02d", hours)).append(":")
    } else if (forceShowHours) {
        sb.append("0:")
    }

    sb.append(String.format(Locale.getDefault(), "%02d", minutes))
    sb.append(":").append(String.format(Locale.getDefault(), "%02d", seconds))
    return sb.toString()
}

const val TYPE_ALL = -1
const val TYPE_IMAGES = 1
const val TYPE_VIDEOS = 2
const val TYPE_GIFS = 4
const val TYPE_RAWS = 8
const val TYPE_SVGS = 16
const val TYPE_PORTRAITS = 32

fun getDefaultFileFilter() = TYPE_IMAGES or TYPE_VIDEOS or TYPE_GIFS or TYPE_RAWS or TYPE_SVGS

fun Context.showToast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, length).show()
}

fun View.beVisibleIf(bool: Boolean) {
    visibility = if (bool) View.VISIBLE else View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}

fun View.vibrate() = reallyPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
fun View.vibrateStrong() = reallyPerformHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

@Suppress("DEPRECATION")
private fun View.reallyPerformHapticFeedback(feedbackConstant: Int) {
    if (context.isTouchExplorationEnabled()) {
        // Don't mess with a blind person's vibrations
        return
    }
    // Either this needs to be set to true, or android:hapticFeedbackEnabled="true" needs to be set in XML
    isHapticFeedbackEnabled = true

    /**
     * [HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING]
     * is deprecated in Android 13+
     */
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        performHapticFeedback(feedbackConstant)
    } else {
        // Most of the constants are off by default: for example, clicking on a button doesn't cause the phone to vibrate anymore
        // if we still want to access this vibration, we'll have to ignore the global settings on that.
        performHapticFeedback(feedbackConstant, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
    }
}

private fun Context.isTouchExplorationEnabled(): Boolean {
    // can be null during unit tests
    val accessibilityManager =
        getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager?
    return accessibilityManager?.isTouchExplorationEnabled ?: false
}

fun String.getParentPath() = removeSuffix("/${getFilenameFromPath()}")

fun String.getFilenameFromPath() = substring(lastIndexOf("/") + 1)

fun String.getFilenameExtension() = substring(lastIndexOf(".") + 1)

fun Activity.toggleOrientation() {
    requestedOrientation =
        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED ||
            requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        ) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
}

val Context.windowManager: WindowManager get() = getSystemService(Context.WINDOW_SERVICE) as WindowManager

val Context.portrait get() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
val Context.navigationBarOnSide: Boolean get() = usableScreenSize.x < realScreenSize.x && usableScreenSize.x > usableScreenSize.y
val Context.navigationBarOnBottom: Boolean get() = usableScreenSize.y < realScreenSize.y
val Context.navigationBarHeight: Int get() = if (navigationBarOnBottom && navigationBarSize.y != usableScreenSize.y) navigationBarSize.y else 0
val Context.navigationBarWidth: Int get() = if (navigationBarOnSide) navigationBarSize.x else 0

val Context.navigationBarSize: Point
    get() = when {
        navigationBarOnSide -> Point(newNavigationBarHeight, usableScreenSize.y)
        navigationBarOnBottom -> Point(usableScreenSize.x, newNavigationBarHeight)
        else -> Point()
    }

val Context.newNavigationBarHeight: Int
    get() {
        var navigationBarHeight = 0
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            navigationBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        return navigationBarHeight
    }

val Context.usableScreenSize: Point
    get() {
        val size = Point()
        windowManager.defaultDisplay.getSize(size)
        return size
    }

val Context.realScreenSize: Point
    get() {
        val size = Point()
        windowManager.defaultDisplay.getRealSize(size)
        return size
    }

fun Activity.hasNavBar(): Boolean {
    val display = windowManager.defaultDisplay

    val realDisplayMetrics = DisplayMetrics()
    display.getRealMetrics(realDisplayMetrics)

    val displayMetrics = DisplayMetrics()
    display.getMetrics(displayMetrics)

    return (realDisplayMetrics.widthPixels - displayMetrics.widthPixels > 0) || (realDisplayMetrics.heightPixels - displayMetrics.heightPixels > 0)
}

fun isQMinus() = Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N)
fun isNougatPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N_MR1)
fun isNougatMR1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O_MR1)
fun isOreoMr1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
fun isPiePlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
fun isQPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
fun isRPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun isSPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
fun isTiramisuPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
fun isUDCakePlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE

val storagePermissions: ArrayList<String> = if (isUDCakePlus()) {
    arrayListOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO,
        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
    )
} else if (isTiramisuPlus()) {
    arrayListOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO
    )
} else if (isRPlus()) {
    arrayListOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
} else {
    arrayListOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
}

fun Context.hasPermissions(arrayList: ArrayList<String>) = arrayList.all {
    ContextCompat.checkSelfPermission(
        this,
        it
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.isOnline(): Boolean {
    val cm = getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
    val net = cm.activeNetwork ?: return false
    val actNw = cm.getNetworkCapabilities(net) ?: return false
    return when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        //for other device how are able to connect with Ethernet
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        //for check internet over Bluetooth
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
        else -> false
    }
}