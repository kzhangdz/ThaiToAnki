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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
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
 * ViewModel to retrieve words from the Room database.
 */
class FlashcardViewModel(
    savedStateHandle: SavedStateHandle,
    private val wordsRepository: WordsRepository
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

//    val flashcardUiState: StateFlow<FlashcardUiState> = wordsRepository.getUniqueWordsStream()
//        .map {
//            val definitions: List<Definition> = it.map { wordWithDetails ->
//                wordWithDetails.toDefinition()
//            }
//            HistoryUiState(definitions)
//        }
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//            initialValue = HistoryUiState()
//        )
    // stateIn converts Flow into StateFlow

    private val word: String = checkNotNull(savedStateHandle.get("word"))

    // TODO: does the current index need to be a MutableStateFlow?
    var currentDefinitionIndex by mutableStateOf(0)

    val flashcardUiState: StateFlow<FlashcardUiState> = wordsRepository.getMatchingWordsStream(word)
        .map { words ->
            // convert List<WordWithDetails> to FlashcardUiState
            val matchingDefinitions: List<Definition> = words.map { word ->
                word.toDefinition()
            }

            // TODO: add in currentsentence indices later
            FlashcardUiState(
                currentDefinitionIndex = currentDefinitionIndex,
                currentDefinitions = matchingDefinitions,
            )
        }
        .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = FlashcardUiState()
            )

    fun increaseCurrentDefinitionIndex(){
        val maxIndex = flashcardUiState.value.currentDefinitions.size - 1
        var updatedIndex = currentDefinitionIndex + 1
        if(updatedIndex > maxIndex){
            updatedIndex = 0
        }
        currentDefinitionIndex = updatedIndex
    }

    fun decreaseCurrentDefinitionIndex(){
        var updatedIndex = currentDefinitionIndex - 1
        if(updatedIndex < 0){
            val maxIndex = flashcardUiState.value.currentDefinitions.size - 1
            updatedIndex = maxIndex
        }
        currentDefinitionIndex = updatedIndex
    }
}

/**
 * Ui State for HistoryScreen
 */
data class FlashcardUiState(
    val currentDefinitionIndex: Int = 0,
    val currentDefinitions: List<Definition> = listOf(),
    val currentSentenceIndices: List<Int?> = listOf()
)
