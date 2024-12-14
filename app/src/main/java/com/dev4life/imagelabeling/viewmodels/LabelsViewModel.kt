package com.dev4life.imagelabeling.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev4life.imagelabeling.R
import com.dev4life.imagelabeling.data.labels.LabelItem
import com.dev4life.imagelabeling.data.media.MediaItem
import com.dev4life.imagelabeling.data.repo.LabelsRepo
import com.dev4life.imagelabeling.data.repo.MediaRepo
import com.dev4life.imagelabeling.states.CurateLabelsState
import com.dev4life.imagelabeling.states.CurationState
import com.dev4life.imagelabeling.states.GroupedLabelItem
import com.dev4life.imagelabeling.states.LabelProcessState
import com.dev4life.imagelabeling.states.LabelState
import com.dev4life.imagelabeling.states.LabelsGroupState
import com.dev4life.imagelabeling.utils.LabelViewItem
import com.dev4life.imagelabeling.utils.RepeatOnResume
import com.dev4life.imagelabeling.utils.Screen
import com.dev4life.imagelabeling.utils.dateHeader
import com.dev4life.imagelabeling.utils.getDate
import com.dev4life.imagelabeling.utils.isOnline
import com.dev4life.imagelabeling.utils.pathToBitmap
import com.dev4life.imagelabeling.utils.scaleBitmapTo
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.tflite.java.TfLite
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@HiltViewModel
class LabelsViewModel @Inject constructor(
    private val context: Context,
    private val labelsRepo: LabelsRepo,
    private val mediaRepo: MediaRepo
) : ViewModel() {
    private val _moduleState = MutableStateFlow(CurateLabelsState())
    val moduleState = _moduleState.asStateFlow()

    private var curatingJob: Job? = null

    var labelName: String = ""

    val labelsState = labelsRepo.getAllLabels()
        .flowOn(Dispatchers.IO)
        .map { result ->
            val data = result.takeIf { it.isNotEmpty() } ?: emptyList()
            var groupedData: SnapshotStateList<GroupedLabelItem>? = mutableStateListOf()

            data.groupBy {
                it.labelName
            }.let { labels ->
                labels.forEach { (name, list) ->
                    groupedData?.add(GroupedLabelItem(name, list))
                }
            }

            val state = LabelState(
                labels = groupedData ?: mutableStateListOf(),
                isLoading = false
            )
            groupedData = null

            state
        }.flowOn(Dispatchers.IO).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LabelState()
        )

    val labelsGroupedState = mediaRepo.getPhotosWithLabels()
        .flowOn(Dispatchers.IO)
        .map { result ->
            result.filter { photoWithLabels ->
                photoWithLabels.labels.any { label -> label.labelName == labelName }
            }
        }.flowOn(Dispatchers.IO).map { result ->
            var mappedData: MutableList<LabelViewItem>? = mutableListOf()
            result.groupBy {
                it.photo.dateAdded.getDate(
                    stringToday = "Today",
                    stringYesterday = "Yesterday"
                )
            }.forEach { (date, data) ->
                val dateHeader = LabelViewItem.Header("header_$date", date, listOf())
                val groupedMedia = data.map {
                    LabelViewItem.LabelViewItem1.Loaded(
                        "media_${it.photo.id}_${it.labels.size}",
                        it.photo
                    )
                }
                mappedData?.add(dateHeader)
                mappedData?.addAll(groupedMedia)
            }

            val state = LabelsGroupState(
                isLoading = false,
                error = "",
                media = result.map { it.photo },
                mappedMedia = mappedData ?: mutableListOf(),
                dateHeader = result.map { it.photo }.dateHeader(-1L)
            )

            mappedData = null
            state
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LabelsGroupState()
        )

    fun onLabelClick(navigate: (String) -> Unit): (LabelItem) -> Unit = { label ->
        navigate(Screen.LabelViewScreen.route + "?labelName=${label.labelName}")
    }

    private val _labelProcessState = MutableStateFlow(LabelProcessState())
    val labelProcessState = _labelProcessState.asStateFlow()

    private var registerCallback: (List<LabelItem>) -> Unit = {}
    private var registerMediaCallback: (MediaItem) -> Unit = {}

    private val updateMediaFlow = callbackFlow {
        registerMediaCallback = { data ->
            trySend(data)
        }
        awaitClose()
    }

    private val insertFlow = callbackFlow {
        registerCallback = { data ->
            trySend(data)
        }
        awaitClose()
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            insertFlow.collectLatest { labelsList ->
                labelsRepo.insertLabels(labelsList)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            updateMediaFlow.collectLatest { media ->
                mediaRepo.updateMedia(media)
            }
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun attachToLifecycle() {
        RepeatOnResume {
        }
    }

    fun checkAndCurate() {
        val moduleInstallClient = ModuleInstall.getClient(context)
        val optionalModuleApi = TfLite.getClient(context)

        moduleInstallClient
            .areModulesAvailable(optionalModuleApi)
            .addOnSuccessListener { modulesAvailable ->
                _moduleState.value =
                    if (modulesAvailable.areModulesAvailable()) {
                        CurateLabelsState(CurationState.ModulesAvailable)
                    } else {
                        if (context.isOnline()) {
                            CurateLabelsState(CurationState.ModulesNotAvailable)
                        } else {
                            CurateLabelsState(CurationState.Offline)
                        }
                    }
            }
            .addOnFailureListener {
                _moduleState.value = CurateLabelsState(CurationState.Error)
            }
    }

    @OptIn(InternalCoroutinesApi::class)
    fun startCuratingPhotos() {
        val labeler by lazy {
            ImageLabeling.getClient(
                ImageLabelerOptions.Builder().setConfidenceThreshold(0.80f).build()
            )
        }
        val progress = AtomicInteger(0)

        curatingJob = viewModelScope.launch(Dispatchers.IO) {
            _labelProcessState.value = LabelProcessState(isCurating = true)

            val fetchLabelsTask = async {
                val unlabeledPhotos = mediaRepo.getUnlabeledMedia()

                var image: InputImage? = null
                for (media in unlabeledPhotos) {
                    ensureActive()

                    var bitmap: Bitmap? = null

                    try {
                        val opt = BitmapFactory.Options().apply {
                            inJustDecodeBounds = true
                        }
                        BitmapFactory.decodeFile(media.path, opt)
                        if (opt.outWidth >= 480 && opt.outHeight >= 480) {
                            bitmap = media.path.pathToBitmap().scaleBitmapTo(480, 480)
                            image = InputImage.fromBitmap(
                                bitmap, 0
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    try {
                        image?.let { img ->
                            val labelsList = processImage(labeler, img, media)

                            registerCallback.invoke(labelsList)
                            registerMediaCallback.invoke(media.apply {
                                isCurated = true
                            })

                            progress.getAndIncrement()
//                          bitmap?.recycle()
                            bitmap = null
                            _labelProcessState.value =
                                LabelProcessState(
                                    counts = progress.get(),
                                    total = unlabeledPhotos.size,
                                    isCurating = true
                                )
                        }
                    } catch (e: Exception) {
                        Log.e(
                            "TAG",
                            "curatingException: ${context.getString(R.string.something_went_wrong_try_again)}",
                        )
                    }
                }
            }
            fetchLabelsTask.await()
            _labelProcessState.value = LabelProcessState(isCurating = false)
        }

        curatingJob?.invokeOnCompletion(onCancelling = true) {
            _labelProcessState.value = LabelProcessState(isCurating = false)
        }
    }

    private suspend fun processImage(
        labeler: ImageLabeler,
        image: InputImage,
        media: MediaItem
    ): List<LabelItem> =
        suspendCancellableCoroutine { continuation ->
            labeler.process(image).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val labelsList = ArrayList(task.result.map {
                        LabelItem(
                            labelName = it.text,
                            labelCreatorId = media.id,
                            photoPath = media.path
                        )
                    })
                    continuation.resume(labelsList)
                } else {
                    continuation.resumeWithException(task.exception!!)
                }
            }

            continuation.invokeOnCancellation {
            }
        }

    fun stopCuratingPhotos() {
        curatingJob?.cancel()
    }
}