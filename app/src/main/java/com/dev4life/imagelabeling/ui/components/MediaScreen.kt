package com.dev4life.imagelabeling.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.rememberGlidePreloadingData
import com.dev4life.imagelabeling.R
import com.dev4life.imagelabeling.data.media.MediaItem
import com.dev4life.imagelabeling.states.MediaState
import com.dev4life.imagelabeling.utils.MediaKey
import com.dev4life.imagelabeling.utils.MediaViewItem
import com.dev4life.imagelabeling.utils.isBigHeaderKey
import com.dev4life.imagelabeling.utils.isHeaderKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive

@Composable
fun MediaScreen(
    paddingValues: PaddingValues,
    mediaState: StateFlow<MediaState>,
) {
    TimeLineView(
        paddingValuesTop = paddingValues.calculateTopPadding(),
        state = mediaState,
    )
}

@Composable
fun TimeLineView(
    paddingValuesTop: Dp = 0.dp,
    state: StateFlow<MediaState>,
) {

    val mediaState by state.collectAsStateWithLifecycle()

    val gridState = rememberLazyGridState()
    var level by remember { mutableIntStateOf(3) }

    AnimatedVisibility(
        visible = level == 0,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut(

        ) + fadeOut(
            animationSpec = keyframes {
                durationMillis = 200
                0.8f at 20
                0.6f at 70
                0.2f at 80
            }
        )
    ) {
        PhotoGrid(
            paddingValuesTop,
            mediaState,
            gridState,
            1,
            1,
            0
        ) {
            level = it
        }
    }
    AnimatedVisibility(
        visible = level == 1,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut(

        ) + fadeOut(
            animationSpec = keyframes {
                durationMillis = 200
                0.8f at 20
                0.6f at 70
                0.2f at 80
            }
        )
    ) {
        PhotoGrid(
            paddingValuesTop,
            mediaState,
            gridState,
            2,
            2,
            0
        ) {
            level = it
        }
    }
    AnimatedVisibility(
        visible = level == 2,
        enter = scaleIn(
            initialScale = 0.75f
        ) + fadeIn(
            initialAlpha = 0.1f
        ),
        exit = scaleOut(
            targetScale = 0.75f
        ) + fadeOut(
            animationSpec = keyframes {
                durationMillis = 200
                0.8f at 20
                0.6f at 70
                0.2f at 80
            }
        )
    ) {
        PhotoGrid(
            paddingValuesTop,
            mediaState,
            gridState,
            3,
            3,
            1
        ) {
            level = it
        }
    }

    AnimatedVisibility(
        visible = level == 3,
        enter = scaleIn(
            initialScale = 0.75f
        ) + fadeIn(
            initialAlpha = 0.1f
        ),
        exit = scaleOut(
            targetScale = 0.75f
        ) + fadeOut(
            animationSpec = keyframes {
                durationMillis = 200
                0.8f at 20
                0.6f at 70
                0.2f at 80
            }
        )
    ) {
        PhotoGrid(
            paddingValuesTop,
            mediaState,
            gridState,
            4,
            3,
            2
        ) {
            level = it
        }
    }
}

@Composable
private fun PhotoGrid(
    paddingValuesTop: Dp = 0.dp,
    mediaState: MediaState,
    state: LazyGridState,
    gridCounts: Int,
    nextLevel: Int,
    previousLevel: Int,
    onZoomLevelChange: (Int) -> Unit
) {

    val displayMode = "displayMode"
    var zoom by remember(key1 = displayMode) { mutableFloatStateOf(1f) }
    val zoomTransition: Float by animateFloatAsState(
        zoom,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "zoom"
    )

    val stringToday = stringResource(id = R.string.header_today)
    val stringYesterday = stringResource(id = R.string.header_yesterday)

    val mappedData = mediaState.mappedMedia

    val preloadingData = rememberGlidePreloadingData(
        data = mediaState.media,
        preloadImageSize = Size(48f, 48f)
    ) { media: MediaItem, requestBuilder: RequestBuilder<Drawable> ->
        requestBuilder
            .signature(
                MediaKey(
                    media.id, media.dateAdded, media.mimeType, media.orientation
                )
            )
            .load(media.uri)
    }

    val autoScrollSpeed = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(autoScrollSpeed.floatValue) {
        if (autoScrollSpeed.floatValue != 0f) {
            while (isActive) {
                state.scrollBy(autoScrollSpeed.floatValue)
                delay(10)
            }
        }
    }

    LazyVerticalGrid(
        state = state,
        columns = GridCells.Fixed(count = gridCounts),
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        contentPadding = PaddingValues(3.dp),
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectPinchGestures(
                    pass = PointerEventPass.Initial,
                    onGesture = { centroid: Offset, newZoom: Float ->
                        val newScale = (zoom * newZoom)
                        if (newScale > 1.25f) {
                            onZoomLevelChange.invoke(previousLevel)
                        } else if (newScale < 0.75f) {
                            onZoomLevelChange.invoke(nextLevel)
                        } else {
                            zoom = newScale
                        }
                    },
                    onGestureEnd = { zoom = 1f }
                )
            }
            .graphicsLayer {
                scaleX = zoomTransition
                scaleY = zoomTransition
            }
    ) {
        item {
            Spacer(Modifier.size(paddingValuesTop))
        }

        items(
            items = mappedData,
            key = { if (it is MediaViewItem.MediaViewItem1) mediaState.media.indexOf(it.media) else it.key },
            contentType = { !it.key.startsWith("header_") },
            span = { item ->
                GridItemSpan(if (item.key.isHeaderKey) maxLineSpan else 1)
            }) { item ->

            when (item) {
                is MediaViewItem.Header -> {
                    val title = item.text
                        .replace("Today", stringToday)
                        .replace("Yesterday", stringYesterday)

                    StickyHeader(
                        date = title,
                        showAsBig = item.key.isBigHeaderKey,
                    )
                }

                is MediaViewItem.MediaViewItem1 -> {
                    val photo = item.media
                    val mediaIndex = mediaState.media.indexOf(photo).coerceAtLeast(0)

                    val (media, preloadRequestBuilder) = preloadingData[mediaIndex]

                    ImageItem(
                        photo = media,
                        preloadRequestBuilder = preloadRequestBuilder
                    )
                }
            }
        }
    }
}