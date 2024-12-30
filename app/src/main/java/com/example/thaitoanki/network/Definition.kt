package com.example.thaitoanki.network

import com.example.thaitoanki.data.database.entities.Classifier
import com.example.thaitoanki.data.database.entities.Component
import com.example.thaitoanki.data.database.entities.Example
import com.example.thaitoanki.data.database.entities.RelatedWord
import com.example.thaitoanki.data.database.entities.Sentence
import com.example.thaitoanki.data.database.entities.Synonym
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

fun Definition.toClassifiers(wordId: Long): List<Classifier> = classifiers
    .map { classifier ->
        Classifier(
            //classifierId = autoincrement,
            classifier = classifier.baseWord,
            definition = classifier.definition,
            romanization = classifier.romanization,
            wordId = wordId
        )
    }

fun Definition.toComponents(wordId: Long): List<Component> = components
    .map { component ->
        Component(
            //componentId = autoincrement,
            component = component.baseWord,
            definition = component.definition,
            romanization = component.romanization,
            wordId = wordId
        )
    }

fun Definition.toExamples(wordId: Long): List<Example> = examples
    .map { example ->
        Example(
            //exampleId = autoincrement,
            example = example.baseWord,
            definition = example.definition,
            romanization = example.romanization,
            wordId = wordId
        )
    }

fun Definition.toRelatedWords(wordId: Long): List<RelatedWord> = relatedWords
    .map { relatedWords ->
        RelatedWord(
            //relatedWordsId = autoincrement,
            relatedWord = relatedWords.baseWord,
            definition = relatedWords.definition,
            romanization = relatedWords.romanization,
            wordId = wordId
        )
    }

fun Definition.toSentences(wordId: Long): List<Sentence> = sentences
    .map { sentences ->
        Sentence(
            //sentencesId = autoincrement,
            sentence = sentences.baseWord,
            definition = sentences.definition,
            romanization = sentences.romanization,
            wordId = wordId
        )
    }

fun Definition.toSynonyms(wordId: Long): List<Synonym> = synonyms
    .map { synonyms ->
        Synonym(
            //synonymsId = autoincrement,
            synonym = synonyms.baseWord,
            definition = synonyms.definition,
            romanization = synonyms.romanization,
            wordId = wordId
        )
    }