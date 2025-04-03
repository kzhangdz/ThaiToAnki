package com.example.thaitoanki.services.windows

import android.content.Context
import android.view.ContextThemeWrapper
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.example.thaitoanki.R
import com.example.thaitoanki.data.DefaultAppContainer

/**
 * Class to handle window opening and closing
 */

class WindowGroup(
    val serviceContext: Context,
    val applicationContext: Context,
    val lifecycleScope: LifecycleCoroutineScope,
) {

    val container = DefaultAppContainer(applicationContext)
    val languageRepo = container.thaiLanguageRepository //ThaiLanguageApplication().container.thaiLanguageRepository
    val wordRepo = container.wordsRepository

    var windows: MutableList<Window> = mutableListOf()

    fun start(){

        val window = SearchWindow(
            ContextThemeWrapper(serviceContext, R.style.Theme_ThaiToAnki),
            serviceContext = serviceContext,
            applicationContext = applicationContext,
            lifecycleScope = lifecycleScope,
            languageRepository = languageRepo,
            wordsRepository = wordRepo
        )
        window.onSearchCompleted = {
            val searchValue = window.getSearchValue()

            val flashcardWindow = FlashcardWindow(
                searchValue,
                ContextThemeWrapper(serviceContext, R.style.Theme_ThaiToAnki),
                serviceContext,
                applicationContext,
                lifecycleScope = lifecycleScope,
                languageRepository = languageRepo,
                wordsRepository = wordRepo
            )
            flashcardWindow.setUpWindow()
            flashcardWindow.open()
            windows.add(flashcardWindow)
        }
        window.onMinimize = {
            minimize()
        }

        window.open()
        windows.add(window)

    }

    // TODO: pass an onMinimize() to all windows
    // here, iterate through the openedWindows and trigger hide() for each one then. bring up the MinimizedWindow
    // also keep a list of hiddenWindows

    // need a way to keep track of windows closing. Does that need to be another callback function?
    // onClose() and onMinimize()
    // onMinimize() for each window will call this.minimize() (windowGroup is the context)
    fun minimize(){
        for(window in windows){
            window.hide()
        }

        val minimizedWindow = MinimizedWindow(
            ContextThemeWrapper(serviceContext, R.style.Theme_ThaiToAnki),
            serviceContext,
            applicationContext,
        )
        minimizedWindow.open()
    }
}