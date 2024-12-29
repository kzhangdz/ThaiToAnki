package com.example.thaitoanki.data.database.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "related_words",
    foreignKeys = [
        ForeignKey(
            entity = Word::class,
            parentColumns = arrayOf("word_id"),
            childColumns = arrayOf("word_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )]
)
data class RelatedWord(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "related_word_id")
    val relatedWordId: Long = 0,

    @NonNull
    @ColumnInfo(name = "related_word")
    val relatedWord: String,

    val definition: String,

    val romanization: String,

    @ColumnInfo(name = "word_id")
    val wordId: Long
)
