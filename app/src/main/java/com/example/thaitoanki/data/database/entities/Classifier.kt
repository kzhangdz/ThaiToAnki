package com.example.thaitoanki.data.database.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "classifiers",
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
data class Classifier(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "classifier_id")
    val classifierId: Long = 0,

    @NonNull
    val classifier: String,

    val definition: String,

    val romanization: String,

    @ColumnInfo(name = "word_id")
    val wordId: Long
)
