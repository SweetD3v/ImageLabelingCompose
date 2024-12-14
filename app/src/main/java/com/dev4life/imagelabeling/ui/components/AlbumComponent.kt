package com.dev4life.imagelabeling.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.dev4life.imagelabeling.R
import com.dev4life.imagelabeling.data.albums.Album
import com.dev4life.imagelabeling.utils.vibrate
import java.io.File

@Composable
fun AlbumComponent(
    album: Album,
    onItemClick: ((Album) -> Unit)? = null
) {
    val showDropDown = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.padding(horizontal = 8.dp),
    ) {
        AlbumImage(album = album, onItemClick) {
            showDropDown.value = !showDropDown.value
        }
        Text(
            text = album.label,
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp
        )
        Text(
            text = pluralStringResource(
                id = R.plurals.item_count,
                count = album.count.toInt(),
                album.count
            ),
            modifier = Modifier
                .padding(top = 2.dp, bottom = 16.dp)
                .padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelMedium,
            fontSize = 12.sp
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun AlbumImage(
    album: Album,
    onItemClick: ((Album) -> Unit)? = null,
    onItemLongClick: ((Album) -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState()
    val radius = if (isPressed.value) 32.dp else 16.dp
    val cornerRadius by animateDpAsState(targetValue = radius, label = "cornerRadius")
    val view = LocalView.current
    GlideImage(
        modifier = Modifier
            .aspectRatio(1f)
            .size(150.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(cornerRadius)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onItemClick?.invoke(album) },
                onLongClick = {
                    view.vibrate()
                    onItemLongClick?.invoke(album)
                }
            ),
        model = File(album.pathToThumbnail),
        contentDescription = album.label,
        contentScale = ContentScale.Crop,
    )
}