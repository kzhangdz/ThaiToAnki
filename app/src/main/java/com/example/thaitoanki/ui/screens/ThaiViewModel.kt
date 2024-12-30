package com.example.thaitoanki.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thaitoanki.data.ThaiLanguageRepository
import com.example.thaitoanki.data.database.WordsRepository
import com.example.thaitoanki.data.database.entities.Word
import com.example.thaitoanki.network.Definition
import com.example.thaitoanki.network.ThaiLanguageData
import com.example.thaitoanki.network.ThaiLanguageSearchResults
import com.example.thaitoanki.network.toWord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    // only allow the state to be modified in this class by making it private
    private val _uiState = MutableStateFlow(ThaiLanguageUiState())

    // public variable that acts as our getter method for the state. read-only state flow
    val uiState: StateFlow<ThaiLanguageUiState> = _uiState.asStateFlow()

    var loadingStatus: LoadingStatus = LoadingStatus.Success //LoadingStatus.Loading
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
                    updateDefinitions(wordData.definitions)

                    // Save the successful search to the database
                    viewModelScope.launch {
                        saveWord()
                    }
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
            break
        }

//        val definition: Definition = uiState.value.currentDefinitions[uiState.value.currentDefinitionIndex]
//        wordsRepository.insertDefinitionAsWord(definition)
    }

    // can't directly pass in params to a view model, so we use this factory
//    companion object {
//        val Factory: ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val application = (this[APPLICATION_KEY] as ThaiLanguageApplication)
//                val thaiLanguageRepository = application.container.thaiLanguageRepository
//                ThaiViewModel(thaiLanguageRepository = thaiLanguageRepository)
//            }
//        }
//    }
}