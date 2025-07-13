package com.example.thaitoanki.services.windows

import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.VIEW_MODEL_KEY
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.thaitoanki.R
import com.example.thaitoanki.data.network.ThaiLanguageRepository
import com.example.thaitoanki.data.database.WordsRepository
import com.example.thaitoanki.data.network.Definition
import com.example.thaitoanki.services.FloatingService
import com.example.thaitoanki.services.ServiceViewModelProvider
import com.example.thaitoanki.ui.AppViewModelProvider
import com.example.thaitoanki.ui.screens.FlashcardViewModel
import com.example.thaitoanki.ui.screens.ThaiViewModel
import com.example.thaitoanki.ui.screens.components.FLASHCARD_DUPLICATE_MESSAGE
import com.example.thaitoanki.ui.screens.components.FLASHCARD_FAILURE_MESSAGE
import com.example.thaitoanki.ui.screens.components.FLASHCARD_SUCCESS_MESSAGE
import com.example.thaitoanki.ui.screens.components.buildSection
import com.example.thaitoanki.ui.screens.components.updateFlashcardFrontView
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class FlashcardWindow(
    val word: String,
    context: ContextWrapper,
    override val serviceContext: Context,
    override val applicationContext: Context,
    val lifecycleScope: LifecycleCoroutineScope,
    val languageRepository: ThaiLanguageRepository,
    val wordsRepository: WordsRepository,
    override var onMinimize: () -> Unit,
    override val onClose: (Window) -> Unit
): Window(
    context = context,
    serviceContext = serviceContext,
    applicationContext = applicationContext,
    layoutId = R.layout.window_flashcard,
    windowWidth = 300,
    windowHeight = 400,
    onMinimize = onMinimize
) {
    // TODO: pass in viewModel instead?
    //val viewModel: ThaiViewModel = ThaiViewModel(languageRepository, wordsRepository)

    //val viewModel: FlashcardViewModel by viewModels { MyViewModel.Factory }

    // Use from ComponentActivity, Fragment, NavBackStackEntry,
    // or another ViewModelStoreOwner.
//    val viewModelStoreOwner: ViewModelStoreOwner = this
//    val myViewModel: MyViewModel = ViewModelProvider.create(
//        viewModelStoreOwner,
//        factory = MyViewModel.Factory,
//        extras = MutableCreationExtras().apply {
//            set(MyViewModel.MY_REPOSITORY_KEY, myRepository)
//        },
//    )[MyViewModel::class]

    val viewModelStoreOwner: ViewModelStoreOwner = this
    val flashcardViewModel: FlashcardViewModel = ViewModelProvider.create(
        viewModelStoreOwner,
        factory = ServiceViewModelProvider(serviceContext, applicationContext).Factory,
        extras = MutableCreationExtras().apply {
            set(SAVED_STATE_REGISTRY_OWNER_KEY, serviceContext as SavedStateRegistryOwner)
            set(VIEW_MODEL_STORE_OWNER_KEY, serviceContext as ViewModelStoreOwner)
            set(VIEW_MODEL_KEY, "Flashcard")


            val bundle = Bundle()
            bundle.putString("word", word) //"ขนม")
            set(DEFAULT_ARGS_KEY, bundle)
            //set(DEFAULT_ARGS_KEY, savedState())
        },
    )[FlashcardViewModel::class]

    // state variables
    var currentDefinitionIndex: Int = 0
    var currentExampleIndex: Int? = null
    var currentSentenceIndex: Int? = null

    /**
     * State manipulation functions
     */
    private fun increaseIndex(baseList: List<Any>, valueToChange: Int): Int {
        val maxIndex = baseList.size - 1
        var updatedIndex = valueToChange + 1
        if (updatedIndex > maxIndex) {
            updatedIndex = 0
        }
        return updatedIndex
    }

    private fun decreaseIndex(baseList: List<Any>, valueToChange: Int): Int {
        var updatedIndex = valueToChange - 1
        if (updatedIndex < 0) {
            val maxIndex = baseList.size - 1
            updatedIndex = maxIndex
        }
        return updatedIndex
    }

    private fun increaseCurrentDefinitionIndex(definitionList: List<Definition>){
        val updatedIndex =
            increaseIndex(definitionList, currentDefinitionIndex)
        currentDefinitionIndex = updatedIndex
//        currentDefinitionIndex += 1
//        if (currentDefinitionIndex >= definitionList.size){
//            currentDefinitionIndex = 0
//        }

        resetExampleSentenceIndices()

        setUpWindow() // reload the window view with the new configuration

        // TODO: reset exampleIndex and sentenceIndex to 0
    }

    private fun decreaseCurrentDefinitionIndex(definitionList: List<Definition>){
        val updatedIndex = decreaseIndex(definitionList, currentDefinitionIndex)
        currentDefinitionIndex = updatedIndex
//        currentDefinitionIndex -= 1
//        if (currentDefinitionIndex < 0){
//            val maxIndex = definitionList.size - 1
//            currentDefinitionIndex = maxIndex
//        }

        resetExampleSentenceIndices()

        setUpWindow() // reload the window view with the new configuration
    }

    private fun increaseCurrentExampleIndex(exampleList: List<Definition>){
        if (currentExampleIndex != null) {
            val updatedIndex = increaseIndex(exampleList, currentExampleIndex!!)
            currentExampleIndex = updatedIndex
        }

        setUpWindow()
    }

    private fun decreaseCurrentExampleIndex(exampleList: List<Definition>){
        if (currentExampleIndex != null) {
            val updatedIndex = decreaseIndex(exampleList, currentExampleIndex!!)
            currentExampleIndex = updatedIndex
        }

        setUpWindow()
    }

    private fun increaseCurrentSentenceIndex(sentenceList: List<Definition>){
        if (currentSentenceIndex != null) {
            val updatedIndex = increaseIndex(sentenceList, currentSentenceIndex!!)
            currentSentenceIndex = updatedIndex
        }

        setUpWindow()
    }

    private fun decreaseCurrentSentenceIndex(sentenceList: List<Definition>){
        if (currentSentenceIndex != null) {
            val updatedIndex = decreaseIndex(sentenceList, currentSentenceIndex!!)
            currentSentenceIndex = updatedIndex
        }

        setUpWindow()
    }

    private fun resetExampleSentenceIndices(){
        currentExampleIndex = null
        currentSentenceIndex = null
    }

    // NOTE: initWindow was running before the viewModel was generated, so a separate function was created
    //override fun initWindow() {
    fun setUpWindow(){
        // super init
        super.initWindow()

        // Save button
        val saveButtonView = rootView.findViewById<ImageButton>(R.id.save_button)
        //change view color
        ImageViewCompat.setImageTintList(saveButtonView, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_theme_background)));

        // Counter
        val counterView = rootView.findViewById<CardView>(R.id.counter)
        val counterTextView = counterView.getChildAt(0) as TextView
        //change view color
        counterTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.md_theme_background))

        // TODO: flow isn't collecting anything. Try to make a direct query for words
        // Actually, it seems like the flow is only collecting after running this setUpWindow() function
        // init + initWindow() -> setUpWindow() -> flow collection
        lifecycleScope.launch {
            flashcardViewModel.definitionsState.collect { definitionList ->
                // Update View with the latest defns
                Log.d("FlashcardWindow collection", definitionList.toString())

                if(definitionList.isNotEmpty()) {
                    /*
2025-01-26 14:27:21.051 11435-11435 FlashcardW...collection com.example.thaitoanki               D  []
2025-01-26 14:27:21.053 11435-11435 FlashcardWindow         com.example.thaitoanki               D  ขนม
2025-01-26 14:27:21.108 11435-11435 FlashcardW...collection com.example.thaitoanki               D  [Definition(baseWord=ขนม, definition=dessert; sweets; cake, romanization=kha<sup>L</sup> nohm<sup>R</sup>, pronunciation=ขะ-หฺนม, partOfSpeech=[noun], etymology=, classifiers=[Definition(baseWord=ชิ้น , definition=[numerical classifier for pieces of food; for a piece of writing; pieces of work], romanization=chin<sup>H</sup>, pronunciation=, partOfSpeech=, etymology=, classifiers=[], components=[], synonyms=[], relatedWords=[], examples=[], sentences=[], wordId=null), Definition(baseWord=ลูก , definition=[numerical classifier for children, round fruit, balls, midsized hard round objects, and storms], romanization=luuk<sup>F</sup>, pronunciation=, partOfSpeech=, etymology=, classifiers=[], components=[], synonyms=[], relatedWords=[], examples=[], sentences=[], wordId=null)], components=[], synonyms=[], relatedWords=[Definition(baseWord=ของหวาน, definition=[colloquial] sweets; desserts, romanization=khaawng<sup>R</sup> waan<sup>R</sup>, pronunciation=, partOfSpeech=, etymology=, classifiers=[], components=[], synonyms=[], relatedWords=[], examples=[], sentences=[], wordId=null), Definition(baseWord=อาหารหวาน, definition=[formal term] sweets; desserts, romanization=aa<sup>M</sup> haan<sup>R</sup> waan<sup>R</sup>, pronunciation=, partOfSpeech=, etymology=, classifiers=[], components=[], synonyms=[], relatedWords=[], examples=[], sentences=[], wordId=null)], examples=[], sentences=[Definition(baseWord=ซื้อขนมมา, definition="(I've) been buying sweets.", romanization=seuu<sup>H</sup> kha<sup>L</sup> nohm<sup>R</sup> maa<sup>M</sup>, pronunciation=, partOfSpeech=, etymology=, classifiers=[], components=[], synonyms=[], relatedWords=[], examples=[], sentences=[], wordId=null), Definition(baseWord=เด็ก ๆ ชอบใจเมื่อได้รับขนมและลูกกวาดจากคุณครู, definition="The children were very happy when they received cakes and candy from their teacher.", romanization=dek<sup>L</sup> dek<sup>L</sup> chaawp<sup>F</sup> jai<sup>M</sup> meuua<sup>F</sup> dai<sup>F</sup> rap<sup>H</sup> kha<sup>L</sup> nohm<sup>R</sup> lae<sup>H</sup> luuk<sup>F</sup> gwaat<sup>L</sup> jaak<sup>L</sup> khoon<sup>M</sup> khruu<sup>M</sup>, pronunciation=, partOfSpeech=, etymology=, classifiers=[], components=[], synonyms=[], relatedWords=[], examples=[], sentences=[], wordId=null), Definition(baseWord=เด็ก ๆ วิ่งเล่นส่งเสียงกรี๊ดกร๊าดก่อนมารุมล้อมโต๊ะอันเพียบไปด้วยอาหารและขนม, definition="The children ran around screaming before they surrounded the table chock full of food and sweets.", romanization=dek<sup>L</sup> dek<sup>L</sup> wing<sup>F</sup> len<sup>F</sup> sohng<sup>L</sup> siiang<sup>R</sup> greet<sup>H</sup> graat<sup>H</sup> gaawn<sup>L</sup> maa<sup>M</sup> room<sup>M</sup> laawm<sup>H</sup> dto<sup>H</sup> an<sup>M</sup> phiiap<sup>F</sup> bpai<sup>M</sup> duay<sup>F</sup> aa<sup>M</sup> haan<sup>R</sup> lae<sup>H</sup> kha<sup>L</sup> nohm<sup>R</sup>, pronunciation=, partOfSpeech=, etymology=, classifiers=[], components=[], synonyms=[], relatedWords=[], examples=[], sentences=[], wordId=null), Definition(baseWord=๘. อย่าเดินกินขนม หรืออาหาร, definition="8. Do not walk while eating.", romanization=bpaaet<sup>L</sup> yaa<sup>L</sup> deern<sup>M</sup> gin<sup>M</sup> kha<sup>L</sup> nohm<sup>R</sup> reuu<sup>R</sup> aa<sup>M</sup> haan<sup>R</sup>, pronunciation=, partOfSpeech=, etymology=, classifiers=[], components=[], synonyms=[], relatedWords=[], examples=[], sentences=[], wordId=null), Definition(baseWord=แม่กับลูกกำลังเพลินกับการทำขนมอยู่ในครัวโน่น, definition="The mother and her [daughter] are having a good time making sweets over there in their kitc
2025-01-26 14:27:21.108 11435-11435 FlashcardWindow         com.example.thaitoanki               D  ขนม
                     */

                    // this part is working
//                    val titleView = rootView.findViewById<TextView>(R.id.word)
//                    Log.d("FlashcardWindow", def[0].baseWord)
//                    //temp
//                    titleView.text = def[0].definition

                    val currentFlashcard = definitionList[currentDefinitionIndex]

                    if (currentFlashcard.examples.isNotEmpty() && currentExampleIndex == null){
                        currentExampleIndex = 0
                    }

                    if (currentFlashcard.sentences.isNotEmpty() && currentSentenceIndex == null){
                        currentSentenceIndex = 0
                    }

                    updateFlashcardFrontView(
                        view = rootView,
                        currentDefinitionIndex = currentDefinitionIndex,
                        definitionCount = definitionList.size,
                        currentFlashcard = currentFlashcard, //def[flashcardViewModel.uiState.value.currentDefinitionIndex],
                        currentDefinitionExampleIndex = currentExampleIndex,
                        currentDefinitionSentenceIndex = currentSentenceIndex,
                        onClick = {
                            // in this function, manually modify the results as desired by selecting the view

                            // at the end, update the data in the flashcard view model manually, i.e. flashcardViewModel.updateIndex(), etc.

                            // or turn this into a lifecycleOwner?
                            // https://developer.android.com/topic/libraries/architecture/lifecycle
                            // manual lifecycle management would help with opening and closing windows




                            // unsure why the uiState and _uiState isn't changed with this command. All parts remain uninitialized
//                            flashcardViewModel.increaseCurrentDefinitionIndex()
//                            Log.d("flashcardwindow",
//                                flashcardViewModel.uiState.value.currentDefinitionIndex.toString()
//                            )

                            increaseCurrentDefinitionIndex(definitionList)



                            // change title
//                            val titleTextView = rootView.findViewById<TextView>(R.id.word)
//                            // TODO: temporarily changing card on title click
//                            titleTextView.setOnClickListener(){
//
//                                // change th
//                                onClick()
//                            }

                            // change definition
//                            val definitionSectionViewId = R.id.definition_container
//                            buildSection(rootView,
//                                sectionInfo = currentFlashcard.definition.toList(),
//                                containerId = definitionSectionViewId,
//                                build = {
//                                    // combine part of speech and definition
//                                    val definitionTextView = rootView.findViewById<TextView>(R.id.partOfSpeechAndDefinition)
//                                    val partOfSpeech: String = if (currentFlashcard.partOfSpeech.isEmpty()) "" else currentFlashcard.partOfSpeech + " "
//                                    val displayText: String = partOfSpeech + currentFlashcard.definition
//                                    definitionTextView.text = displayText
//                                })
                        },
                        onLeftClick = {
                            decreaseCurrentDefinitionIndex(definitionList)
                        },
                        onRightClick = {
                            increaseCurrentDefinitionIndex(definitionList)
                        },
                        onExampleClick = {
                            increaseCurrentExampleIndex(currentFlashcard.examples)
                        },
                        onSentenceClick = {
                            increaseCurrentSentenceIndex(currentFlashcard.sentences)
                        }
                    )

                    // set button
//                    val buttonView = Button(applicationContext)
//                    rootView.addView(buttonView)
//                    rootView.invalidate()
                    rootView.findViewById<View>(R.id.save_button).setOnClickListener{
                        val responseCode = flashcardViewModel.saveCard(
                            context = applicationContext,
                            flashcardData = definitionList[currentDefinitionIndex],
                            exampleIndex = currentExampleIndex,
                            sentenceIndex = currentSentenceIndex
                        )

                        if (responseCode > 0){
                            Toast.makeText(
                                serviceContext,
                                FLASHCARD_SUCCESS_MESSAGE,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else if (responseCode < -2){
                            Toast.makeText(
                                serviceContext,
                                FLASHCARD_DUPLICATE_MESSAGE,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else{
                            Toast.makeText(
                                serviceContext,
                                FLASHCARD_FAILURE_MESSAGE,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

//            val flashcardDefinitions = flashcardViewModel.definitionsState.value
//            val flashcardUiState = flashcardViewModel.uiState.value
//
//            // using the retrieved definition data from the repository, set the flashcardUiState
//            flashcardViewModel.updateDefinitions(flashcardDefinitions)
//            if(flashcardUiState.currentExampleIndices.isEmpty()){
//                flashcardViewModel.initializeExampleIndices()
//            }
//            if(flashcardUiState.currentSentenceIndices.isEmpty()){
//                flashcardViewModel.initializeSentenceIndices()
//            }
        }

//        val titleView = rootView.findViewById<TextView>(R.id.word)
//        Log.d("FlashcardWindow", flashcardViewModel.word)
//        //temp
//        titleView.text = flashcardViewModel.word




        //titleView.text = viewModel.word

//        rootView.findViewById<View>(R.id.search_button).setOnClickListener {
//            with(rootView.findViewById<EditText>(R.id.search_input)) {
//                //db.insert(text.toString(), true)
//
//                //wordsRepository.insertWord(text.toString())
//
//                lifecycleScope.launch {
//
//                }
//            }
//        }

    }


    init {
        //initWindowParams()
        //initWindow()
    }

    override fun close() {
        //viewModelStore.clear()

        // TODO: move this to the base Window class?
        // clear the viewModelStore that the Service acts as.
        // will clear out the SavedStateHandle, allow us to pass a different 'word' every time
        /*
        private fun createSavedStateHandle(
            savedStateRegistryOwner: SavedStateRegistryOwner,
            viewModelStoreOwner: ViewModelStoreOwner,
            key: String,
            defaultArgs: Bundle?
        ): SavedStateHandle {
            val provider = savedStateRegistryOwner.savedStateHandlesProvider
            val viewModel = viewModelStoreOwner.savedStateHandlesVM
            // If we already have a reference to a previously created SavedStateHandle
            // for a given key stored in our ViewModel, use that. Otherwise, create
            // a new SavedStateHandle, providing it any restored state we might have saved
            return viewModel.handles[key] ?: SavedStateHandle.createHandle(
                provider.consumeRestoredStateForKey(key), defaultArgs
            ).also { viewModel.handles[key] = it }
        }
         */
        (serviceContext as ViewModelStoreOwner).viewModelStore.clear() //.savedStateHandlesVM

        super.close()

        // clear flashcard view model
//        for (key in viewModelStore.) {
//            Log.d("FlashcardWindow: ", viewModelStore.)
//        }
    }
}