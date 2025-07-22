package com.example.thaitoanki.ui.screens.adapters

import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import androidx.core.text.HtmlCompat
import androidx.core.text.toSpannable
import com.example.thaitoanki.data.HTMLFormatting
import com.example.thaitoanki.data.network.Definition
import com.example.thaitoanki.data.network.getSmallDefinition
import com.example.thaitoanki.data.network.getSmallRomanization

class SentenceListAdapter(
    private val definitions: List<Definition>,
    private val definitionBlockOnClick: (Int) -> Unit
) : DefinitionListAdapter(
    definitions,
    definitionBlockOnClick
){
    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val currentSentence = definitions[position]

        val sentenceText = currentSentence.baseWord
//            HTMLFormatting.addHighlightSpannable(
//            stringToModify = currentFlashcard.sentences[currentDefinitionSentenceIndex].baseWord,
//            word = currentFlashcard.baseWord,
//            color = color
//        )
        //sentenceText.setSpan(RelativeSizeSpan(1.2f), 0, sentenceText.length, 0)
        val romanizationText = HtmlCompat.fromHtml(
            currentSentence.getSmallRomanization(),
            HtmlCompat.FROM_HTML_MODE_COMPACT
        ).toSpannable()
        val definitionText = HtmlCompat.fromHtml(
            currentSentence.getSmallDefinition(),
            HtmlCompat.FROM_HTML_MODE_COMPACT
        ).toSpannable()
        val displayText = TextUtils.concat(sentenceText, "\n", romanizationText, "\n", definitionText)

        viewHolder.definitionBlockTextView.text = displayText

        viewHolder.definitionBlockView.setOnClickListener(){
            // TODO: onclick that will return the index of the clicked item
            definitionBlockOnClick(position)
        }
    }
}