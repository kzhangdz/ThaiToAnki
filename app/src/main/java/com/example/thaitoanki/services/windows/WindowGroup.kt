package com.example.thaitoanki.services.windows

import android.content.Context
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.thaitoanki.R
import com.example.thaitoanki.data.DefaultAppContainer

/**
 * Class to handle window opening and closing
 */

class WindowGroup(
    val serviceContext: Context,
    val applicationContext: Context,
    val lifecycleScope: LifecycleCoroutineScope,
    val onClose: () -> Unit
) {

    val container = DefaultAppContainer(applicationContext)
    val languageRepo = container.thaiLanguageRepository //ThaiLanguageApplication().container.thaiLanguageRepository
    val wordRepo = container.wordsRepository

    var windows: MutableList<Window> = mutableListOf()

    // openNextWindow() function that takes the window type as a parameter?

    fun start(){

        val window = SearchWindow(
            ContextThemeWrapper(serviceContext, R.style.Theme_ThaiToAnki),
            serviceContext = serviceContext,
            applicationContext = applicationContext,
            lifecycleScope = lifecycleScope,
            languageRepository = languageRepo,
            wordsRepository = wordRepo,
            onSearchCompleted = { searchValue ->
                // searchValue needs to be the returned parameters searchValue ->

                // val searchValue = window.getSearchValue()

                val flashcardWindow = FlashcardWindow(
                    searchValue,
                    ContextThemeWrapper(serviceContext, R.style.Theme_ThaiToAnki),
                    serviceContext,
                    applicationContext,
                    lifecycleScope = lifecycleScope,
                    languageRepository = languageRepo,
                    wordsRepository = wordRepo,
                    onDefinitionSectionClick = { currentFlashcardWindow ->
                        val definitions = if (currentFlashcardWindow.definitions != null) currentFlashcardWindow.definitions else emptyList()

                        Log.d("definitions", definitions.toString())

                        if (definitions != null) {
                            val definitionListWindow = DefinitionListWindow(
                                definitions = definitions,
                                context = ContextThemeWrapper(
                                    serviceContext,
                                    R.style.Theme_ThaiToAnki
                                ),
                                serviceContext = serviceContext,
                                applicationContext = applicationContext,
                                lifecycleScope = lifecycleScope,
                                languageRepository = languageRepo,
                                wordsRepository = wordRepo,
                                onDefinitionClick = { currentWindow, index ->
                                    // move the flashcard window to the selected index
                                    currentFlashcardWindow.currentDefinitionIndex = index
                                    currentFlashcardWindow.setUpWindow()

                                    // close the definition block window
                                    Log.d("WindowGroup", "CurrentWindow: ${currentWindow.toString()}")
                                    currentWindow.close()
                                },
                                onMinimize = {
                                    minimize()
                                },
                                onClose = { closedWindow ->
                                    close(closedWindow)
                                }
                            )

                            // TODO: hide flashcard window
                            definitionListWindow.setUpWindow()
                            definitionListWindow.open()
                            windows.add(definitionListWindow)
                            Log.d("WindowGroup", windows.toString())
                        }
                    },
                    onMinimize = {
                        minimize()
                    },
                    onClose = { closedWindow ->
                        close(closedWindow)
                    }
                )
                flashcardWindow.setUpWindow()
                flashcardWindow.open()
                windows.add(flashcardWindow)
            },
            onMinimize = {
                minimize()
            },
            onClose = { closedWindow ->
                close(closedWindow)
            }
        )
//        window.onMinimize = {
//            minimize()
//        }

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

        // hold the y coordinates of the last window in the list
        var lastWindowY = 0

        for(i in 0..<windows.size){
            val window = windows[i]
            window.hide()

            // If last index, get the height
            if (i == windows.size - 1){
                lastWindowY = window.windowParams.y
            }
        }

        val minimizedWindow = MinimizedWindow(
            ContextThemeWrapper(serviceContext, R.style.Theme_ThaiToAnki),
            serviceContext,
            applicationContext,
            onClick = { minWindow ->
                for (window in windows) {
                    window.reveal()
                }
                minWindow.close()
            },
            startingX = 0,
            startingY = 500
        )
        minimizedWindow.open()
    }

    fun close(closedWindow: Window){
        for(i in 0..<windows.size){
            val window = windows[i]
            // if the windows match, remove it from the windows list
            if(window === closedWindow){
                windows.removeAt(i)
                break
            }
        }

        // TODO: if there are no windows remaining, should I remove this object from memory?
        if (windows.isEmpty()){
            onClose()
        }

        Log.d("WindowGroup", windows.toString())
    }
}