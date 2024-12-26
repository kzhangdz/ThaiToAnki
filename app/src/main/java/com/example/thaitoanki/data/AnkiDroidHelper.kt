package com.example.thaitoanki.data

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.util.SparseArray
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.thaitoanki.network.Definition
import com.example.thaitoanki.services.indexesOf
import com.example.thaitoanki.services.readArrayFromAsset
import com.example.thaitoanki.services.readTextFromAsset
import com.ichi2.anki.api.AddContentApi
import com.ichi2.anki.api.NoteInfo
import java.util.LinkedList


//interface ThaiLanguageRepository {
//    suspend fun searchDictionary(word: String): Document
//
//    suspend fun getWord(wordId: Int): Document
//}

//https://medium.com/@michaelangelo.reyes19/building-a-data-layer-in-android-using-the-repository-pattern-177cb21e4115#:~:text=One%20of%20the%20most%20effective,modular%2C%20maintainable%2C%20and%20testable.
// can probably make a repository without a retrofit api service
// can pass in a Room db service later

// put functions that trigger the ankidroid api here

// TODO: pass this into the ThaiViewModel

class AnkiDroidHelper(context: Context) {
    val api: AddContentApi
    private val mContext: Context = context.applicationContext
    val READ_WRITE_PERMISSION = AddContentApi.READ_WRITE_PERMISSION
    val LOG_TAG = "AnkiDroidHelper"

    // Anki Intent variables
    val FIELDS: Array<String> = context.readArrayFromAsset(AnkiDroidConfig.FIELDS_FILE_LOCATION)
    val CSS = context.readTextFromAsset(AnkiDroidConfig.CSS_FILE_LOCATION)
    val QFMT = arrayOf(
        context.readTextFromAsset(AnkiDroidConfig.QFMT_FILE_LOCATION)
    )
    val AFMT = arrayOf(
        context.readTextFromAsset(AnkiDroidConfig.AFMT_FILE_LOCATION)
    )

    init {
        api = AddContentApi(mContext)
        for (field in FIELDS){
            Log.i(LOG_TAG, "FIELDS: $field")
        }
    }

