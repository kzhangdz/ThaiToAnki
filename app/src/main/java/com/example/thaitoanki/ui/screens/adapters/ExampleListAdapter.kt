package com.example.thaitoanki.ui.screens.adapters

import com.example.thaitoanki.R
import com.example.thaitoanki.data.HTMLFormatting
import com.example.thaitoanki.data.network.Definition

class ExampleListAdapter(
    private val definitions: List<Definition>,
    private val definitionBlockOnClick: (Int) -> Unit,
    //private val baseWord: String
) : DefinitionListAdapter(
    definitions,
    definitionBlockOnClick
){
    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val currentExample = definitions[position]

//        val color = context.getColor(R.color.md_theme_primary)
//        val displayText = HTMLFormatting.addHighlightSpannable(
//            stringToModify = currentExample.baseWord + " - " + currentExample.definition,
//            word = currentFlashcard.baseWord,
//            color = color
//        )

        viewHolder.definitionBlockTextView.text = currentExample.baseWord + " - " + currentExample.definition

        viewHolder.definitionBlockView.setOnClickListener(){
            // TODO: onclick that will return the index of the clicked item
            definitionBlockOnClick(position)
        }
    }
}