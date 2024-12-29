package com.example.thaitoanki.data.database

import com.example.thaitoanki.data.database.entities.Word
import com.example.thaitoanki.data.database.entities.WordWithDetails
import kotlinx.coroutines.flow.Flow

interface WordsRepository {
    /**
     * Retrieve all the word from the the given data source.
     */
    fun getAllWordsStream(): Flow<List<WordWithDetails>>

    /**
     * Retrieve a word from the given data source that matches with the [id].
     */
    fun getWordStream(id: Int): Flow<WordWithDetails?>

    /**
     * Insert word in the data source
     */
    suspend fun insertWord(word: Word) //todo: may need more parameters for examples, sentences, etc.

    /**
     * Delete word from the data source
     */
    suspend fun deleteWord(word: Word)

    /**
     * Update word in the data source
     */
    suspend fun updateWord(word: Word)
}