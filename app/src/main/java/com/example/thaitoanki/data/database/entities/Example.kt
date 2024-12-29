package com.example.thaitoanki.data.database.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "examples",
    foreignKeys = [
        ForeignKey(
            entity = Word::class,
            parentColumns = arrayOf("word_id"),
            childColumns = arrayOf("word_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )]
)
data class Example(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "example_id")
    val exampleId: Long = 0,

    @NonNull
    val example: String,

    val definition: String,

    val romanization: String,

    @ColumnInfo(name = "word_id")
    val wordId: Long
)
