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
import com.example.thaitoanki.ui.screens.components.updateDefinitionListView
import com.example.thaitoanki.ui.screens.components.updateFlashcardFrontView
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class DefinitionListWindow(
    val definitions: List<Definition>,
    context: ContextWrapper,
    override val serviceContext: Context,
    override val applicationContext: Context,
    val lifecycleScope: LifecycleCoroutineScope,
    val languageRepository: ThaiLanguageRepository,
    val wordsRepository: WordsRepository,
    val onDefinitionClick: (DefinitionListWindow, Int) -> Unit,
    override var onMinimize: () -> Unit,
    override val onClose: (Window) -> Unit
): Window(
    context = context,
    serviceContext = serviceContext,
    applicationContext = applicationContext,
    layoutId = R.layout.window_definition_list,
    windowWidth = 300,
    windowHeight = 400,
    onMinimize = onMinimize
) {

//    val viewModelStoreOwner: ViewModelStoreOwner = this
//    val flashcardViewModel: FlashcardViewModel = ViewModelProvider.create(
//        viewModelStoreOwner,
//        factory = ServiceViewModelProvider(serviceContext, applicationContext).Factory,
//        extras = MutableCreationExtras().apply {
//            set(SAVED_STATE_REGISTRY_OWNER_KEY, serviceContext as SavedStateRegistryOwner)
//            set(VIEW_MODEL_STORE_OWNER_KEY, serviceContext as ViewModelStoreOwner)
//            set(VIEW_MODEL_KEY, "Flashcard")
//
//
//            val bundle = Bundle()
//            bundle.putString("word", word) //"ขนม")
//            set(DEFAULT_ARGS_KEY, bundle)
//            //set(DEFAULT_ARGS_KEY, savedState())
//        },
//    )[FlashcardViewModel::class]

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


    // NOTE: initWindow was running before the viewModel was generated, so a separate function was created
    //override fun initWindow() {
    fun setUpWindow(){
        // super init
        super.initWindow()

        updateDefinitionListView(rootView, definitions,
            definitionBlockOnClick = { index ->
                // pass back the current window and the index
                onDefinitionClick(this, index)
            })

    }


    init {
        //initWindowParams()
        //initWindow()

        //updateDefinitionListView(rootView, definitions)

    }

    override fun close() {

        (serviceContext as ViewModelStoreOwner).viewModelStore.clear() //.savedStateHandlesVM

        super.close()
    }
}