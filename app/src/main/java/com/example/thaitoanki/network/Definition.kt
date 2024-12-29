package com.example.thaitoanki.network

import com.example.thaitoanki.data.database.entities.Word

data class Definition(
    val baseWord: String,
    val definition: String,
    val romanization: String = "",
    val pronunciation: String = "",
    val partOfSpeech: String = "",
    val etymology: String = "",
    val classifiers: List<Definition> = emptyList(),
    val components: List<Definition> = emptyList(),
    val synonyms: List<Definition> = emptyList(),
    val relatedWords: List<Definition> = emptyList(),
    val examples: List<Definition> = emptyList(),
    val sentences: List<Definition> = emptyList(),
    val wordId: Int? = null
)

// TODO: add extension function to convert Definition to db Word entity
//fun ItemDetails.toItem(): Item = Item(
//    id = id,
//    name = name,
//    price = price.toDoubleOrNull() ?: 0.0,
//    quantity = quantity.toIntOrNull() ?: 0
//)
fun Definition.toWord(): Word = Word(
    //wordId = autoincrement,
    //createdAt = "CURRENT_TIMESTAMP"
    word = baseWord,
    definition = definition,
    romanization = romanization,
    pronunciation = pronunciation,
    partOfSpeech = partOfSpeech,
    etymology = etymology,
    referenceId = wordId?.toLong(),
    createdAt = System.currentTimeMillis() //current timestamp in ms
)