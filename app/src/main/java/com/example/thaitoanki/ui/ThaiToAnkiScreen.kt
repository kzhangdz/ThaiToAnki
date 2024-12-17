package com.example.thaitoanki.ui

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.thaitoanki.R
import com.example.thaitoanki.network.Definition
import com.example.thaitoanki.ui.screens.FlashcardScreen
import com.example.thaitoanki.ui.screens.HistoryScreen
import com.example.thaitoanki.ui.screens.SearchScreen
import com.example.thaitoanki.ui.screens.ThaiViewModel

import com.example.thaitoanki.ui.screens.SettingsScreen
import kotlinx.coroutines.launch

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
    topBarState: Boolean
) {
    if(topBarState) {
        CenterAlignedTopAppBar(
            title = { Text("") }, //{Text(stringResource(R.string.app_name))},
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
    }
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

//    BackHandler {
//        if (navBackStackEntry?.destination?.route == ThaiToAnkiScreen.Flashcard.name){
//            Log.d(LOG_TAG, "Reset View Model")
//            viewModel.resetView()
//        }
//        else{
//            navBackStackEntry?.destination?.route?.let { Log.d(LOG_TAG, it) }
//        }
//    }

    Scaffold(
        topBar = {
            ThaiToAnkiAppBar(
                currentScreen = ThaiToAnkiScreen.Start, //currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = {
                    navController.navigateUp()
                },
                topBarState = topBarState,
                actions = {
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
            )
        }
    ) { innerPadding ->

        val uiState by viewModel.uiState.collectAsState()

        // Start the foreground service.
//        val launcher = rememberLauncherForActivityResult(
//            contract = ActivityResultContracts.RequestPermission(),
//            onResult = { isGranted ->
//                //startFloatingService()
//            }
//        )
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
//        }

//        val pushNotificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
//            viewModel.inputs.onTurnOnNotificationsClicked(granted)
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            pushNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
//        }



        NavHost(
            navController = navController,
            startDestination = ThaiToAnkiScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = ThaiToAnkiScreen.Start.name
            ) {
                // TODO: put HomeScreen and HistoryScreen into a single VerticalPager

                // TODO: clear word on back navigation
                // onDestroy for flashcardScreen?
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
                    onBackPressed = {
                        navController.popBackStack()


                        //viewModel.resetView()
                        //                            Log.d(LOG_TAG, "reset view model")
                    },
                    modifier = Modifier
                        .fillMaxSize()
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
                        .padding(
                            horizontal = dimensionResource(R.dimen.padding_medium)
                        )
                )
            }

            composable(
                route = ThaiToAnkiScreen.Settings.name
            ) {
                SettingsScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = dimensionResource(R.dimen.padding_medium)
                        )
                )
            }
        }
    }
}