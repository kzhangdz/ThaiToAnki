package com.example.thaitoanki.network

data class Definition(
    val baseWord: String,
    val definition: String,
    val romanization: String = "",
    val pronunciation: String = "",
    val partOfSpeech: String = "",
    val etymology: String = "",
    val synonyms: List<Definition> = emptyList(),
    val examples: List<Definition> = emptyList(),
    val sentences: List<Definition> = emptyList()
)
