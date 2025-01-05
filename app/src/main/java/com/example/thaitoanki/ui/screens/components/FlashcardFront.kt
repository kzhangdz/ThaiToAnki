package com.example.thaitoanki.ui.screens.components

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.thaitoanki.R
import com.example.thaitoanki.data.network.Definition
import com.example.thaitoanki.data.network.TestDefinitions
import com.example.thaitoanki.ui.theme.ThaiToAnkiTheme


@Composable
fun FlashcardFront(
    flashcardInfo: List<Definition>,
    currentDefinitionIndex: Int,
    modifier: Modifier = Modifier
){
    val currentFlashcard = flashcardInfo[currentDefinitionIndex]
    
    AndroidView(
        factory = { context ->
            View.inflate(context, R.layout.fragment_flashcard_front, null)
        },
        modifier = modifier,
        update = { view ->
            // set the header text with word info
            // word
            val titleTextView = view.findViewById<TextView>(R.id.word)
            titleTextView.setText(currentFlashcard.baseWord)

            // pronunciation
            val pronunciationTextView = view.findViewById<TextView>(R.id.pronunciation)
            pronunciationTextView.setText(R.string.pronunciation_label)

            // onClickListener

            // romanization

            
            // information sections
            // partOfSpeech
            val partOfSpeechTextView = view.findViewById<TextView>(R.id.partOfSpeech)
            if (currentFlashcard.partOfSpeech.isNotEmpty()){
                partOfSpeechTextView.setText(currentFlashcard.partOfSpeech)
            }
            else{
                val definitionSectionView: View = view.findViewById<LinearLayout>(R.id.definition_container)
                (definitionSectionView.getParent() as ViewGroup).removeView(definitionSectionView)
            }

            // definition
            val definitionTextView = view.findViewById<TextView>(R.id.definition)
            if (currentFlashcard.definition.isNotEmpty()){
                definitionTextView.setText(currentFlashcard.definition)
            }
            else{
                val definitionSectionView: View = view.findViewById<LinearLayout>(R.id.definition_container)
                (definitionSectionView.getParent() as ViewGroup).removeView(definitionSectionView)
            }

            // classifier

            // components

            // synonyms

            // related words

            // examples

            // sentences

            // reference
        }
    )
}

// display or hide sections of the flashcard
fun buildSection(view: View, sectionInfo: List<Any>, @IdRes containerId: Int, build: () -> Unit){
    if(sectionInfo.isNotEmpty()){
        build()
    }
    else{
        val containerView: View = view.findViewById(containerId)
        (containerView.getParent() as ViewGroup).removeView(containerView)
    }
}

@Preview(showBackground = true)
@Composable
fun FlashcardFrontPreview() {
    ThaiToAnkiTheme(
        darkTheme = false
    ) {
        FlashcardFront(
            flashcardInfo = TestDefinitions.definitions,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
                .verticalScroll(rememberScrollState()),
            currentDefinitionIndex = 0,
        )
    }
}