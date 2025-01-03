package com.example.thaitoanki.data.database

import android.util.Log
import androidx.room.Transaction
import com.example.thaitoanki.data.database.entities.Classifier
import com.example.thaitoanki.data.database.entities.Component
import com.example.thaitoanki.data.database.entities.Example
import com.example.thaitoanki.data.database.entities.RelatedWord
import com.example.thaitoanki.data.database.entities.Sentence
import com.example.thaitoanki.data.database.entities.Synonym
import com.example.thaitoanki.data.database.entities.Word
import com.example.thaitoanki.data.database.entities.WordWithDetails
import com.example.thaitoanki.network.Definition
import com.example.thaitoanki.network.toClassifiers
import com.example.thaitoanki.network.toComponents
import com.example.thaitoanki.network.toExamples
import com.example.thaitoanki.network.toRelatedWords
import com.example.thaitoanki.network.toSentences
import com.example.thaitoanki.network.toSynonyms
import com.example.thaitoanki.network.toWord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

const val LOG_TAG = "OfflineWordsRepository"

class OfflineWordsRepository(
    private val wordDao: WordDao
): WordsRepository {

    override fun getAllWordsStream(): Flow<List<WordWithDetails>> {
        return wordDao.getAll()
    }

    override fun getUniqueWordsStream(): Flow<List<WordWithDetails>> {
        return wordDao.getAllUniqueWords()
    }

    override fun getMatchingWordsStream(word: String): Flow<List<WordWithDetails>> {
        return wordDao.getMatchingWords(word)
    }

    override fun getWordStream(id: Int): Flow<WordWithDetails?> {
        return wordDao.getByWordId(id.toLong())
    }

    override fun getWordAndDefinitionStream(
        word: String,
        definition: String
    ): Flow<WordWithDetails> {
        return wordDao.getByWordAndDefinition(word, definition)
    }

    // TODO: could change this to use functions w/ an @Upsert tag
    @Transaction
    override suspend fun insertDefinitionAsWord(definition: Definition): Long?{
        //TODO: only insert if the word and definition don't already exist in the database
        val wordStream = getWordAndDefinitionStream(definition.baseWord, definition.definition)
        val existingWord = wordStream.firstOrNull()

        // get the definition parts
        val wordToInsert: Word = definition.toWord()

        if(existingWord == null) {
            // insert the parts into the db
            val wordId = insertWord(wordToInsert)

            val classifiers = definition.toClassifiers(wordId)
            val components = definition.toComponents(wordId)
            val examples = definition.toExamples(wordId)
            val relatedWords = definition.toRelatedWords(wordId)
            val sentences = definition.toSentences(wordId)
            val synonyms = definition.toSynonyms(wordId)

            insertClassifiers(classifiers)
            insertComponents(components)
            insertExamples(examples)
            insertRelatedWords(relatedWords)
            insertSentences(sentences)
            insertSynonyms(synonyms)

            return wordId
        }
        else{
            Log.d(LOG_TAG, "Word ${definition.baseWord} already exists in the database. Updating instead")

            // get the primary key of the existing word
            val existingId = existingWord.word.wordId

            // mark our word with that id
            val wordToUpdate = wordToInsert.copy(wordId = existingId)

            //update the word, which will include an updated searchedAt field
            wordDao.update(wordToUpdate)

            return null
        }
    }

    // TODO modify this to take in examples, sentences, etc.
    override suspend fun insertWord(word: Word): Long {
        val id = wordDao.insert(word)
        Log.d("WordsRepository db", "Inserted $id: ${word.word}")
        return id
    }

    override suspend fun insertClassifiers(classifiers: List<Classifier>): List<Long>{
        val ids = wordDao.insertClassifiers(classifiers)
        return ids
    }

    override suspend fun insertComponents(components: List<Component>): List<Long>{
        val ids = wordDao.insertComponents(components)
        return ids
    }

    override suspend fun insertExamples(examples: List<Example>): List<Long>{
        val ids = wordDao.insertExamples(examples)
        return ids
    }

    override suspend fun insertRelatedWords(relatedWords: List<RelatedWord>): List<Long>{
        val ids = wordDao.insertRelatedWords(relatedWords)
        return ids
    }

    override suspend fun insertSentences(sentences: List<Sentence>): List<Long>{
        val ids = wordDao.insertSentences(sentences)
        return ids
    }

    override suspend fun insertSynonyms(synonyms: List<Synonym>): List<Long>{
        val ids = wordDao.insertSynonyms(synonyms)
        return ids
    }

    override suspend fun deleteWord(word: Word) {
        //TODO
    }

    override suspend fun updateWord(word: Word) {
        //TODO
    }
}