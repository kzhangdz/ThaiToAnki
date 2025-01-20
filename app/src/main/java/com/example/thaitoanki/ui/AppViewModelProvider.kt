/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.thaitoanki.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.thaitoanki.ThaiLanguageApplication
import com.example.thaitoanki.data.DefaultAppContainer
import com.example.thaitoanki.ui.screens.FlashcardViewModel
import com.example.thaitoanki.ui.screens.HistoryViewModel
import com.example.thaitoanki.ui.screens.ThaiViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire app
 * can't directly pass in params to a view model, so we use this factory
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for ThaiViewModel
        initializer {
            val thaiLanguageRepository = thaiLanguageApplication().container.thaiLanguageRepository
            val wordsRepository = thaiLanguageApplication().container.wordsRepository
            ThaiViewModel(thaiLanguageRepository, wordsRepository)
        }
        // Initializer for FlashcardViewModel
        initializer {
            val wordsRepository = thaiLanguageApplication().container.wordsRepository
            FlashcardViewModel(this.createSavedStateHandle(), wordsRepository)
        }
        // Initializer for HistoryViewModel
        initializer {
            val wordsRepository = thaiLanguageApplication().container.wordsRepository
            HistoryViewModel(wordsRepository)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [ThaiLanguageApplication].
 */
fun CreationExtras.thaiLanguageApplication(): ThaiLanguageApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ThaiLanguageApplication)
