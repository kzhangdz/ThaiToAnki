package com.example.thaitoanki.services.windows

import android.content.Context
import android.view.ContextThemeWrapper
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.example.thaitoanki.R
import com.example.thaitoanki.data.DefaultAppContainer

class WindowGroup(
    val serviceContext: Context,
    val applicationContext: Context,
    val lifecycleScope: LifecycleCoroutineScope,
) {

    val container = DefaultAppContainer(applicationContext)
    val languageRepo = container.thaiLanguageRepository //ThaiLanguageApplication().container.thaiLanguageRepository
    val wordRepo = container.wordsRepository

    var openedWindows: List<Window> = mutableListOf()

    fun start(){

        val window = SearchWindow(
            ContextThemeWrapper(serviceContext, R.style.Theme_ThaiToAnki),
            serviceContext = serviceContext,
            applicationContext = applicationContext,
            lifecycleScope = lifecycleScope,
            languageRepository = languageRepo,
            wordsRepository = wordRepo
        )
        window.open()

    }
}