    /**
     * Whether or not we should request full access to the AnkiDroid API
     */
    fun shouldRequestPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false
        }
        return ContextCompat.checkSelfPermission(
            mContext,
            AddContentApi.READ_WRITE_PERMISSION
        ) != PackageManager.PERMISSION_GRANTED
    }

    /**
     * Request permission from the user to access the AnkiDroid API (for SDK 23+)
     * @param callbackActivity An Activity which implements onRequestPermissionsResult()
     * @param callbackCode The callback code to be used in onRequestPermissionsResult()
     */
    fun requestPermission(callbackActivity: Activity?, callbackCode: Int) {
        ActivityCompat.requestPermissions(
            callbackActivity!!,
            arrayOf(AddContentApi.READ_WRITE_PERMISSION),
            callbackCode
        )
    }


    /**
     * Save a mapping from deckName to getDeckId in the SharedPreferences
     */
    fun storeDeckReference(deckName: String?, deckId: Long) {
        val decksDb = mContext.getSharedPreferences(DECK_REF_DB, Context.MODE_PRIVATE)
        decksDb.edit().putLong(deckName, deckId).apply()
    }

    /**
     * Save a mapping from modelName to modelId in the SharedPreferences
     */
    fun storeModelReference(modelName: String?, modelId: Long) {
        val modelsDb = mContext.getSharedPreferences(MODEL_REF_DB, Context.MODE_PRIVATE)
        modelsDb.edit().putLong(modelName, modelId).apply()
    }

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
    ) {
        // Build a list of the duplicate keys (first fields) and find all notes that have a match with each key
        val keys: MutableList<String> = ArrayList(fields.size)
        for (f in fields) {
            f[0]?.let { keys.add(it) }
        }
        val duplicateNotes: SparseArray<MutableList<NoteInfo?>>? = api.findDuplicateNotes(modelId, keys)
        // Do some sanity checks
        check(tags.size == fields.size) { "List of tags must be the same length as the list of fields" }
        if (duplicateNotes == null || duplicateNotes.size() == 0 || fields.size == 0 || tags.size == 0) {
            return
        }
        check(duplicateNotes.keyAt(duplicateNotes.size() - 1) < fields.size) { "The array of duplicates goes outside the bounds of the original lists" }
        // Iterate through the fields and tags LinkedLists, removing those that had a duplicate
        val fieldIterator = fields.listIterator()
        val tagIterator = tags.listIterator()
        var listIndex = -1
        for (i in 0 until duplicateNotes.size()) {
            val duplicateIndex = duplicateNotes.keyAt(i)
            while (listIndex < duplicateIndex) {
                fieldIterator.next()
                tagIterator.next()
                listIndex++
            }
            fieldIterator.remove()
            tagIterator.remove()
        }
    }


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
    fun findModelIdByName(modelName: String, numFields: Int): Long? {
        val modelsDb = mContext.getSharedPreferences(MODEL_REF_DB, Context.MODE_PRIVATE)
        val prefsModelId = modelsDb.getLong(modelName, -1L)
        // if we have a reference saved to modelName and it exists and has at least numFields then return it
        if ((prefsModelId != -1L)
            && (api.getModelName(prefsModelId) != null)
            && (api.getFieldList(prefsModelId) != null)
            && (api.getFieldList(prefsModelId)!!.size >= numFields)
        ) { // could potentially have been renamed
            return prefsModelId
        }
        val modelList: Map<Long, String>? = api.getModelList(numFields)
        if (modelList != null) {
            for ((key, value) in modelList) {
                if (value == modelName) {
                    return key // first model wins
                }
            }
        }
        // model no longer exists (by name nor old id), the number of fields was reduced, or API error
        return null
    }


    /**
     * Try to find the given deck by name, accounting for potential renaming of the deck by the user as follows:
     * If there's a deck with deckName then return it's ID
     * If there's no deck with deckName, but a ref to deckName is stored in SharedPreferences, and that deck exist in
     * AnkiDroid (i.e. it was renamed), then use that deck.Note: this deck will not be found if your app is re-installed
     * If there's no reference to deckName anywhere then return null
     * @param deckName the name of the deck to find
     * @return the did of the deck in Anki
     */
    fun findDeckIdByName(deckName: String): Long? {
        val decksDb = mContext.getSharedPreferences(DECK_REF_DB, Context.MODE_PRIVATE)
        // Look for deckName in the deck list
        var did = getDeckId(deckName)
        if (did != null) {
            // If the deck was found then return its id
            return did
        } else {
            // Otherwise try to check if we have a reference to a deck that was renamed and return that
            did = decksDb.getLong(deckName, -1)
            return if (did != -1L && api.getDeckName(did) != null) {
                did
            } else {
                // If the deck really doesn't exist then return null
                null
            }
        }
    }

    /**
     * Get the ID of the deck which matches the name
     * @param deckName Exact name of deck (note: deck names are unique in Anki)
     * @return the ID of the deck that has given name, or null if no deck was found or API error
     */
    private fun getDeckId(deckName: String): Long? {
        val deckList = api.deckList
        if (deckList != null) {
            for ((key, value) in deckList) {
                if (value.equals(deckName, ignoreCase = true)) {
                    return key
                }
            }
        }
        return null
    }

