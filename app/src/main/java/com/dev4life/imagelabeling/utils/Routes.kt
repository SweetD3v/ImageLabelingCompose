package com.dev4life.imagelabeling.utils

sealed class Screen(val route: String) {
    object TimelineScreen : Screen("timeline_screen")
    object AlbumsScreen : Screen("albums_screen")

    object LabelsScreen : Screen("labels_screen")
    object LabelViewScreen : Screen("label_view_screen")

    operator fun invoke() = route
}
