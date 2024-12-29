package com.example.thaitoanki

import android.app.Application
import com.example.thaitoanki.data.AppContainer
import com.example.thaitoanki.data.DefaultAppContainer

class ThaiLanguageApplication : Application() {

    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }

}