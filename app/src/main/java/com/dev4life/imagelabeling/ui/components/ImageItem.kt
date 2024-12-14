package com.dev4life.imagelabeling.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DecodeFormat
import com.dev4life.imagelabeling.data.media.MediaItem
import com.dev4life.imagelabeling.utils.MediaKey

/**
 * A square image that can be shown in a grid.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageItem(
    photo: MediaItem,
    preloadRequestBuilder: RequestBuilder<Drawable>,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier = modifier.aspectRatio(1f),
        tonalElevation = 3.dp
    ) {
        Box {
            GlideImage(
                modifier = Modifier
                    .matchParentSize(),
                model = photo.uri,
                contentDescription = photo.label,
                contentScale = ContentScale.Crop,
            ) {
                it.thumbnail(preloadRequestBuilder)
                    .signature(
                        MediaKey(
                            photo.id,
                            photo.dateAdded,
                            photo.mimeType,
                            photo.orientation
                        )
                    )
                    .format(DecodeFormat.PREFER_RGB_565)
                    .override(270)
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun LabelImageItem(
    photo: MediaItem,
    preloadRequestBuilder: RequestBuilder<Drawable>,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier = modifier.aspectRatio(1f),
        tonalElevation = 3.dp
    ) {
        Box {
            GlideImage(
                modifier = Modifier
                    .matchParentSize(),
                model = photo.uri,
                contentDescription = photo.label,
                contentScale = ContentScale.Crop,
            ) {
                it.thumbnail(preloadRequestBuilder)
                    .signature(
                        MediaKey(
                            photo.id,
                            photo.dateAdded,
                            photo.mimeType,
                            photo.orientation
                        )
                    )
                    .format(DecodeFormat.PREFER_RGB_565)
                    .override(270)
            }
        }
    }
}