package com.example.thaitoanki.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.thaitoanki.R
import com.example.thaitoanki.data.AnkiDroidHelper
import com.example.thaitoanki.network.Definition
import com.example.thaitoanki.ui.screens.FlashcardScreen
import com.example.thaitoanki.ui.screens.HistoryScreen
import com.example.thaitoanki.ui.screens.SearchScreen
import com.example.thaitoanki.ui.screens.ThaiViewModel

import com.example.thaitoanki.ui.screens.SettingsScreen

enum class ThaiToAnkiScreen(){
    Start,
    Flashcard,
    History,
    Settings
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThaiToAnkiAppBar(
    currentScreen: ThaiToAnkiScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    actions: @Composable() (RowScope.() -> Unit),
    modifier: Modifier = Modifier,
    //topBarState: Boolean
) {
    //if(topBarState) {
        CenterAlignedTopAppBar(
            title = {
                if(currentScreen == ThaiToAnkiScreen.Settings){
                    Text(currentScreen.name)
                }
                else {
                    Text("")
                }
                    }, //{Text(stringResource(R.string.app_name))},
//        title = { Text(stringResource(currentScreen.title)) },
//        colors = TopAppBarDefaults.mediumTopAppBarColors(
//            containerColor = MaterialTheme.colorScheme.primary
//        ),

            scrollBehavior = null, // TODO: may implement hiding bar on scroll
            actions = actions,
            modifier = modifier,
//        navigationIcon = {
//            if (canNavigateBack) {
//                IconButton(onClick = navigateUp) {
//                    Icon(
//                        imageVector = Icons.Filled.ArrowBack,
//                        contentDescription = stringResource(R.string.back_button)
//                    )
//                }
//            }
//        }
        )
    //}
}

@Composable
fun ThaiToAnkiApp(
    viewModel: ThaiViewModel = viewModel(factory = ThaiViewModel.Factory),
    navController: NavHostController = rememberNavController()
) {
    val LOG_TAG = "ThaiToAnkiApp"

    //TODO: instead of removing the app bar, just put Settings in the title
    //https://stackoverflow.com/questions/66493551/jetpack-compose-navigation-get-route-of-current-destination-as-string
    // State of topBar, set state to false, if current page route is "settings"
    var topBarState by rememberSaveable { (mutableStateOf(true)) }

    // Subscribe to navBackStackEntry, required to get current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen = ThaiToAnkiScreen.valueOf(
        navBackStackEntry?.destination?.route ?: ThaiToAnkiScreen.Start.name
    )

    // Control TopBar and BottomBar
    when (navBackStackEntry?.destination?.route) {
        ThaiToAnkiScreen.Start.name -> {
            // Show TopBar
            topBarState = true
        }
        ThaiToAnkiScreen.Flashcard.name -> {
            // Show TopBar
            topBarState = true
        }
        ThaiToAnkiScreen.History.name -> {
            // Show opBar
            topBarState = true
        }
        ThaiToAnkiScreen.Settings.name -> {
            // Hide TopBar
            topBarState = false
        }
    }

    /*
    AnkiDroid Access
     */
    val context = LocalContext.current
    val ankiDroidHelper = AnkiDroidHelper(context)

