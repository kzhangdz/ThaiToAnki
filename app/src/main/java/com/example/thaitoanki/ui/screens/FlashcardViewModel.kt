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

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thaitoanki.data.database.WordsRepository
import com.example.thaitoanki.data.database.entities.toDefinition
import com.example.thaitoanki.data.network.Definition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

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

    // current thinking
    // Need to have a mutableStateFlow for FlashcardUiState.
    //

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

    val definitionsState: StateFlow<List<Definition>> = wordsRepository.getMatchingWordsStream(word)
        .map { words ->
            // convert List<WordWithDetails> to FlashcardUiState
            val matchingDefinitions: List<Definition> = words.map { word ->
                word.toDefinition()
            }

            // TODO: add in currentsentence indices later
//            FlashcardUiState(
//                currentDefinitionIndex = currentDefinitionIndex,
//                currentDefinitions = matchingDefinitions,
//            )
            matchingDefinitions
        }
        .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = listOf()
            )

    // currentExampleIndices
//    var currentExampleIndices: MutableState<List<Int>> by mutableStateOf(
//        List(size = flashcardUiState.value.currentDefinitions.size) { 0 }
//    )

    // initialize our main flashcard state using the data retrieved from the repository

    // only allow the state to be modified in this class by making it private
    private val _uiState = MutableStateFlow(FlashcardUiState())

    // public variable that acts as our getter method for the state. read-only state flow
    val uiState: StateFlow<FlashcardUiState> = _uiState.asStateFlow()

    fun initializeExampleIndices(){
        val indexList = mutableListOf<Int?>()
        for (definition in _uiState.value.currentDefinitions){
            if (definition.examples.isNotEmpty()){
                indexList.add(0)
            }
            else{
                indexList.add(null)
            }
        }
        _uiState.update {
                currentState ->
            currentState.copy(
                currentExampleIndices = indexList
            )
        }
    }

//    val currentExampleIndices by mutableStateListOf<Int>(
//        List(size = flashcardUiState.value.currentDefinitions.size) { 0 }
//    )

    private fun increaseIndex(baseList: List<Any>, valueToChange: Int): Int{
        val maxIndex = baseList.size - 1
        var updatedIndex = valueToChange + 1
        if(updatedIndex > maxIndex){
            updatedIndex = 0
        }
        return updatedIndex
    }

    private fun decreaseIndex(baseList: List<Any>, valueToChange: Int): Int{
        var updatedIndex = valueToChange - 1
        if(updatedIndex < 0){
            val maxIndex = baseList.size - 1
            updatedIndex = maxIndex
        }
        return updatedIndex
    }

    fun increaseCurrentDefinitionIndex(){
        val updatedIndex = increaseIndex(uiState.value.currentDefinitions, uiState.value.currentDefinitionIndex)
        _uiState.update {
                currentState ->
            currentState.copy(currentDefinitionIndex = updatedIndex)
        }
    }

    fun decreaseCurrentDefinitionIndex(){
        val updatedIndex = decreaseIndex(uiState.value.currentDefinitions, uiState.value.currentDefinitionIndex)
        _uiState.update {
                currentState ->
            currentState.copy(currentDefinitionIndex = updatedIndex)
        }
    }

    fun updateDefinitions(definitions: List<Definition>){
        _uiState.update {
                currentState ->
            currentState.copy(currentDefinitions = definitions)
        }
    }

//    fun increaseCurrentDefinitionIndex(){
//        currentDefinitionIndex = increaseIndex(flashcardUiState.value.currentDefinitions, currentDefinitionIndex)
//    }
//
//    fun decreaseCurrentDefinitionIndex(){
//        currentDefinitionIndex = decreaseIndex(flashcardUiState.value.currentDefinitions, currentDefinitionIndex)
//    }

//    fun increaseCurrentExampleIndex(){
//        currentExampleIndices[0] = increaseIndex(flashcardUiState.value., currentExampleIndex)
//    }
}

/**
 * Ui State for HistoryScreen
 */
data class FlashcardUiState(
    val currentDefinitionIndex: Int = 0,
    val currentDefinitions: List<Definition> = listOf(),
    val currentExampleIndices: List<Int?> = listOf(),
    val currentSentenceIndices: List<Int?> = listOf()
)
