package com.example.thaitoanki.data.anki

import android.app.Activity
import com.example.thaitoanki.data.network.Definition
import java.util.LinkedList

interface AnkiRepository {

    /**
     * Whether or not we should request full access to the AnkiDroid API
     */
    fun shouldRequestPermission(): Boolean

    /**
     * Request permission from the user to access the AnkiDroid API (for SDK 23+)
     * @param callbackActivity An Activity which implements onRequestPermissionsResult()
     * @param callbackCode The callback code to be used in onRequestPermissionsResult()
     */
    fun requestPermission(callbackActivity: Activity?, callbackCode: Int)

    /**
     * Save a mapping from deckName to getDeckId in the SharedPreferences
     */
    fun storeDeckReference(deckName: String?, deckId: Long)

    /**
     * Save a mapping from modelName to modelId in the SharedPreferences
     */
    fun storeModelReference(modelName: String?, modelId: Long)

    /**
     * Remove the duplicates from a list of note fields and tags
     * @param fields List of fields to remove duplicates from
     * @param tags List of tags to remove duplicates from
     * @param modelId ID of model to search for duplicates on
     */
    fun removeDuplicates(
        fields: LinkedList<Array<String>>,
        tags: LinkedList<Set<String?>?>,
        modelId: Long
    )

    /**
     * Try to find the given model by name, accounting for renaming of the model:
     * If there's a model with this modelName that is known to have previously been created (by this app)
     * and the corresponding model ID exists and has the required number of fields
     * then return that ID (even though it may have since been renamed)
     * If there's a model from #getModelList with modelName and required number of fields then return its ID
     * Otherwise return null
     * @param modelName the name of the model to find
     * @param numFields the minimum number of fields the model is required to have
     * @return the model ID or null if something went wrong
     */
    fun findModelIdByName(modelName: String, numFields: Int): Long?

    /**
     * Try to find the given deck by name, accounting for potential renaming of the deck by the user as follows:
     * If there's a deck with deckName then return it's ID
     * If there's no deck with deckName, but a ref to deckName is stored in SharedPreferences, and that deck exist in
     * AnkiDroid (i.e. it was renamed), then use that deck.Note: this deck will not be found if your app is re-installed
     * If there's no reference to deckName anywhere then return null
     * @param deckName the name of the deck to find
     * @return the did of the deck in Anki
     */
    fun findDeckIdByName(deckName: String): Long?

    /**
     * Get the ID of the deck which matches the name
     * @param deckName Exact name of deck (note: deck names are unique in Anki)
     * @return the ID of the deck that has given name, or null if no deck was found or API error
     */
    fun getDeckId(deckName: String): Long?

    fun createDeck(deckName: String): Long?

    fun createModel(modelName: String, deckId: Long): Long?

    /**
     * Add a card to the specified deck using the specified model
     * @param deckId ID of deck in Anki
     * @param modelId ID of the model to use in Anki
     * @param data list of field_name->data pairs
     * @return response code. 0: error.
     */
    fun addCardsToAnkiDroid(deckId: Long, modelId: Long, data: List<Map<String, String>>): Int

    fun getFields(): Array<String>

    fun definitionListToMapList(definitions: List<Definition>): List<Map<String, String>>

    // definitionToMap

    // definitionListToMapList
}

/**
 *
 * // only upload the highlighted flashcard, with it's selected sentence and example
 *                                 val currentDefinition = uiState.currentDefinitions[uiState.currentDefinitionIndex]
 *                                 currentDefinition.examples = currentDefinition.e
 */