    /*
    Setting variables
     */
    var hasNotificationPermission by rememberSaveable() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else {
            mutableStateOf(true)
        }
    }

    // TODO: change this to RequestMultiplePermissions and handle overlay permissions as well
    // notes: rememberLauncherForActivityResult essentially replicates a an intent + onActivityResult
    val notificationPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            isGranted ->
            hasNotificationPermission = isGranted
        }
    )

    var hasOverlayPermission by rememberSaveable() {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SYSTEM_ALERT_WINDOW
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val overlayPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            isGranted ->
            hasOverlayPermission = isGranted
        }
    )

    var hasReadWritePermission by rememberSaveable() {
        mutableStateOf(!ankiDroidHelper.shouldRequestPermission())
    }

    val readWritePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            isGranted ->
            hasReadWritePermission = isGranted
        }
    )

    Scaffold(
        topBar = {
            ThaiToAnkiAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = {
                    navController.navigateUp()
                },
                //topBarState = topBarState,
                actions = {
                    if(currentScreen != ThaiToAnkiScreen.Settings) {

                        IconButton(
                            onClick = {
                                navController.navigate(ThaiToAnkiScreen.Settings.name)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->

        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = ThaiToAnkiScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = ThaiToAnkiScreen.Start.name
            ) {
                // TODO: put HomeScreen and HistoryScreen into a single VerticalPager

                SearchScreen(
                    searchValue = viewModel.searchValue,
                    onSearchValueChanged = {
                        viewModel.updateSearchValue(it)
                    },
                    onSearchButtonClicked = {
                        viewModel.searchDictionary()
                        navController.navigate(ThaiToAnkiScreen.Flashcard.name)
                    },
                    onKeyboardSearch = {
                        viewModel.searchDictionary()
                        navController.navigate(ThaiToAnkiScreen.Flashcard.name)
                    },
                    onClearButtonClicked = {
                        viewModel.updateSearchValue("")
                    },
                    onDragUp = {
                        navController.navigate(ThaiToAnkiScreen.History.name)
                    },
                    onNavigationButtonClick = {
                        //TODO
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        // TODO: adding a scroll here seems to conflict with the drag options, potentially the pager as well
                        .verticalScroll(rememberScrollState())
//                        .fillMaxHeight()
//                        .fillMaxWidth()
                        .padding(
                            horizontal = dimensionResource(R.dimen.padding_medium)
                        )
                )
            }

            composable(
                route = ThaiToAnkiScreen.Flashcard.name
            ) {
                FlashcardScreen(
                    loadingStatus = viewModel.loadingStatus,
                    flashcardInfo = uiState.currentDefinitions,
                    currentDefinitionIndex = uiState.currentDefinitionIndex,
                    onFlashcardClick = {
                        // go to the next definition
                        viewModel.increaseCurrentDefinitionIndex()
                    },
                    onLeftButtonClick = {
                        // go to the previous definition
                        viewModel.decreaseCurrentDefinitionIndex()
                    },
                    onRightButtonClick = {
                        // go to the next definition
                        viewModel.increaseCurrentDefinitionIndex()
                    },
                    onSaveFlashcardButtonClick = {
                        if(!hasReadWritePermission){
                            // TODO: request read/write permission
                            // am I supposed to do that with a launcher, like above? Is there a way to put that inside the Helper?
                            readWritePermissionsLauncher.launch()
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
//                        .fillMaxHeight()
//                        .fillMaxWidth()
                        .padding(
                            horizontal = dimensionResource(R.dimen.padding_medium)
                        )
                )
            }

            composable(
                route = ThaiToAnkiScreen.History.name,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = {
                            it / 2
                        },
                    )
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = {
                            it / 2
                        },
                    )
                }
            ){
                HistoryScreen(
                    definitions = listOf(
                        Definition("test", "test"),
                        Definition("test2", "test2")
                    ),
                    onNavigationButtonClick = {
                        //TODO
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            horizontal = dimensionResource(R.dimen.padding_medium)
                        )
                )
            }

            composable(
                route = ThaiToAnkiScreen.Settings.name
            ) {
                SettingsScreen(
                    hasNotificationPermission = hasNotificationPermission,
                    onNotificationCheckedChange = {
                        // TODO: add ability to revoke permissions

                        // if the check is switched to true, launch the permission granter
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionsLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    },
                    hasOverlayPermission = hasOverlayPermission,
                    onOverlayCheckedChange = {
                        overlayPermissionsLauncher.launch(Manifest.permission.SYSTEM_ALERT_WINDOW)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            horizontal = dimensionResource(R.dimen.padding_medium)
                        )
                )
            }
        }
    }
}