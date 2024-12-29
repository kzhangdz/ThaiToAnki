package com.example.thaitoanki.data.database

import android.util.Log
import com.example.thaitoanki.data.database.entities.Word
import com.example.thaitoanki.data.database.entities.WordWithDetails
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

    // TODO modify this to take in examples, sentences, etc.
    override suspend fun insertWord(word: Word) {
        val id = wordDao.insert(word)
        Log.d("WordsRepository db", "Inserted $id: ${word.word}")
    }

    override suspend fun deleteWord(word: Word) {
        //TODO
    }

    override suspend fun updateWord(word: Word) {
        //TODO
    }
}