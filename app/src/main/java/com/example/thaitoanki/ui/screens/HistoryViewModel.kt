/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.thaitoanki.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thaitoanki.data.database.WordsRepository
import com.example.thaitoanki.data.database.entities.toDefinition
import com.example.thaitoanki.data.network.Definition
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve all items in the Room database.
 */
class HistoryViewModel(
    private val wordsRepository: WordsRepository
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val historyUiState: StateFlow<HistoryUiState> = wordsRepository.getUniqueWordsStream()
        .map {
            val definitions: List<Definition> = it.map { wordWithDetails ->
                wordWithDetails.toDefinition()
            }
            HistoryUiState(definitions)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HistoryUiState()
        )
    // stateIn converts Flow into StateFlow
}

/**
 * Ui State for HistoryScreen
 */
data class HistoryUiState(val wordList: List<Definition> = listOf())
