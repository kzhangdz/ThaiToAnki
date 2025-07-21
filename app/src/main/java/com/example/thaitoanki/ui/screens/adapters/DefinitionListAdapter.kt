package com.example.thaitoanki.ui.screens.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.thaitoanki.R
import com.example.thaitoanki.data.network.Definition

class DefinitionListAdapter(
    private val definitions: List<Definition>,
    private val definitionBlockOnClick: (Int) -> Unit
) : RecyclerView.Adapter<DefinitionListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // need to declare all the elements in the card
        val definitionBlockView: LinearLayout
        val definitionBlockTextView: TextView

        init {
            // Define click listener for the ViewHolder's View
            definitionBlockView = view.findViewById<LinearLayout>(R.id.definition_block)
            definitionBlockTextView = view.findViewById<TextView>(R.id.definition_block_text)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.fragment_definition_block, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.definitionBlockTextView.text = definitions[position].definition

        viewHolder.definitionBlockView.setOnClickListener(){
            // TODO: onclick that will return the index of the clicked item
            definitionBlockOnClick(position)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = definitions.size

}