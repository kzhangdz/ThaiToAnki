package com.example.thaitoanki.services.windows

import android.content.Context
import android.content.ContextWrapper
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thaitoanki.R
import com.example.thaitoanki.data.ThaiLanguageRepository
import com.example.thaitoanki.data.database.WordsRepository
import com.example.thaitoanki.services.registerDraggableTouchListener
import com.example.thaitoanki.ui.AppViewModelProvider
import com.example.thaitoanki.ui.screens.FlashcardViewModel
import com.example.thaitoanki.ui.screens.ThaiViewModel
import kotlinx.coroutines.launch


class FlashcardWindow(
    context: ContextWrapper,
    applicationContext: Context,
    val lifecycleScope: LifecycleCoroutineScope,
    val languageRepository: ThaiLanguageRepository,
    val wordsRepository: WordsRepository
): Window(
    context = context,
    applicationContext = applicationContext,
    layoutId = R.layout.window_flashcard
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

    override fun initWindow() {
        // super init
        super.initWindow()

        AppViewModelProvider.Factory

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
        initWindow()
    }
}