package com.example.thaitoanki.services.windows

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.thaitoanki.R
import com.example.thaitoanki.data.network.ThaiLanguageRepository
import com.example.thaitoanki.data.database.WordsRepository
import com.example.thaitoanki.services.ServiceViewModelProvider
import com.example.thaitoanki.ui.screens.ThaiViewModel
import kotlinx.coroutines.launch


// pass in viewModel that was initialized by the ViewModelFactory, which we used in the Service.
// The service can use the viewModel because it is a ViewModelStoreOwner

class SearchWindow(
    context: ContextWrapper,
    override val serviceContext: Context,
    override val applicationContext: Context,
    val lifecycleScope: LifecycleCoroutineScope,
    val languageRepository: ThaiLanguageRepository,
    val wordsRepository: WordsRepository
): Window(
    context = context,
    serviceContext = serviceContext,
    applicationContext = applicationContext,
    layoutId = R.layout.window_search,
    windowWidth = 300,
    windowHeight = 80
) {

    // TODO: pass in viewModel instead?
    //val viewModel: ThaiViewModel = ThaiViewModel(languageRepository, wordsRepository)

    val viewModelStoreOwner: ViewModelStoreOwner = this
    val viewModel: ThaiViewModel = ViewModelProvider.create(
        viewModelStoreOwner,
        factory = ServiceViewModelProvider(serviceContext, applicationContext).Factory,
//        extras = MutableCreationExtras().apply {
//            set(ThaiViewModel.MY_REPOSITORY_KEY, myRepository)
//        },
    )[ThaiViewModel::class]

    override fun initWindow() {
        // super init for window commands, like closing and minimizing
        super.initWindow()


        // Create menu https://developer.android.com/develop/ui/views/components/menus
        // Paste data https://developer.android.com/develop/ui/views/touch-and-input/copy-paste
        rootView.findViewById<EditText>(R.id.search_input).setOnLongClickListener {
            var clipboard = applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var pasteData: String = ""

            val item: ClipData.Item? = clipboard.primaryClip?.getItemAt(0)

            if(item != null){
                // Gets the clipboard as text.
                pasteData = item.text.toString()
                Log.d("Paste Data", pasteData)

                val editTextView = rootView.findViewById<EditText>(R.id.search_input)
                editTextView.setText(pasteData)
                // set cursor at end
                editTextView.setSelection(editTextView.length())

                true
            }
            else{
                false
            }
        }

        rootView.findViewById<EditText>(R.id.search_input).setOnEditorActionListener { _, actionId, _ ->
            var handled: Boolean = false

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search()

                disableKeyboard()

                handled = true
            }

            return@setOnEditorActionListener handled
        }

        rootView.findViewById<View>(R.id.search_button).setOnClickListener {
            search()
        }
    }

    private fun search(){
        with(rootView.findViewById<EditText>(R.id.search_input)) {
            //db.insert(text.toString(), true)

            //wordsRepository.insertWord(text.toString())

            lifecycleScope.launch {
                //val results = languageRepository.searchDictionary(text.toString())
                //Log.d("Service", results.html())
                val searchValue = text.toString()
                viewModel.updateSearchValue(searchValue)

                // TODO: need to add a version of search Dictionary that suspends and does not start another coroutine
                //viewModel.searchDictionary()

                viewModel.sendDictionaryQuery()

                // test
                //close()
                //open()

                setText("")

                //SavedStateHandle().set("word", searchValue)

                val flashcardWindow = FlashcardWindow(
                    searchValue,
                    ContextThemeWrapper(context, R.style.Theme_ThaiToAnki),
                    serviceContext,
                    applicationContext,
                    lifecycleScope = lifecycleScope,
                    languageRepository = languageRepository,
                    wordsRepository = wordsRepository
                )
                flashcardWindow.setUpWindow()
                flashcardWindow.open()

                // closing and opening the window affected the search insertion somehow
                // rather than closing the window, should I move it offscreen and show another one?

                // have the definition window open off screen?
                // then after a word is saved, use a broadcast receiver notify the definition window and retrieve definitions?
                // try to do it by collecting from the flow first

                // I think I can open multiple windows. max is 300
                // can probably put it directly over the searchWindow's

                // next step for now is to make an inheritable window

                // An overlay is just a layout, without an underlying activity



                // Can I actually just do an overlay activity instead?
                // This link uses an intent to start a second activity that's based in a window manager
                // https://stackoverflow.com/questions/18843868/android-overlay-on-window-over-activities-of-a-task
            }
        }
    }

    init {
        //initWindowParams()
        initWindow()
    }
}