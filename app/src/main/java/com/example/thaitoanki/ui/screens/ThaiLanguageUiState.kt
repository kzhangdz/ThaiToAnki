package com.example.thaitoanki.ui.screens

import com.example.thaitoanki.network.Definition

data class ThaiLanguageUiState(
    val currentWord: String = "",
    val currentDefinitionIndex: Int = 0,
    val currentDefinitions: List<Definition> = listOf(),
    val currentSentenceIndices: List<Int?> = listOf() // represents the selected sentence for a definition i.e. [0, null, 5, 2, null, 0]
    //val definition: String = ""
)
