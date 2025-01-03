package com.example.thaitoanki.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thaitoanki.data.ThaiLanguageRepository
import com.example.thaitoanki.data.database.WordsRepository
import com.example.thaitoanki.data.database.entities.Word
import com.example.thaitoanki.data.database.entities.WordWithDetails
import com.example.thaitoanki.data.database.entities.toDefinition
import com.example.thaitoanki.network.Definition
import com.example.thaitoanki.network.ThaiLanguageData
import com.example.thaitoanki.network.ThaiLanguageSearchResults
import com.example.thaitoanki.network.toWord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

enum class LoadingStatus(){
    Success,
    Error,
    Loading
}

class ThaiViewModel(
    private val thaiLanguageRepository: ThaiLanguageRepository,
    private val wordsRepository: WordsRepository
): ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    // only allow the state to be modified in this class by making it private
    private val _uiState = MutableStateFlow(ThaiLanguageUiState())

    // public variable that acts as our getter method for the state. read-only state flow
    val uiState: StateFlow<ThaiLanguageUiState> = _uiState.asStateFlow()

    var loadingStatus: LoadingStatus by mutableStateOf(LoadingStatus.Success) //LoadingStatus.Loading
        private set

    var searchValue by mutableStateOf("")
        private set

    /*
    Setting variables
     */


    fun resetView(){
        _uiState.value = ThaiLanguageUiState()

        updateLoadingStatus(LoadingStatus.Success)
        //updateSearchValue("")
    }


    fun updateLoadingStatus(updatedStatus: LoadingStatus){
        loadingStatus = updatedStatus
    }

    fun updateSearchValue(updatedSearchValue: String){
        searchValue = updatedSearchValue
    }

//    fun updateDefinition(definition: String){
//        _uiState.update {
//            currentState ->
//            currentState.copy(definition = definition)
//        }
//    }

    fun updateDefinitions(definitions: List<Definition>){
        _uiState.update {
            currentState ->
            currentState.copy(currentDefinitions = definitions)
        }
    }

    fun increaseCurrentDefinitionIndex(){
        val maxIndex = _uiState.value.currentDefinitions.size - 1
        var updatedIndex = _uiState.value.currentDefinitionIndex + 1
        if(updatedIndex > maxIndex){
            updatedIndex = 0
        }
        _uiState.update {
            currentState ->
            currentState.copy(currentDefinitionIndex = updatedIndex)
        }
    }

    fun decreaseCurrentDefinitionIndex(){
        var updatedIndex = _uiState.value.currentDefinitionIndex - 1
        if(updatedIndex < 0){
            val maxIndex = _uiState.value.currentDefinitions.size - 1
            updatedIndex = maxIndex
        }
        _uiState.update {
            currentState ->
            currentState.copy(currentDefinitionIndex = updatedIndex)
        }
    }

    /*
    Functions for querying data from the api
     */
    fun searchDictionary(){
        // reset all previous results when searching
        resetView()

        updateLoadingStatus(LoadingStatus.Loading)

        // TODO: the code might not be properly awaiting for a response to finish before continuing. need to properly await
        viewModelScope.launch {
            loadingStatus = try {

                val searchResult = thaiLanguageRepository.searchDictionary(searchValue)
                val thaiLanguageSearchResults = ThaiLanguageSearchResults(searchValue, searchResult)

                val id = thaiLanguageSearchResults.topResultId

                var wordData: ThaiLanguageData? = null
                // if we have not retrieved the definition, query for it
                if (!thaiLanguageSearchResults.isDefinitionRetrieved){
                    val definitionResult = id?.let { thaiLanguageRepository.getWord(it) }

                    if (definitionResult != null) {
                        wordData = ThaiLanguageData(searchValue, definitionResult, wordId = id)
                    }
                }
                // branch for if the thaiLanguageSearchResults contain the data
                else{
                    wordData = ThaiLanguageData(searchValue, searchResult, wordId = id)
                }

                if (wordData != null) {
                    // TODO: may want to considering adding a condition to check if the search matches the returned value before saving
                    // Or, we could add a search results page
                    //if (wordData.definitions[0] == searchValue)
                    updateDefinitions(wordData.definitions)

                    // Save the successful search to the database
                    saveWord()
                }
                LoadingStatus.Success

            } catch (e: IOException) {
                //CLEARTEXT communication to www.thai-language.com not permitted by network security policy
                //https://stackoverflow.com/questions/45940861/android-8-cleartext-http-traffic-not-permitted
                LoadingStatus.Error
            }
        }
    }

    /*
    Database functions
     */
    suspend fun saveWord(){
        //TODO; save the list of definitions
        val definitions = uiState.value.currentDefinitions

        for (definition in definitions){
            wordsRepository.insertDefinitionAsWord(definition)

            // todo: only save the first definition for now
            //break
        }

//        val definition: Definition = uiState.value.currentDefinitions[uiState.value.currentDefinitionIndex]
//        wordsRepository.insertDefinitionAsWord(definition)
    }

//    fun findMatchingWords(word: String){
//        resetView()
//
//        viewModelScope.launch {
//            val matchingDefinitions = getMatchingWords(word)
//
//            updateDefinitions(matchingDefinitions)
//        }
//    }

//    suspend fun getMatchingWords(word: String): List<Definition> {
//        val matchingWords: List<WordWithDetails> = wordsRepository.getMatchingWords(word)
//
//        val matchingDefinitions: List<Definition> = matchingWords.map { word ->
//            word.toDefinition()
//        }
//
//        return matchingDefinitions
//    }



//
//    fun getMatchingWordsStream(word: String): Flow<List<WordWithDetails>> {
//        var matchingWords: List<WordWithDetails> = mutableListOf()
//        // TODO: don't collect inside the viewModel. emit inside the view model and collect in the activity
//        var matchingWordsFlow = wordsRepository.getMatchingWordsStream(word)
//
//        return matchingWordsFlow
//    }
//
//    suspend fun getMatchingWords(word: String): List<Definition>{
//        var matchingWords: List<WordWithDetails> = mutableListOf()
//        // TODO: don't collect inside the viewModel. emit inside the view model and collect in the activity
//        wordsRepository.getMatchingWordsStream(word)
//            .collect(){ words ->
//                matchingWords = words
//                Log.d("ThaiViewModel", "Outputting Matching Words: ${words.toString()}")
//            }
//
//        val matchingDefinitions: List<Definition> = matchingWords.map { word ->
//            word.toDefinition()
//        }
//
//        return matchingDefinitions
//    }
//
//    private val queriedWord: MutableStateFlow<String> = MutableStateFlow("")
//
//    fun updateQueriedWord(word: String) {
//        queriedWord.value = word
//    }
//
//    // TODO: this doesn't seem to be triggering when queriedWord is changed
//    val matchingWords: StateFlow<List<WordWithDetails>> =
//        wordsRepository.getMatchingWordsStream(queriedWord.value)
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = listOf()
//            )

//    val matchingWords: StateFlow<> =
//        wordsRepository.getMatchingWordsStream()
//            .filterNotNull()
//            .map {
//                ItemDetailsUiState(outOfStock = it.quantity <= 0, itemDetails = it.toItemDetails())
//            }.stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = ItemDetailsUiState()
//            )

}