package com.example.thaitoanki.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.example.thaitoanki.data.network.Definition

/*
https://stackoverflow.com/questions/47511750/how-to-use-foreign-key-in-room-persistence-library
https://developer.android.com/training/data-storage/room/relationships/one-to-many
 */
data class WordWithDetails(
    @Embedded
    val word: Word,
    @Relation(
        parentColumn = "word_id",
        entityColumn = "word_id"
    )
    val classifiers: List<Classifier>,
    @Relation(
        parentColumn = "word_id",
        entityColumn = "word_id"
    )
    val components: List<Component>,
    @Relation(
        parentColumn = "word_id",
        entityColumn = "word_id"
    )
    val examples: List<Example>,
    @Relation(
        parentColumn = "word_id",
        entityColumn = "word_id"
    )
    val relatedWords: List<RelatedWord>,
    @Relation(
        parentColumn = "word_id",
        entityColumn = "word_id"
    )
    val sentences: List<Sentence>,
    @Relation(
        parentColumn = "word_id",
        entityColumn = "word_id"
    )
    val synonyms: List<Synonym>
)

fun WordWithDetails.toDefinition(): Definition = Definition(
    baseWord = word.word,
    definition = word.definition,
    romanization = word.romanization,
    pronunciation = word.pronunciation,
    partOfSpeech = word.partOfSpeech,
    etymology = word.etymology,
    classifiers = classifiers.map { classifier ->
        Definition(
            baseWord = classifier.classifier,
            definition = classifier.definition,
            romanization = classifier.romanization,
        )
    },
    components = components.map { component ->
        Definition(
            baseWord = component.component,
            definition = component.definition,
            romanization = component.romanization
        )
    },
    synonyms = synonyms.map { synonym ->
        Definition(
            baseWord = synonym.synonym,
            definition = synonym.definition,
            romanization = synonym.romanization
        )
    },
    relatedWords = relatedWords.map{ relatedWord ->
        Definition(
            baseWord = relatedWord.relatedWord,
            definition = relatedWord.definition,
            romanization = relatedWord.romanization
        )
    },
    examples = examples.map { example ->
        Definition(
            baseWord = example.example,
            definition = example.definition,
            romanization = example.romanization
        )
    },
    sentences = sentences.map { sentence ->
        Definition(
            baseWord = sentence.sentence,
            definition = sentence.definition,
            romanization = sentence.romanization
        )
    },
    wordId = word.referenceId?.toInt()
)