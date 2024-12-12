package com.example.thaitoanki.ui.screens

import com.example.thaitoanki.network.Definition

data class ThaiLanguageUiState(
    val currentWord: String = "",
    val currentDefinitionIndex: Int = 0,
    val currentDefinitions: List<Definition> = listOf()
    //val definition: String = ""
)
