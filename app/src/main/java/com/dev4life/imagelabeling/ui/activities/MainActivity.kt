package com.dev4life.imagelabeling.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.dev4life.imagelabeling.states.CurationState
import com.dev4life.imagelabeling.states.PermissionEvent
import com.dev4life.imagelabeling.ui.components.AppBarContainer
import com.dev4life.imagelabeling.ui.components.CurationDialog
import com.dev4life.imagelabeling.ui.components.NavigationComp
import com.dev4life.imagelabeling.ui.components.ShowSettingsDialog
import com.dev4life.imagelabeling.ui.theme.GalleryComposeTheme
import com.dev4life.imagelabeling.utils.hasPermissions
import com.dev4life.imagelabeling.utils.storagePermissions
import com.dev4life.imagelabeling.viewmodels.LabelsViewModel
import com.dev4life.imagelabeling.viewmodels.MediaViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mediaViewModel: MediaViewModel by viewModels()
    private val labelsViewModel: LabelsViewModel by viewModels()

    private var navigatedToSettings: Boolean = false
    private var askCount = 0

    private val permissionsLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantRes ->
            val grantedPermissions = grantRes.filterValues { it }.keys.toList()
            val deniedPermissions = grantRes.filterValues { !it }.keys.toList()
            mediaViewModel.onPermissionResult(grantedPermissions, deniedPermissions)
        }


    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            GalleryComposeTheme {

                val navController = rememberNavController()
                val isScrolling = remember { mutableStateOf(false) }
                val bottomBarState = rememberSaveable { (mutableStateOf(true)) }
                val systemBarFollowThemeState = rememberSaveable { (mutableStateOf(true)) }
                val systemUiController = rememberSystemUiController()
                systemUiController.systemBarsDarkContentEnabled =
                    systemBarFollowThemeState.value && !isSystemInDarkTheme()

                var canCurate by remember { mutableStateOf(false) }
                var isCurating by remember { mutableStateOf(false) }
                val showCuratingScreen by remember(isCurating) { derivedStateOf { isCurating } }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = { paddingValues ->
                        AppBarContainer(
                            navController = navController,
                            paddingValues = paddingValues,
                            bottomBarState = bottomBarState,
                            windowSizeClass = windowSizeClass,
                            isScrolling = isScrolling,
                            onCurateClick = {
                                isCurating = true
                                if (canCurate)
                                    labelsViewModel.startCuratingPhotos()
                                else labelsViewModel.checkAndCurate()
                            }
                        ) {
                            NavigationComp(
                                navController = navController,
                                paddingValues = paddingValues,
                                bottomBarState = bottomBarState,
                                systemBarFollowThemeState = systemBarFollowThemeState
                            )
                        }
                    }
                )

                val moduleState by labelsViewModel.moduleState.collectAsStateWithLifecycle()
                val labelingState by labelsViewModel.labelProcessState.collectAsStateWithLifecycle()
                var labelingDetails by remember { mutableStateOf(Pair(0, 0)) }

                if (showCuratingScreen) {
                    CurationDialog(labelingDetails.first, labelingDetails.second) {
                        labelsViewModel.stopCuratingPhotos()
                    }
                }

                LaunchedEffect(key1 = moduleState) {
                    if (moduleState.curationState is CurationState.ModulesAvailable) {
                        canCurate = true
                        labelsViewModel.startCuratingPhotos()
                    }
                }

                LaunchedEffect(key1 = labelingState) {
                    labelingDetails = Pair(labelingState.counts, labelingState.total)
                    isCurating = labelingState.isCurating
                }

                val permissionState = mediaViewModel.permissionEvents.collectAsState()
                val askCounts = mediaViewModel.askCounts

                val showSettingsDialog = remember { mutableStateOf(false) }

                if (showSettingsDialog.value) {
                    ShowSettingsDialog(this@MainActivity)
                }

                LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
                    if (mediaViewModel.getAskCounts() >= 2) {
                        mediaViewModel.resetAskCounts()
                        showSettingsDialog.value = true
                    }
                }

                LaunchedEffect(key1 = askCounts.value) {
                    if (!hasPermissions(storagePermissions)) {
                        navigatedToSettings = false
                        mediaViewModel.requestPermissions(this@MainActivity)
                    } else {
                        if (navigatedToSettings) {
                            mediaViewModel.onPermissionResult(
                                grantedPermissions = storagePermissions,
                                deniedPermissions = listOf()
                            )
                        }
                    }
                }

                LaunchedEffect(key1 = permissionState.value) {
                    when (val result = permissionState.value) {
                        is PermissionEvent.RequestPermissions -> {
                            permissionsLauncher.launch(storagePermissions.toTypedArray())
                        }

                        is PermissionEvent.PermissionResult -> {
                            val grantedPermissions = result.grantedPermissions
                            val deniedPermissions = result.deniedPermissions

                            mediaViewModel.incrementAskCounts()

                            if (grantedPermissions.containsAll(storagePermissions)) {
                                // All storage permissions granted, start using storage features
                                permissionGranted()
                            } else {
                                // Some or all storage permissions denied
                                val permanentlyDenied = deniedPermissions.filter {
                                    !shouldShowRequestPermissionRationale(it)
                                }
                                if (permanentlyDenied.isNotEmpty()) {
                                    // User has permanently denied some permissions, guide them to app settings
                                } else {
                                    // User has granted all permissions
                                    permissionGranted()
                                }
                            }
                        }

                        is PermissionEvent.ShowRationale -> {
                            showSettingsDialog.value = true
                        }

                        PermissionEvent.Idle -> {}
                    }
                }
            }
        }
    }

    private fun permissionGranted() {
        navigatedToSettings = false
        mediaViewModel.permissionGranted()
    }
}