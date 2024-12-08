package com.example.thaitoanki.ui

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.thaitoanki.ui.screens.FlashcardScreen
import com.example.thaitoanki.ui.screens.HomeScreen
import com.example.thaitoanki.ui.screens.ThaiViewModel
import okhttp3.internal.wait

enum class ThaiToAnkiScreen(){
    Start,
    Flashcard,
    Options
}

@Composable
fun ThaiToAnkiApp(
    viewModel: ThaiViewModel = viewModel(factory = ThaiViewModel.Factory),
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        topBar = {
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
                HomeScreen(
                    searchValue = viewModel.searchValue,
                    onSearchValueChanged = {
                        viewModel.updateSearchValue(it)
                    },
                    onSearchButtonClicked = {
                        viewModel.searchDictionary()
                        navController.navigate(ThaiToAnkiScreen.Flashcard.name)
                    },
                    onKeyboardDone = {
                        viewModel.searchDictionary()
                        navController.navigate(ThaiToAnkiScreen.Flashcard.name)
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                )
            }

            composable(
                route = ThaiToAnkiScreen.Flashcard.name
            ) {
                FlashcardScreen(
                    flashcardInfo = uiState.definition,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                )
            }
        }
    }
}