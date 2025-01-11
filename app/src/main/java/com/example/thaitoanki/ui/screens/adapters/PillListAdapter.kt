package com.example.thaitoanki.ui.screens.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.thaitoanki.R
import com.example.thaitoanki.data.network.Definition

class PillListAdapter(private val definitions: List<Definition>) :
    RecyclerView.Adapter<PillListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // need to declare all the elements in the card
        val pillView: CardView
        val pillTextView: TextView

        init {
            // Define click listener for the ViewHolder's View
            pillView = view.findViewById<CardView>(R.id.pill)
            pillTextView = view.findViewById<TextView>(R.id.pill_text)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.fragment_pill, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.pillTextView.text = definitions[position].baseWord

        viewHolder.pillView.setOnClickListener(){
            val isNotExpanded: Boolean = viewHolder.pillTextView.text == definitions[position].baseWord

            // modify the pill text to display a paragraph
            if (isNotExpanded){
                // expand
                viewHolder.pillTextView.text = definitions[position].baseWord + "\n" + definitions[position].definition
            }
            else{
                viewHolder.pillTextView.text = definitions[position].baseWord
            }

            // need way to unclick
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = definitions.size

}