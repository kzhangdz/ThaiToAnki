package com.example.thaitoanki.data.database.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "components",
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
data class Component(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "component_id")
    val componentId: Long = 0,

    @NonNull
    val component: String,

    val definition: String,

    val romanization: String,

    @ColumnInfo(name = "word_id")
    val wordId: Long
)
