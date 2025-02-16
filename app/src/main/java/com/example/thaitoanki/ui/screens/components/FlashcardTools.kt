package com.example.thaitoanki.ui.screens.components

import android.content.Context
import android.graphics.Typeface
import android.text.Html
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.toSpannable
import androidx.core.text.toSpanned
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thaitoanki.R
import com.example.thaitoanki.data.HTMLFormatting
import com.example.thaitoanki.data.network.Definition
import com.example.thaitoanki.ui.screens.adapters.PillListAdapter

// TODO: move these functions out if needed in the service
fun updateFlashcardFrontView(
    view: View,
    currentDefinitionIndex: Int,
    definitionCount: Int,
    currentFlashcard: Definition,
    currentDefinitionExampleIndex: Int?,
    currentDefinitionSentenceIndex: Int?,
    onClick: () -> Unit,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    onExampleClick: () -> Unit,
    onSentenceClick: () -> Unit,
){
    // variables used for adding views to sections
    val context = view.context.applicationContext
    val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater



    val flashcardView = view.findViewById<CardView>(R.id.flashcard)

    // not working
    // I think it's because on the blue architecture view, it only has a small outline exposed
    flashcardView.setOnClickListener(){
        Log.d("flashcard", "clicking works")
    }

    /**
     * Header section
     *
     */

    // set the header text with word info
    // word
    val titleTextView = view.findViewById<TextView>(R.id.word)
    titleTextView.setText(currentFlashcard.baseWord)
    // TODO: temporarily changing card on title click
    titleTextView.setOnClickListener(){

        // update the index by one
        onClick()
    }

    // Counter
    val counterView = view.findViewById<CardView>(R.id.counter)
    val counterTextView = counterView.getChildAt(0) as TextView
    val counterText = if(definitionCount > 0) "${currentDefinitionIndex + 1}/${definitionCount}" else "0/0"
    counterTextView.setText(counterText)
    //change view color
    counterTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.md_theme_background))

    // Left Button
    val leftView = view.findViewById<ImageButton>(R.id.left_button)
    leftView.setOnClickListener(){
        onLeftClick()
    }

    // Right Button
    val rightView = view.findViewById<ImageButton>(R.id.right_button)
    rightView.setOnClickListener(){
        onRightClick()
    }

    // pronunciation
    val pronunciationSectionViewId = R.id.pronunciation
    buildSection(view,
        sectionInfo = currentFlashcard.pronunciation.toList(),
        containerId = pronunciationSectionViewId,
        build = {
            buildHeaderClickableTextSection(
                view = view,
                context = context,
                textViewId = pronunciationSectionViewId,
                textLabelId = R.string.pronunciation_label,
                data = currentFlashcard.pronunciation
            )
        })

    // romanization
    val romanizationSectionViewId = R.id.romanization
    buildSection(view,
        sectionInfo = currentFlashcard.romanization.toList(),
        containerId = romanizationSectionViewId,
        build = {
            buildHeaderClickableTextSection(
                view = view,
                context = context,
                textViewId = romanizationSectionViewId,
                textLabelId = R.string.romanization_label,
                data = currentFlashcard.romanization
            )
        })

    /**
     * Information sections
     */

    // definition
    val definitionSectionViewId = R.id.definition_container
    buildSection(view,
        sectionInfo = currentFlashcard.definition.toList(),
        containerId = definitionSectionViewId,
        build = {
            // combine part of speech and definition
            val definitionTextView = view.findViewById<TextView>(R.id.partOfSpeechAndDefinition)
            val partOfSpeech: String = if (currentFlashcard.partOfSpeech.isEmpty()) "" else currentFlashcard.partOfSpeech + " "
            val displayText: String = partOfSpeech + currentFlashcard.definition
            definitionTextView.text = displayText
        })

    // classifier
    val classifierSectionViewId = R.id.classifiers_container
    buildSection(view,
        sectionInfo = currentFlashcard.classifiers,
        containerId = classifierSectionViewId,
        build = {
            buildPillSection(
                view = view,
                context = context,
                layoutInflater = layoutInflater,
                contentId = R.id.classifiers_content,
                data = currentFlashcard.classifiers
            )
        })

    // components
    val componentsSectionViewId = R.id.components_container
    buildSection(view,
        sectionInfo = currentFlashcard.components,
        containerId = componentsSectionViewId,
        build = {
            buildPillSection(
                view = view,
                context = context,
                layoutInflater = layoutInflater,
                contentId = R.id.components_content,
                data = currentFlashcard.components
            )
        })

    // synonyms
    val synonymsSectionViewId = R.id.synonyms_container
    buildSection(view,
        sectionInfo = currentFlashcard.synonyms,
        containerId = synonymsSectionViewId,
        build = {
            buildPillSection(
                view = view,
                context = context,
                layoutInflater = layoutInflater,
                contentId = R.id.synonyms_content,
                data = currentFlashcard.synonyms
            )
        })

    // related words
    val relatedWordsViewId = R.id.related_words_container
    buildSection(view,
        sectionInfo = currentFlashcard.relatedWords,
        containerId = relatedWordsViewId,
        build = {
            buildPillSection(
                view = view,
                context = context,
                layoutInflater = layoutInflater,
                contentId = R.id.related_words_content,
                data = currentFlashcard.relatedWords
            )
        })

    // examples
    val exampleSectionViewId = R.id.examples_container
    buildSection(view,
        sectionInfo = currentFlashcard.examples,
        containerId = exampleSectionViewId,
        build = {
            val parent = view.findViewById<LinearLayout>(R.id.examples_content)

            // get the current example
            if(currentDefinitionExampleIndex != null){

                // modify the text view
                val examplesTextView = view.findViewById<TextView>(R.id.examples_text)
                val color = context.getColor(R.color.md_theme_primary)
                val displayText = HTMLFormatting.addHighlightSpannable(
                    stringToModify = currentFlashcard.examples[currentDefinitionExampleIndex].baseWord + " - " + currentFlashcard.examples[currentDefinitionExampleIndex].definition,
                    word = currentFlashcard.baseWord,
                    color = color
                )
                examplesTextView.text = displayText

                // on click, bring up a dialog to switch to other examples
                // TODO: temporarily switch to the next item on click
                examplesTextView.setOnClickListener(){
                    // viewModel function passed in, which will increase the current example index
                    onExampleClick()
                }
            }
        })

    // sentences
    val sentenceSectionViewId = R.id.sentences_container
    buildSection(view,
        sectionInfo = currentFlashcard.sentences,
        containerId = sentenceSectionViewId,
        build = {
            val parent = view.findViewById<LinearLayout>(R.id.sentences_content)

            // get the current sentence
            if(currentDefinitionSentenceIndex != null){

                // modify the text view
                val sentencesTextView = view.findViewById<TextView>(R.id.sentences_text)
                val color = context.getColor(R.color.md_theme_primary)
                val sentenceText = HTMLFormatting.addHighlightSpannable(
                    stringToModify = currentFlashcard.sentences[currentDefinitionSentenceIndex].baseWord + "\n",
                    word = currentFlashcard.baseWord,
                    color = color
                )
                val romanizationText = HtmlCompat.fromHtml(currentFlashcard.sentences[currentDefinitionSentenceIndex].romanization, HtmlCompat.FROM_HTML_MODE_COMPACT)
                    .toSpannable()
                val definitionText = "\n" + currentFlashcard.sentences[currentDefinitionSentenceIndex].definition
                    .toSpannable()
                val displayText = TextUtils.concat(sentenceText, romanizationText, definitionText)
                sentencesTextView.text = displayText

                // on click, bring up a dialog to switch to other examples
                // TODO: temporarily switch to the next item on click
                sentencesTextView.setOnClickListener(){
                    // viewModel function passed in, which will increase the current example index
                    onSentenceClick()
                }
            }
        })

    // reference
    val referencesSectionViewId = R.id.references_container
    buildSection(view,
        sectionInfo = currentFlashcard.baseWord.toList(),
        containerId = referencesSectionViewId,
        build = {
            val parent = view.findViewById<LinearLayout>(R.id.references_content)

            // generate a references data class on load?
//            {
//                "link": "https://etc",
//                "display": "Wiktionary"
//            }

            // TODO: this should also probably be a recycler view

            //val newView = layoutInflater.inflate(R.layout.fragment_pill, parent)

//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            ViewGroup parent = (ViewGroup)findViewById(R.id.where_you_want_to_insert);
//            inflater.inflate(R.layout.the_child_view, parent);
        })
}