//    fun addCardsToAnkiDroid(deckName: String, modelName: String, modelFields: Array<String>, data: List<Map<String, String>>){
//        var deckId = findDeckIdByName(deckName)
//        if(deckId == null) {
//            deckId = api.addNewDeck(deckName)
//            deckId?.let { storeDeckReference(deckName, it) }
//        }
//
//        var modelId = findModelIdByName(
//            modelName,
//            numFields = modelFields.size
//        )
//        if (modelId == null){
//            modelId = api.addNewCustomModel(
//                modelName,
//                fields = modelFields,
//                cards = TODO(),
//                qfmt = TODO(),
//                afmt = TODO(),
//                css = TODO(),
//                did = TODO(),
//                sortf = TODO()
//            )
//        }
//
//        if (deckId == null || modelId == null){
//            return
//        }
//
//    }

    fun createDeck(deckName: String): Long?{
        val deckId = api.addNewDeck(deckName)
        deckId?.let { storeDeckReference(deckName, it) }
        return deckId
    }

    fun createModel(modelName: String, deckId: Long): Long?{
//        val modelId = api.addNewCustomModel(modelName, AnkiDroidConfig.FIELDS,
//            AnkiDroidConfig.CARD_NAMES, AnkiDroidConfig.QFMT, AnkiDroidConfig.AFMT, AnkiDroidConfig.CSS, deckId, null);
        val modelId = api.addNewCustomModel(modelName, FIELDS,
            AnkiDroidConfig.CARD_NAMES, QFMT, AFMT, CSS, deckId, null);
        modelId?.let {storeModelReference(modelName, it)}
        return modelId
    }

    fun addCardsToAnkiDroid(deckId: Long, modelId: Long, data: List<Map<String, String>>): Int{
        // TODO: add a code output
        // TODO: look into YomiChan's data output format

        val fieldNames = api.getFieldList(modelId)
        if(fieldNames == null){
            // api error
            Log.d(LOG_TAG, "Field names null")
            return 0
        }

        // Build list of fields and tags
        val fields = LinkedList<Array<String>>()
        val tags = LinkedList<Set<String?>?>()
        val finalTags: MutableList<Set<String>?> = mutableListOf()
        for (fieldMap in data) {
            // Build a field map accounting for the fact that the user could have changed the fields in the model
            val flds = Array<String>(fieldNames.size){""} //arrayOfNulls<String>(fieldNames.size)
            for (i in flds.indices) {
                // Fill up the fields one-by-one until either all fields are filled or we run out of fields to send
                if (i < AnkiDroidConfig.FIELDS.size) {
                    flds[i] = fieldMap[AnkiDroidConfig.FIELDS.get(i)].toString()
                }
            }
            tags.add(AnkiDroidConfig.TAGS)
            fields.add(flds)

            finalTags.add(AnkiDroidConfig.TAGS)
        }

        val finalFields = fields.toList()
        //val finalTags = fields.toList()

        // Remove any duplicates from the LinkedLists and then add over the API
        removeDuplicates(fields, tags, modelId)
        val added: Int = api.addNotes(modelId, deckId, finalFields, finalTags)
        if (added != 0) {
            // successful
            Log.d(LOG_TAG, "successful insertion")
        } else {
            // API indicates that a 0 return value is an error
            Log.d(LOG_TAG, "failed insertion")
        }
        return added
    }

    fun definitionToMap(definition: Definition): Map<String, String>{
        // TODO: format synonym, relatedwords, etc.
        val synonyms = formatSynonymsToHTML(definition.synonyms)
        val examples = formatExamplesToHTML(definition.examples, definition.baseWord)
        val sentences = formatSentencesToHTML(definition.sentences, definition.baseWord)

        Log.i(LOG_TAG, "Examples: $examples")

        val map: Map<String, String> = mapOf(
            AnkiDroidConfig.FIELDS[0] to definition.baseWord,
            AnkiDroidConfig.FIELDS[1] to definition.pronunciation,
            AnkiDroidConfig.FIELDS[2] to definition.romanization,
            AnkiDroidConfig.FIELDS[3] to definition.partOfSpeech,
            AnkiDroidConfig.FIELDS[4] to definition.definition,
            AnkiDroidConfig.FIELDS[5] to synonyms,
            AnkiDroidConfig.FIELDS[6] to "", // TODO: relatedwords
            AnkiDroidConfig.FIELDS[7] to examples,
            AnkiDroidConfig.FIELDS[8] to sentences,
            AnkiDroidConfig.FIELDS[9] to definition.wordId?.toString().orEmpty() // convert to "" if null
        )

        return map
    }

    fun definitionListToMapList(definitions: List<Definition>): List<Map<String, String>>{
        val mapList: MutableList<Map<String, String>> = mutableListOf()
        for (definition in definitions){
            val map = definitionToMap(definition)
            mapList.add(map)

            // TODO: for now, only add the first definition
            break
        }
        return mapList
    }

    /*
    Desired Output
    <span class="pill">คุย (to chat)</span>
	<span class="pill">เม้าท์ (to talk)</span>
     */
    fun formatSynonymsToHTML(synonyms: List<Definition>): String{
        var HTMLString = ""
        for (synonym in synonyms){
            val currentHTMLString = """<span class="pill">${synonym.baseWord} (${synonym.definition})</span>"""
            HTMLString += currentHTMLString
        }
        return HTMLString
    }

    // TODO: potential error where every string that matches the word gets highlighted.
    // could happen easily with งง, i.e. วงงง
    /*
    Desired Output
    <span class="highlight">พูด</span>เล่น - to joke; make a joke; speak jokingly; to kid
     */
    fun formatExamplesToHTML(examples: List<Definition>, word: String): String{
        var HTMLString = ""
        for(example in examples){
            var currentHTMLString = addHighlightHTML(example, word)

            currentHTMLString += " - ${example.definition}"

            HTMLString += currentHTMLString

            // TODO: for now, only use the first example
            break
        }
        return HTMLString
    }

    /*
    Desired Output
    <div><span class="highlight">พูด</span>ภาษาไทยได้ไหมครับ</div>
	<div class="description">Can you speak Thai?</div>
     */
    fun formatSentencesToHTML(sentences: List<Definition>, word: String): String{
        var HTMLString = ""
        for(sentence in sentences){
            // get output like <span class="highlight">พูด</span>ภาษาไทยได้ไหมครับ
            var currentHTMLString = addHighlightHTML(sentence, word)

            // add div around the output
            currentHTMLString = "<div>${currentHTMLString}</div>"

            // add the romanization <div class="description">innerHTML</div>
            currentHTMLString += """<div class="description">${sentence.romanization}</div>"""

            // add the translation <div class="description">Can you speak Thai?</div>
            currentHTMLString += """<div class="description">${sentence.definition}</div>"""

            HTMLString += currentHTMLString

            // TODO: for now, only use the first sentence
            break
        }
        return HTMLString
    }

    /*
    Given a word, add a <span class="highlight"></span> around the word, using the sliding window method
     */
    fun addHighlightHTML(definition: Definition, word: String): String{
        var currentHTMLString = ""

        var i = 0
        while(i < definition.baseWord.length){ //- word.length
            val windowEndIndex = i + word.length

            // at the end, if there isn't enough room left for the window
            if(windowEndIndex >= definition.baseWord.length){
                // add the current character
                currentHTMLString += definition.baseWord[i]
                i++
            }
            else {
                val currentWindow: String = definition.baseWord.substring(i, windowEndIndex)

                if (currentWindow == word) {
                    currentHTMLString += """<span class="highlight">${currentWindow}</span>"""

                    // skip to the end of the window word
                    i = windowEndIndex
                } else {
                    // otherwise, add the current character
                    currentHTMLString += currentWindow[0]
                    i++
                }
            }
        }

        return currentHTMLString
    }

    /*
    TODO: function to convert definitions to HTML string in this format:
    <div style="text-align: left;">
       <ol>
          <li>ごう‐かい【豪快】ガウクヮイ〘名・形動〙<br>規模が大きく、気持ちがよいほど力強いこと。「─な上手投げが決まる」「─に笑う」  ‐さ<br></li>
          <li>ごう かい （がうくわい） [0]【豪快】<br>（形動）〔文〕ナリ<br>堂々として力にあふれ，気持ちのよいさま。「―な投げわざ」「―に笑う」<br>〔派生〕― さ（名）</li>
          <li>ごうかい【豪快】ガウクワイ[0]<br>―な／―に スケールが大きくて、ほかに比べるものが無いという印象を与える様子。<br>――さ[0]<br></li>
          <li>ごう‐かい【豪快】ガウクワイ<br>堂々としていて、見て気持よいこと。「―な上手投げ」「―に飲む」<br></li>
          <li>ごうかい【豪快】<br>〘adj-na・n〙<br>hearty; exciting; stirring; lively; heroic; largehearted; splendid.</li>
          <li>ごうかい２【豪快】 [ローマ字](gōkai)<br>〜な stirring; splendid; glorious; thrilling; tremendous; magnificent; heroic 《endeavors》; big-hearted; animating; hearty 《laughter》.<br>►豪快な人物　a big-hearted person<br>・豪快なホームランを飛ばす　hit an enthralling [a glorious, a thrilling] home run<br>・豪快な取り口で相手を土俵にたたきつける　bring one's opponent down on to the ring with a ┏magnificent [heroic] attack.<br>豪快に　in a ┏stirring [thrilling] way; in an enthralling way; splendidly; gloriously; tremendously; magnificently; heroically; 《laugh》 heartily.<br>►豪快に笑い飛ばす　dismiss 《sb's fears》 with a hearty roar of laughter<br>・豪快に大杯を飲み干す　drink off a big glass heroically [without turning a hair].<br></li>
       </ol>
    </div>
     */
    fun mergeDefinitions(definitions: List<Definition>): List<Definition>{
        // TODO
        val definitionsHTMLString = convertDefinitionsToHTMLString(definitions)

        return definitions
    }

    fun convertDefinitionsToHTMLString(definitions: List<Definition>): String{
        for (definition in definitions){

        }

        return ""
    }

    companion object {
        private const val DECK_REF_DB = "com.ichi2.anki.api.decks"
        private const val MODEL_REF_DB = "com.ichi2.anki.api.models"

        /**
         * Whether or not the API is available to use.
         * The API could be unavailable if AnkiDroid is not installed or the user explicitly disabled the API
         * @return true if the API is available to use
         */
        fun isApiAvailable(context: Context?): Boolean {
            return AddContentApi.getAnkiDroidPackageName(context!!) != null
        }
    }
}