package com.example.thaitoanki.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.thaitoanki.data.database.entities.Classifier
import com.example.thaitoanki.data.database.entities.Component
import com.example.thaitoanki.data.database.entities.Example
import com.example.thaitoanki.data.database.entities.RelatedWord
import com.example.thaitoanki.data.database.entities.Sentence
import com.example.thaitoanki.data.database.entities.Synonym
import com.example.thaitoanki.data.database.entities.Word
import com.example.thaitoanki.data.database.entities.WordWithDetails

@Database(entities = [
    Word::class,
    Classifier::class,
    Component::class,
    Example::class,
    RelatedWord::class,
    Sentence::class,
    Synonym::class
                     ], version = 1, exportSchema = false)
abstract class WordsDatabase: RoomDatabase() {
    abstract fun wordDao(): WordDao

    companion object {
        // Companion object to ensure we only ever have one instance of the db open
        /*
        The value of a volatile variable is never cached, and all reads and writes are to and from the main memory.
        These features help ensure the value of Instance is always up to date and is the same for all execution threads.
        It means that changes made by one thread to Instance are immediately visible to all other threads.
         */
        @Volatile
        private var Instance: WordsDatabase? = null

        /*
        Multiple threads can potentially ask for a database instance at the same time, which results in two databases instead of one.
        This issue is known as a race condition.
        Wrapping the code to get the database inside a synchronized block means that only one thread of execution at a time can enter this block of code,
        which makes sure the database only gets initialized once.
        Use synchronized{} block to avoid the race condition.
         */
        fun getDatabase(context: Context): WordsDatabase {
            return Instance ?: synchronized(this) {
                var dbName = "words_database"
                Room.databaseBuilder(context, WordsDatabase::class.java, dbName)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it } // reference to the db instance we just created
            }
        }
    }
}