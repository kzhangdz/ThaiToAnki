package com.example.thaitoanki.ui.screens.components

import android.content.Context
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
            updateFlashcardFrontView(view, currentFlashcard)
        }
    )
}

// TODO: move these functions out if needed in the service
fun updateFlashcardFrontView(view: View, currentFlashcard: Definition){
    // variables used for adding views to sections
    val context = view.context.applicationContext
    val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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
//    val classifierSectionViewId = R.id.classifier_container
//    buildSection(view,
//        sectionInfo = currentFlashcard.classifiers,
//        containerId = classifierSectionViewId,
//        build = {
//
//        })

    // components
    val componentsSectionViewId = R.id.components_container
    buildSection(view,
        sectionInfo = currentFlashcard.components,
        containerId = componentsSectionViewId,
        build = {
            val parent = view.findViewById<LinearLayout>(R.id.components_content)

            for (i in 0..<currentFlashcard.components.size){
                //val newView = View()
                //parent.addView(R.layout.fragment_pill)
                //val pill = layoutInflater.inflate(R.layout.fragment_pill, parent) as Button

                // TODO: might need an arrayAdapter for this to work
                // Might also need to switch to ListView
                val pill = View.inflate(context, R.layout.fragment_pill, parent) as ViewGroup

                Log.d("test", pill.getChildAt(0).toString())
                //https://stackoverflow.com/questions/8395168/android-get-children-inside-a-view

//                val pillButton = view.findViewById<Button>(R.id.pill)
//                pillButton.text = "test"
//
//                view.findView

                // set the id
                //pill.setTag(i)
                //pill.id = View.generateViewId()

                // adjust the text inside
                //pill.text = "text"//(currentFlashcard.components[i].baseWord)
            }

//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            ViewGroup parent = (ViewGroup)findViewById(R.id.where_you_want_to_insert);
//            inflater.inflate(R.layout.the_child_view, parent);
        })

    // synonyms

    // related words

    // examples
    val exampleSectionViewId = R.id.examples_container
    buildSection(view,
        sectionInfo = currentFlashcard.examples,
        containerId = exampleSectionViewId,
        build = {
            val parent = view.findViewById<LinearLayout>(R.id.examples_section)


            val newView = layoutInflater.inflate(R.layout.fragment_pill, parent)

//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            ViewGroup parent = (ViewGroup)findViewById(R.id.where_you_want_to_insert);
//            inflater.inflate(R.layout.the_child_view, parent);
        })

    // sentences

    // reference
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
            currentDefinitionIndex = 1,
        )
    }
}