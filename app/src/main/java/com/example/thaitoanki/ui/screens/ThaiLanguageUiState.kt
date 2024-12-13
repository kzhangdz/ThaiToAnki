package com.example.thaitoanki.ui.screens

import com.example.thaitoanki.network.Definition

data class ThaiLanguageUiState(
    val currentWord: String = "",
    val currentDefinitionIndex: Int = 0,
    val currentDefinitions: List<Definition> = listOf(),
    val isFlashcardShowingBack: Boolean = false // TODO: change to a list of true/false for multiple cards?
    //val definition: String = ""
)
