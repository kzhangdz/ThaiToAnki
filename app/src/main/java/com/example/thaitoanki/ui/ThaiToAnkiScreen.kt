package com.example.thaitoanki.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.thaitoanki.R
import com.example.thaitoanki.network.Definition
import com.example.thaitoanki.ui.screens.FlashcardScreen
import com.example.thaitoanki.ui.screens.HistoryScreen
import com.example.thaitoanki.ui.screens.HomeScreen
import com.example.thaitoanki.ui.screens.ThaiViewModel

enum class ThaiToAnkiScreen(){
    Start,
    Flashcard,
    History,
    Options
}

@Composable
fun ThaiToAnkiApp(
    viewModel: ThaiViewModel = viewModel(factory = ThaiViewModel.Factory),
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        topBar = {
            // TODO: settings option in top bar
//            LunchTrayAppBar(
//                currentScreen = currentScreen,
//                canNavigateBack = navController.previousBackStackEntry != null,
//                navigateUp = {
//                    navController.navigateUp()
//                }
//            )
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

                HomeScreen(
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