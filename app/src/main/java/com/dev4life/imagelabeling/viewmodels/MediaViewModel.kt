package com.dev4life.imagelabeling.viewmodels

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.asIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev4life.imagelabeling.GalleryApp
import com.dev4life.imagelabeling.data.media.MediaItem
import com.dev4life.imagelabeling.data.repo.AlbumsRepo
import com.dev4life.imagelabeling.data.repo.MediaRepo
import com.dev4life.imagelabeling.states.MediaState
import com.dev4life.imagelabeling.states.PermissionEvent
import com.dev4life.imagelabeling.utils.MediaViewItem
import com.dev4life.imagelabeling.utils.PrefsManager
import com.dev4life.imagelabeling.utils.RepeatOnResume
import com.dev4life.imagelabeling.utils.dateHeader
import com.dev4life.imagelabeling.utils.getDate
import com.dev4life.imagelabeling.utils.hasPermissions
import com.dev4life.imagelabeling.utils.isQPlus
import com.dev4life.imagelabeling.utils.storagePermissions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val mediaRepo: MediaRepo,
    private val albumRepo: AlbumsRepo
) : ViewModel() {

    private val app: GalleryApp by lazy { GalleryApp.getInstance() }
    private val prefs by lazy { PrefsManager.newInstance(app.applicationContext) }
    private val lastFetchTime: Long
        get() = prefs.getLong("lastFetchTime", -1L)

    private val _permissionEvents: MutableStateFlow<PermissionEvent> =
        MutableStateFlow(PermissionEvent.Idle)
    val permissionEvents get() = _permissionEvents.asStateFlow()

    private val _askCounts = mutableIntStateOf(0)
    val askCounts: State<Int> = _askCounts.asIntState()

    private var loadMediaJob: Job? = null
    private var updateMediaJob: Job? = null

    private val _mediaState = MutableStateFlow(MediaState())
    val mediaState = _mediaState.asStateFlow()

    init {
        loadAllMedia()
        manageNewMedia()
    }

    fun incrementAskCounts() {
        _askCounts.intValue += 1
    }

    fun getAskCounts() = _askCounts.intValue

    fun resetAskCounts() {
        _askCounts.intValue = 0
    }

    private fun loadAllMedia() {
        loadMediaJob?.cancel()
        loadMediaJob = viewModelScope.launch {
            mediaRepo.getAllMedia().collectLatest { data ->
                val error = ""

                var mappedData: ArrayList<MediaViewItem>? = ArrayList()
                data.groupBy {
                    it.dateAdded.getDate(
                        stringToday = "Today",
                        stringYesterday = "Yesterday"
                    )
                }.forEach { (date, data) ->
                    val dateHeader = MediaViewItem.Header("header_$date", date, data)
                    val groupedMedia = data.map {
                        MediaViewItem.MediaViewItem1.Loaded("media_${it.id}_${it.label}", it)
                    }
                    mappedData?.add(dateHeader)
                    mappedData?.addAll(groupedMedia)
                }

                _mediaState.value = MediaState(
                    isLoading = false,
                    error = error,
                    media = data,
                    mappedMedia = mappedData!!,
                    dateHeader = data.dateHeader(-1L)
                )

                mappedData = null
            }
        }
    }

    private fun manageNewMedia() {
        updateMediaJob?.cancel()
        updateMediaJob = viewModelScope.launch {
            mediaRepo.getAllMedia().collect { list ->
                manageUris(
                    list,
                    getAllUris()
                )
            }
        }
    }

    fun permissionGranted() {
        viewModelScope.launch(Dispatchers.IO) {
            val mediaTask = async {
                loadImages()
            }

            val albumsTask = async {
                loadAlbums()
            }

            awaitAll(mediaTask, albumsTask)
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun attachToLifecycle() {
        RepeatOnResume {
            // TODO: Uncomment this code if you need to refresh the media in onResume of the activity
//            viewModelScope.launch(Dispatchers.IO) {
//                loadImages()
//            }
        }
    }

    private suspend fun loadImages() {
        if (!app.applicationContext.hasPermissions(storagePermissions))
            return

        val images = mutableListOf<MediaItem>()

        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DURATION,
            MediaStore.MediaColumns.ORIENTATION,
            MediaStore.MediaColumns.MIME_TYPE
        )

        var selection: String = ("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO + ")"
                )

        var selectionArgs: Array<String>? = null
        val sortOrder = MediaStore.MediaColumns.DATE_MODIFIED + " DESC"

        if (lastFetchTime != -1L) {
            selection += " AND " + MediaStore.MediaColumns.DATE_MODIFIED + " > ?"
            selectionArgs = arrayOf((lastFetchTime / 1000L).toString())
        }

        val query = app.applicationContext.contentResolver.query(
            MediaStore.Files.getContentUri(
                if (isQPlus())
                    MediaStore.VOLUME_EXTERNAL
                else "external"
            ),
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        query?.use { cursor ->
            while (cursor.moveToNext()) {
                val media_id =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                val displayName =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                val bDisplayName =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME))
                val size =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))
                val path =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
                val dateAdded =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED))
                val bucket_id =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID))
                val duration =
                    try {
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION)) / 1000L
                    } catch (e: Exception) {
                        0L
                    }
                val orientation: Int =
                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.ORIENTATION))

                val mimeType: String =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE))
                val contentUri = if (mimeType.contains("image"))
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                else
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                val uri = ContentUris.withAppendedId(contentUri, media_id)

                images.add(
                    MediaItem(
                        mediaId = media_id,
                        label = displayName,
                        bucketId = bucket_id,
                        albumName = bDisplayName ?: "${Build.MANUFACTURER} ${Build.MODEL}",
                        path = path,
                        size = size,
                        dateAdded = dateAdded,
                        duration = duration,
                        orientation = orientation,
                        uri = uri,
                        mimeType = mimeType
                    )
                )
            }
        }

        prefs.putLong("lastFetchTime", System.currentTimeMillis())
        mediaRepo.insertAllMedia(images)
    }

    private suspend fun loadAlbums() {
        if (!app.applicationContext.hasPermissions(storagePermissions))
            return

        val albums = albumRepo.loadAlbums()
        albumRepo.insertAlbums(albums)
    }

    private fun getAllUris(): List<MediaItem> {
        val images = mutableListOf<MediaItem>()
        if (!app.applicationContext.hasPermissions(storagePermissions))
            return emptyList()

        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DURATION,
            MediaStore.MediaColumns.ORIENTATION,
            MediaStore.MediaColumns.MIME_TYPE
        )

        val selection: String = ("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO + ")")

        val query = app.applicationContext.contentResolver.query(
            MediaStore.Files.getContentUri(
                if (isQPlus())
                    MediaStore.VOLUME_EXTERNAL
                else "external"
            ),
            projection,
            selection,
            null,
            null
        )

        query?.use { cursor ->
            while (cursor.moveToNext()) {
                val media_id =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                val displayName =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                val bDisplayName =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME))
                val size =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))
                val path =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
                val dateAdded =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED))
                val bucket_id =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID))
                val orientation: Int =
                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.ORIENTATION))
                val duration =
                    try {
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION)) / 1000L
                    } catch (e: Exception) {
                        0L
                    }

                val mimeType: String =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE))
                val contentUri = if (mimeType.contains("image"))
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                else
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                val uri = ContentUris.withAppendedId(contentUri, media_id)

                images.add(
                    MediaItem(
                        mediaId = media_id,
                        bucketId = bucket_id,
                        label = displayName,
                        albumName = bDisplayName ?: "${Build.MANUFACTURER} ${Build.MODEL}",
                        path = path,
                        size = size,
                        dateAdded = dateAdded,
                        duration = duration,
                        orientation = orientation,
                        uri = uri,
                        mimeType = mimeType
                    )
                )
            }
        }
        return images
    }

    private suspend fun manageUris(
        oldList: List<MediaItem>,
        urisList: List<MediaItem>,
    ) {
        val oldIdsList = oldList.map { it.mediaId }.toSet()
        val newIdsList = urisList.map { it.mediaId }.toSet()

        val newRecordsList = newIdsList - oldIdsList
        val deleteUriList = oldIdsList - newIdsList

        if (newRecordsList.isNotEmpty()) {
            val list = urisList.filter { newRecordsList.contains(it.mediaId) }
            mediaRepo.insertAllMedia(list)
        }

        if (deleteUriList.isNotEmpty()) {
            val mediaIds = oldList.filter { oldIdsList.contains(it.mediaId) }.map { it.mediaId }
            val batchSize = 500
            for (i in mediaIds.indices step batchSize) {
                val batch = mediaIds.subList(i, minOf(i + batchSize, mediaIds.size))
                mediaRepo.deleteMediaByIds(batch)
            }
        }
    }

    private suspend fun showPermissionsRationale() {
        _permissionEvents.emit(PermissionEvent.ShowRationale)
    }

    fun requestPermissions(activity: Activity) {
        viewModelScope.launch {
            val shouldShowRationale =
                storagePermissions.any {
                    activity.shouldShowRequestPermissionRationale(it)
                }
            when {
                !activity.hasPermissions(storagePermissions) && !shouldShowRationale && askCounts.value < 2 -> {
                    _permissionEvents.emit(PermissionEvent.RequestPermissions)
                }

                shouldShowRationale && askCounts.value < 2 -> {
                    _permissionEvents.emit(PermissionEvent.RequestPermissions)
                }

                else -> {
                    val grantedPermissions = storagePermissions.filter {
                        activity.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
                    }
                    val deniedPermissions = storagePermissions.filter {
                        activity.checkSelfPermission(it) == PackageManager.PERMISSION_DENIED
                    }
                    if (!shouldShowRationale) showPermissionsRationale()
                    else _permissionEvents.emit(
                        PermissionEvent.PermissionResult(
                            grantedPermissions,
                            deniedPermissions
                        )
                    )
                }
            }
        }
    }

    fun onPermissionResult(grantedPermissions: List<String>, deniedPermissions: List<String>) {
        viewModelScope.launch {
            _permissionEvents.emit(
                PermissionEvent.PermissionResult(
                    grantedPermissions,
                    deniedPermissions
                )
            )
        }
    }
}