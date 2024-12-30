package com.example.thaitoanki.data.database.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "synonyms",
    indices = [Index(value = ["word_id"])],
    foreignKeys = [
        ForeignKey(
            entity = Word::class,
            parentColumns = arrayOf("word_id"),
            childColumns = arrayOf("word_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )]
)
data class Synonym(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "synonym_id")
    val synonymId: Long = 0,

    @NonNull
    val synonym: String,

    val definition: String,

    val romanization: String,

    @ColumnInfo(name = "word_id")
    val wordId: Long
)
