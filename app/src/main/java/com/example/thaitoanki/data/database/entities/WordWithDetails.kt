package com.example.thaitoanki.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

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
    val synonym: List<Synonym>
)
