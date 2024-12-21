package com.dev4life.imagelabeling.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.dev4life.imagelabeling.R
import com.dev4life.imagelabeling.utils.Animation.navigateInAnimation
import com.dev4life.imagelabeling.utils.Animation.navigateUpAnimation
import com.dev4life.imagelabeling.utils.Screen
import com.dev4life.imagelabeling.viewmodels.AlbumsViewModel
import com.dev4life.imagelabeling.viewmodels.ChanneledViewModel
import com.dev4life.imagelabeling.viewmodels.LabelsViewModel
import com.dev4life.imagelabeling.viewmodels.MediaViewModel

@Composable
fun NavigationComp(
    navController: NavHostController,
    paddingValues: PaddingValues,
    bottomBarState: MutableState<Boolean>,
    systemBarFollowThemeState: MutableState<Boolean>
) {
    val bottomNavEntries = rememberNavigationItems()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    navBackStackEntry?.destination?.route?.let {
        val shouldDisplayBottomBar = bottomNavEntries.find { item -> item.route == it } != null
        bottomBarState.value = shouldDisplayBottomBar
        systemBarFollowThemeState.value = !it.contains(Screen.LabelViewScreen.route)
    }
    val navPipe = hiltViewModel<ChanneledViewModel>()
    navPipe
        .initWithNav(navController, bottomBarState)
        .collectAsStateWithLifecycle(LocalLifecycleOwner.current)

    NavHost(
        navController = navController,
        startDestination = Screen.TimelineScreen.route
    ) {
        composable(
            route = Screen.TimelineScreen.route,
            enterTransition = { navigateInAnimation },
            exitTransition = { navigateUpAnimation },
            popEnterTransition = { navigateInAnimation },
            popExitTransition = { navigateUpAnimation }
        ) {
            val viewModel = hiltViewModel<MediaViewModel>()
            viewModel.attachToLifecycle()
            TimelineScreen(
                paddingValues = paddingValues,
                mediaState = viewModel.mediaState,
            )
        }
        composable(
            route = Screen.AlbumsScreen.route,
            enterTransition = { navigateInAnimation },
            exitTransition = { navigateUpAnimation },
            popEnterTransition = { navigateInAnimation },
            popExitTransition = { navigateUpAnimation }
        ) {
            val viewModel = hiltViewModel<AlbumsViewModel>()
            viewModel.attachToLifecycle()
            AlbumsScreen(
                paddingValues = paddingValues,
                viewModel = viewModel,
            )
        }

        composable(
            route = Screen.LabelsScreen.route,
            enterTransition = { navigateInAnimation },
            exitTransition = { navigateUpAnimation },
            popEnterTransition = { navigateInAnimation },
            popExitTransition = { navigateUpAnimation }
        ) {
            val viewModel = hiltViewModel<LabelsViewModel>()
            viewModel.attachToLifecycle()
            PhotoLabelsScreen(
                navigate = navPipe::navigate,
                paddingValues = paddingValues,
                viewModel = viewModel,
            )
        }
        composable(
            route = Screen.LabelViewScreen.route +
                    "?labelName={labelName}",
            enterTransition = { navigateInAnimation },
            exitTransition = { navigateUpAnimation },
            popEnterTransition = { navigateInAnimation },
            popExitTransition = { navigateUpAnimation },
            arguments = listOf(
                navArgument(name = "labelName") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val argumentLabelName = backStackEntry.arguments?.getString("labelName")
                ?: stringResource(id = R.string.app_name)
            val viewModel: LabelsViewModel = hiltViewModel<LabelsViewModel>()
                .apply { labelName = argumentLabelName }
            viewModel.attachToLifecycle()
            LabelsViewScreen(
                labelName = argumentLabelName,
                labelState = viewModel.labelsGroupedState,
                navigateUp = navPipe::navigateUp
            )
        }
    }
}