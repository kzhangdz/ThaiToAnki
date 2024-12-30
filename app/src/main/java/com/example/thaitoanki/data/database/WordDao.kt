package com.example.thaitoanki.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
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

/*
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(vararg users: User)

    @Insert
    fun insertBothUsers(user1: User, user2: User)

    @Insert
    fun insertUsersAndFriends(user: User, friends: List<User>)
}
 */

@Dao
interface WordDao {
    // we can ignore insertions with the same primary key, since this app is guaranteed to only add one at a time
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: Word): Long // return Long to get the word_id back

    // vararg allows an unlimited number of arguments, i.e. insert(classifier2, classifier2, classifier3)
//    @Insert
//    suspend fun insert(vararg classifier: Classifier)

    @Insert
    suspend fun insertClassifiers(classifiers: List<Classifier>): List<Long>

    @Insert
    suspend fun insertComponents(components: List<Component>): List<Long>

    @Insert
    suspend fun insertExamples(examples: List<Example>): List<Long>

    @Insert
    suspend fun insertRelatedWords(relatedWords: List<RelatedWord>): List<Long>

    @Insert
    suspend fun insertSentences(sentences: List<Sentence>): List<Long>

    @Insert
    suspend fun insertSynonyms(synonyms: List<Synonym>): List<Long>

    @Update
    suspend fun update(word: Word)

    @Delete
    suspend fun delete(word: Word)

    /*
    Flow is the database listener

    It is recommended to use Flow in the persistence layer.
    With Flow as the return type, you receive notification whenever the data in the database changes.
    The Room keeps this Flow updated for you, which means you only need to explicitly get the data once.
    This setup is helpful to update the inventory list, which you implement in the next codelab. Because of the Flow return type, Room also runs the query on the background thread.
    You don't need to explicitly make it a suspend function and call it inside a coroutine scope.

    Note: Flow in Room database can keep the data up-to-date by emitting a notification whenever the data in the database changes.
    This allows you to observe the data and update your UI accordingly.
    */
    @Transaction
    @Query("SELECT * FROM words order by word_id desc")
    fun getAll(): Flow<List<WordWithDetails>>

    @Transaction
    @Query("SELECT * FROM words WHERE word_id = :id")
    fun getByWordId(id: Long): Flow<WordWithDetails>

    @Transaction
    @Query("SELECT * FROM words WHERE word = :word AND definition = :definition")
    fun getByWordAndDefinition(word: String, definition: String): Flow<WordWithDetails>

//    @Transaction
//    @Query("SELECT * FROM artist WHERE id = :id")
//    suspend fun getByArtistId(id: String): ArtistAndAlbums
//
//
//    @Query("SELECT * from items WHERE id = :id")
//    fun getItem(id: Int): Flow<Item>
//
//    @Query("SELECT * from items ORDER BY name ASC")
//    fun getAllItems(): Flow<List<Item>>
}