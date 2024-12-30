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

class OfflineWordsRepository(
    private val wordDao: WordDao
): WordsRepository {
    override fun getAllWordsStream(): Flow<List<WordWithDetails>> {
        return wordDao.getAll()
    }

    override fun getWordStream(id: Int): Flow<WordWithDetails?> {
        return wordDao.getByWordId(id.toLong())
    }

    @Transaction
    override suspend fun insertDefinitionAsWord(definition: Definition): Long{
        //TODO: only insert if the word and definition don't already exist in the database

        // get the definition parts
        val word: Word = definition.toWord()

        // insert the parts into the db
        val wordId = insertWord(word)

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