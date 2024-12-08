package com.example.thaitoanki.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.thaitoanki.ThaiLanguageApplication
import com.example.thaitoanki.data.ThaiLanguageRepository
import com.example.thaitoanki.network.ThaiLanguageData
import com.example.thaitoanki.network.ThaiLanguageSearchResults
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

class ThaiViewModel(private val thaiLanguageRepository: ThaiLanguageRepository): ViewModel() {

    // only allow the state to be modified in this class by making it private
    private val _uiState = MutableStateFlow(ThaiLanguageUiState())

    // public variable that acts as our getter method for the state. read-only state flow
    val uiState: StateFlow<ThaiLanguageUiState> = _uiState.asStateFlow()

    var loadingStatus: LoadingStatus = LoadingStatus.Loading

    var searchValue by mutableStateOf("")
        private set

    // TODO: function to reset view model, especially after a cancel

    fun updateSearchValue(updatedSearchValue: String){
        searchValue = updatedSearchValue
    }

    fun updateDefinition(definition: String){
        _uiState.update {
            currentState ->
            currentState.copy(definition = definition)
        }
    }

    /*
    Functions for querying data
     */
    fun searchDictionary(){
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
                        wordData = ThaiLanguageData(searchValue, definitionResult)
                    }
                }
                // branch for if the thaiLanguageSearchResults contain the data
                else{
                    wordData = ThaiLanguageData(searchValue, searchResult)
                }

                // TODO: branch for no results


                updateDefinition(searchResult.html())
                LoadingStatus.Success

            } catch (e: IOException) {
                //CLEARTEXT communication to www.thai-language.com not permitted by network security policy
                //https://stackoverflow.com/questions/45940861/android-8-cleartext-http-traffic-not-permitted
                LoadingStatus.Error
            }
        }
    }

    // can't directly pass in params to a view model, so we use this factory
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ThaiLanguageApplication)
                val thaiLanguageRepository = application.container.thaiLanguageRepository
                ThaiViewModel(thaiLanguageRepository = thaiLanguageRepository)
            }
        }
    }
}