package com.example.thaitoanki.data.database

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

    override suspend fun insertWord(word: Word) {
        //TODO
    }

    override suspend fun deleteWord(word: Word) {
        //TODO
    }

    override suspend fun updateWord(word: Word) {
        //TODO
    }
}