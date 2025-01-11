package com.example.thaitoanki.ui.screens.components

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.cardview.widget.CardView
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.thaitoanki.R
import com.example.thaitoanki.data.network.Definition
import com.example.thaitoanki.data.network.TestDefinitions
import com.example.thaitoanki.ui.screens.adapters.PillListAdapter
import com.example.thaitoanki.ui.theme.ThaiToAnkiTheme

//import com.mig35.carousellayoutmanager.CarouselLayoutManager
//import com.mig35.carousellayoutmanager.CenterScrollListener


@Composable
fun TestFlashcardFront(
    flashcardInfo: List<Definition>,
    currentDefinitionIndex: Int,
    modifier: Modifier = Modifier
){
    AndroidView(
        factory = { context ->
            View.inflate(context, R.layout.fragment_carousel, null)
        },
        modifier = modifier,
        update = { view ->
            updateTestFlashcardFrontView(view, flashcardInfo)
        }
    )
}

fun updateTestFlashcardFrontView(view: View, flashcardInfo: List<Definition>){
//    val layoutManager: CarouselLayoutManager = CarouselLayoutManager(HeroCarouselStrategy()) //CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL);
//
//    val recyclerView = view.findViewById<RecyclerView>(R.id.carousel_horizontal);
//    recyclerView.setLayoutManager(layoutManager);
//    recyclerView.setHasFixedSize(true);
//
//    recyclerView.adapter = CustomAdapter(
//        definitions = flashcardInfo
//    )

    // enable items center scrolling
    //recyclerView.addOnScrollListener(CenterScrollListener())




    val pager = view.findViewById<ViewPager2>(R.id.pager_horizontal)

    pager.adapter = CustomAdapter(
        definitions = flashcardInfo
    )



    val flashcard = view.findViewById<LinearLayout>(R.id.flashcard_front)



//    val pager = view.findViewById<ViewPager>(R.id.pager_horizontal)
//
//    pager.adapter = CardPagerAdapter(
//        definitions = flashcardInfo
//    )
}

//class CardPagerAdapter(private val definitions: List<Definition>) :
//        PagerAdapter()
//{
//    override fun instantiateItem(container: ViewGroup, position: Int): Any {
//        val view = LayoutInflater.from(viewGroup.context)
//            .inflate(R.layout.fragment_flashcard_front, viewGroup, false)
//
//
//    }
//
//    override fun getCount(): Int {
//        return definitions.size
//    }
//
//    override fun isViewFromObject(view: View, `object`: Any): Boolean {
//        return view === `object`
//    }
//
//}

class CustomAdapter(private val definitions: List<Definition>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // need to declare all the elements in the card
        // start with just the title for now
        val wordView: TextView
        val definitionView: TextView

        init {
            // Define click listener for the ViewHolder's View
            wordView = view.findViewById(R.id.word)
            definitionView = view.findViewById(R.id.definition)

        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.fragment_flashcard_front, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.wordView.text = definitions[position].baseWord
        viewHolder.definitionView.text = definitions[position].definition + definitions[position].definition + definitions[position].definition

        // for infinite scroll
        if(position == itemCount - 1){

        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = definitions.size

}

@Preview(showBackground = true)
@Composable
fun TestFlashcardFrontPreview() {
    ThaiToAnkiTheme(
        darkTheme = false
    ) {
        TestFlashcardFront(
            flashcardInfo = TestDefinitions.definitions,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
                .verticalScroll(rememberScrollState()),
            currentDefinitionIndex = 1,
        )
    }
}

@Composable
fun FlashcardFront(
    flashcardInfo: List<Definition>,
    currentDefinitionIndex: Int,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
){
    val currentFlashcard = flashcardInfo[currentDefinitionIndex]
    
    AndroidView(
        factory = { context ->
            View.inflate(context, R.layout.fragment_flashcard_front, null)
        },
        modifier = modifier,
        update = { view ->
            updateFlashcardFrontView(view, currentFlashcard, onClick)
        }
    )
}

// TODO: move these functions out if needed in the service
fun updateFlashcardFrontView(view: View, currentFlashcard: Definition, onClick: () -> Unit){
    // variables used for adding views to sections
    val context = view.context.applicationContext
    val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater



    val flashcardView = view.findViewById<CardView>(R.id.flashcard)

    // not working
    // I think it's because on the blue view, it only has a small outline exposed
    flashcardView.setOnClickListener(){
        Log.d("flashcard", "clicking works")
    }

    // set the header text with word info
    // word
    val titleTextView = view.findViewById<TextView>(R.id.word)
    titleTextView.setText(currentFlashcard.baseWord)
    // TODO: temporarily changing card on title click
    titleTextView.setOnClickListener(){
        
        // update the index by one
        onClick()
    }

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

            // insert a horizontal recyclerview and add an adapter on it for pills.
            // the adapter will be for type viewholder

            val recyclerView = layoutInflater.inflate(R.layout.fragment_recycler, null) as RecyclerView
            recyclerView.adapter = PillListAdapter(
                definitions = currentFlashcard.components
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
        })


    // components
//    val componentsSectionViewId = R.id.components_container
//    buildSection(view,
//        sectionInfo = currentFlashcard.components,
//        containerId = componentsSectionViewId,
//        build = {
//            val parent = view.findViewById<LinearLayout>(R.id.components_content)
//
//            for (i in 0..<currentFlashcard.components.size){
//                //val newView = View()
//                //parent.addView(R.layout.fragment_pill)
//                //val pill = layoutInflater.inflate(R.layout.fragment_pill, parent) as Button
//
//                // TODO: might need an arrayAdapter for this to work
//                // Might also need to switch to ListView
////                val pill = View.inflate(context, R.layout.fragment_pill, parent) as ViewGroup
////
////                Log.d("test", pill.getChildAt(0).toString())
////                //https://stackoverflow.com/questions/8395168/android-get-children-inside-a-view
//
////                val pillButton = view.findViewById<Button>(R.id.pill)
////                pillButton.text = "test"
////
////                view.findView
//
//                // set the id
//                //pill.setTag(i)
//                //pill.id = View.generateViewId()
//
//                // adjust the text inside
//                //pill.text = "text"//(currentFlashcard.components[i].baseWord)
//            }
//
////            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////            ViewGroup parent = (ViewGroup)findViewById(R.id.where_you_want_to_insert);
////            inflater.inflate(R.layout.the_child_view, parent);
//        })

    // synonyms

    // related words

    // examples
    val exampleSectionViewId = R.id.examples_container
    buildSection(view,
        sectionInfo = currentFlashcard.examples,
        containerId = exampleSectionViewId,
        build = {
            val parent = view.findViewById<LinearLayout>(R.id.examples_section)


            //val newView = layoutInflater.inflate(R.layout.fragment_pill, parent)

//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            ViewGroup parent = (ViewGroup)findViewById(R.id.where_you_want_to_insert);
//            inflater.inflate(R.layout.the_child_view, parent);
        })

    // sentences

    // reference
}

// display or hide sections of the flashcard
fun buildSection(view: View, sectionInfo: List<Any>, @IdRes containerId: Int, build: () -> Unit){
    val containerView: View = view.findViewById(containerId)

    if(sectionInfo.isNotEmpty()){
        build()

        containerView.visibility = View.VISIBLE
    }
    else{
        //(containerView.getParent() as ViewGroup).removeView(containerView)
        containerView.visibility = View.GONE
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