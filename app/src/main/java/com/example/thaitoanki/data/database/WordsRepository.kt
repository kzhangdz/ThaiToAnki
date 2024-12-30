package com.example.thaitoanki.data.database

import com.example.thaitoanki.data.database.entities.Classifier
import com.example.thaitoanki.data.database.entities.Component
import com.example.thaitoanki.data.database.entities.Example
import com.example.thaitoanki.data.database.entities.RelatedWord
import com.example.thaitoanki.data.database.entities.Sentence
import com.example.thaitoanki.data.database.entities.Synonym
import com.example.thaitoanki.data.database.entities.Word
import com.example.thaitoanki.data.database.entities.WordWithDetails
import com.example.thaitoanki.network.Definition
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

    fun getWordAndDefinitionStream(word: String, definition: String): Flow<WordWithDetails>

    /**
     * Insert word in the data source
     */
    suspend fun insertDefinitionAsWord(definition: Definition): Long?

    suspend fun insertWord(word: Word): Long //todo: may need more parameters for examples, sentences, etc.

    suspend fun insertClassifiers(classifiers: List<Classifier>): List<Long>

    suspend fun insertComponents(components: List<Component>): List<Long>

    suspend fun insertExamples(examples: List<Example>): List<Long>

    suspend fun insertRelatedWords(relatedWords: List<RelatedWord>): List<Long>

    suspend fun insertSentences(sentences: List<Sentence>): List<Long>

    suspend fun insertSynonyms(synonyms: List<Synonym>): List<Long>
    
    /**
     * Delete word from the data source
     */
    suspend fun deleteWord(word: Word)

    /**
     * Update word in the data source
     */
    suspend fun updateWord(word: Word)
}