// display or hide sections of the flashcard
fun buildSection(view: View, sectionInfo: List<Any>, @IdRes containerId: Int, build: () -> Unit){
    val containerView: View = view.findViewById(containerId)

    if(sectionInfo.isNotEmpty()){
        build()

        containerView.visibility = View.VISIBLE
    }
    else{
        containerView.visibility = View.GONE
    }
}

fun buildHeaderClickableTextSection(view: View, context: Context, @IdRes textViewId: Int, @StringRes textLabelId: Int, data: String){
    val textView = view.findViewById<TextView>(textViewId)

    // customize the label with spannable string for underline and italics
    // (getting the string from resources doesn't include the html tag <u> and <i>)
    val label = context.getString(textLabelId)
    val formattedLabel = SpannableString(label)
    formattedLabel.setSpan(UnderlineSpan(), 0, formattedLabel.length, 0)
    formattedLabel.setSpan(StyleSpan(Typeface.ITALIC), 0, formattedLabel.length, 0)

    // onClick, show the value. Otherwise, if it's already shown, show the original label
    textView.setOnClickListener(){
        if (textView.text.toString() == formattedLabel.toString()){
            // converts the data to html if it's a string w/ tags, so that it can be converted to a spannable
            val html = Html.fromHtml(data, HtmlCompat.FROM_HTML_MODE_COMPACT)
            textView.text = html.toSpanned()
        }
        else{
            textView.text = formattedLabel
        }
    }
}

fun buildPillSection(view: View, context: Context, layoutInflater: LayoutInflater, @IdRes contentId: Int, data: List<Definition>){
    val parent = view.findViewById<LinearLayout>(contentId)

    // insert a horizontal recyclerview and add an adapter on it for pills.
    // the adapter will be for type viewholder

    val recyclerView = layoutInflater.inflate(R.layout.fragment_recycler, null) as RecyclerView
    recyclerView.adapter = PillListAdapter(
        definitions = data
    )
    recyclerView.setLayoutManager(
        LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
    )

    // update seems to run on each state rebuild
    // check the childcount so it's not added multiple times
    if (parent.childCount < 1){
        parent.addView(recyclerView)
    }
}