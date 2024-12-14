package com.dev4life.imagelabeling.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PhotoAlbum
import androidx.compose.material.icons.outlined.Webhook
import androidx.compose.material.icons.outlined.Yard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dev4life.imagelabeling.R
import com.dev4life.imagelabeling.data.NavigationItem
import com.dev4life.imagelabeling.utils.Screen

@Composable
fun rememberNavigationItems(): List<NavigationItem> {
    val timelineTitle = stringResource(R.string.nav_timeline)
    val albumsTitle = stringResource(R.string.nav_albums)
    val labelsTitle = stringResource(R.string.nav_labels)
    return remember {
        listOf(
            NavigationItem(
                name = timelineTitle,
                route = Screen.TimelineScreen.route,
                icon = Icons.Outlined.Photo,
            ),
            NavigationItem(
                name = albumsTitle,
                route = Screen.AlbumsScreen.route,
                icon = Icons.Outlined.PhotoAlbum,
            ),
            NavigationItem(
                name = labelsTitle,
                route = Screen.LabelsScreen.route,
                icon = Icons.Outlined.Yard,
            )
        )
    }
}

@Preview
@Composable
fun NavBarPreview() {

    val navController = rememberNavController()

    val backStackEntry = navController.currentBackStackEntryAsState()
    val bottomNavItems = rememberNavigationItems()

    Box {
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            visible = true,
            enter = slideInVertically { it * 2 },
            exit = slideOutVertically { it * 2 },
            content = {
                val modifier = remember {
                    Modifier.fillMaxWidth()
                }
                GalleryNavBar(
                    modifier = modifier,
                    backStackEntry = backStackEntry,
                    navigationItems = bottomNavItems,
                    onClick = { navigate(navController, it) }
                )
            }
        )
    }
}

@Composable
fun AppBarContainer(
    windowSizeClass: WindowSizeClass,
    navController: NavController,
    bottomBarState: MutableState<Boolean>,
    paddingValues: PaddingValues,
    isScrolling: MutableState<Boolean>,
    onCurateClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val bottomNavItems = rememberNavigationItems()
    val useNavRail = windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact
    val showNavigation by bottomBarState

    Box {
        content.invoke()
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = paddingValues.calculateBottomPadding()),
            visible = showNavigation && !isScrolling.value,
            enter = slideInVertically { it * 2 },
            exit = slideOutVertically { it * 2 },
            content = {
                val modifier = remember(useNavRail) {
                    if (useNavRail) Modifier.requiredWidth((110 * bottomNavItems.size).dp)
                    else Modifier.fillMaxWidth()
                }
                GalleryNavBar(
                    modifier = modifier,
                    backStackEntry = backStackEntry,
                    navigationItems = bottomNavItems,
                    onClick = { navigate(navController, it) },
                    onCurateClick = {
                        onCurateClick()
                    }
                )
            }
        )
    }
}

private fun navigate(navController: NavController, route: String) {
    navController.navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}

@Composable
fun GalleryNavBar(
    modifier: Modifier,
    backStackEntry: State<NavBackStackEntry?>,
    navigationItems: List<NavigationItem>,
    onClick: (route: String) -> Unit,
    onCurateClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .then(modifier)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .then(modifier)
                .height(64.dp)
                .weight(1f)
                .background(
                    color = colorScheme.surfaceColorAtElevation(2.dp),
                    shape = RoundedCornerShape(percent = 100)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            navigationItems.forEach { item ->
                val selected = item.route == backStackEntry.value?.destination?.route
                GalleryNavBarItem(
                    navItem = item,
                    isSelected = selected,
                    onClick = onClick
                )
            }
        }

        Box(
            modifier = Modifier
                .padding(start = 24.dp)
                .height(64.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {}
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = colorScheme.surfaceColorAtElevation(2.dp),
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .clickable { onCurateClick() },
            )
            Icon(
                modifier = Modifier
                    .size(32.dp),
                imageVector = Icons.Outlined.Webhook,
                contentDescription = "Curate",
                tint = colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun RowScope.GalleryNavBarItem(
    navItem: NavigationItem,
    isSelected: Boolean,
    onClick: (route: String) -> Unit,
) {
    val mutableInteraction = remember { MutableInteractionSource() }
    val selectedColor by animateColorAsState(
        targetValue = if (isSelected) colorScheme.secondaryContainer else Color.Transparent,
        label = "selectedColor"
    )
    val selectedIconColor by animateColorAsState(
        targetValue = if (isSelected) colorScheme.onSecondaryContainer else colorScheme.onSurfaceVariant,
        label = "selectedIconColor"
    )
    Box(
        modifier = Modifier
            .height(64.dp)
            .weight(1f)
            // Dummy clickable to intercept clicks from passing under the container
            .clickable(
                indication = null,
                interactionSource = mutableInteraction,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .height(32.dp)
                .width(64.dp)
                .background(
                    color = selectedColor,
                    shape = RoundedCornerShape(percent = 100)
                )
                .clip(RoundedCornerShape(100))
                .clickable { if (!isSelected) onClick(navItem.route) },
        )
        Icon(
            modifier = Modifier
                .size(24.dp),
            imageVector = navItem.icon,
            contentDescription = navItem.name,
            tint = selectedIconColor
        )
    }
}