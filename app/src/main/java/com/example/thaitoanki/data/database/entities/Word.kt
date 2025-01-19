package com.example.thaitoanki.data.database.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/*
Class to hold Definitions in the Room database
A view model can make an extension class like Word.toDefinition()
That extension function will likely need to use foreign keys to query for sentences and examples somehow
 */
@Entity(
    tableName = "words",
    indices = [Index(value = ["word_id"])]
    )
data class Word(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "word_id")
    val wordId: Long = 0,

    @NonNull
    val word: String,

    @NonNull
    val definition: String,

    val romanization: String,
    val pronunciation: String,

    @ColumnInfo(name = "part_of_speech")
    val partOfSpeech: String,
    val etymology: String,

    @ColumnInfo(name = "reference_id")
    val referenceId: Long?, // id in thai-language.com

    @ColumnInfo(name = "searched_at", defaultValue = "CURRENT_TIMESTAMP")
    val searchedAt: Long
)
