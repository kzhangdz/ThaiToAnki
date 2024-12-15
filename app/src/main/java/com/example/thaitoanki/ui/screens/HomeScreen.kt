package com.example.thaitoanki.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.NavHostController
import com.example.thaitoanki.R
import com.example.thaitoanki.network.Definition
import com.example.thaitoanki.ui.ThaiToAnkiScreen
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select

@Composable
fun HomeScreen(
    // TODO: pass in screens?
    viewModel: ThaiViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 }) //HomeTabs.entries.size
    //val selectedTabIndex = rememberSaveable{ derivedStateOf { pagerState.currentPage } }

    VerticalPager(
        state = pagerState,
        modifier = modifier
    ) {
        if (pagerState.currentPage == 1) {
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
        else{
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
                    scope.launch {
                        pagerState.scrollToPage(0)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = dimensionResource(R.dimen.padding_medium)
                    )
            )
        }
    }
}