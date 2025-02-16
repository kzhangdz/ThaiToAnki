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
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.cardview.widget.CardView
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.core.text.toSpannable
import androidx.core.text.toSpanned
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.thaitoanki.R
import com.example.thaitoanki.data.HTMLFormatting
import com.example.thaitoanki.data.network.Definition
import com.example.thaitoanki.data.network.TestDefinitions
import com.example.thaitoanki.services.toAnnotatedString
import com.example.thaitoanki.ui.screens.adapters.PillListAdapter
import com.example.thaitoanki.ui.theme.ThaiToAnkiTheme
import okhttp3.internal.format


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
            definitionView = view.findViewById(R.id.partOfSpeechAndDefinition)

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
    currentExampleIndices: List<Int?>,
    currentSentenceIndices: List<Int?>,
    onClick: () -> Unit = {},
    onLeftClick: () -> Unit = {},
    onRightClick: () -> Unit = {},
    onExampleClick: () -> Unit = {},
    onSentenceClick: () -> Unit = {},
    modifier: Modifier = Modifier
){
    val currentFlashcard = flashcardInfo[currentDefinitionIndex]
    val currentDefinitionExampleIndex = currentExampleIndices[currentDefinitionIndex]
    val currentSentenceIndex = currentSentenceIndices[currentDefinitionIndex]

    AndroidView(
        factory = { context ->
            View.inflate(context, R.layout.fragment_flashcard_front, null)
        },
        modifier = modifier,
        update = { view ->
            updateFlashcardFrontView(
                view,
                currentDefinitionIndex = currentDefinitionIndex,
                definitionCount = flashcardInfo.size,
                currentFlashcard,
                currentDefinitionExampleIndex,
                currentSentenceIndex,
                onClick,
                onLeftClick,
                onRightClick,
                onExampleClick,
                onSentenceClick
            )
        }
    )
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
            currentDefinitionIndex = 2,
            currentExampleIndices = List(size = TestDefinitions.definitions.size) { 0 },
            currentSentenceIndices = List(size = TestDefinitions.definitions.size) { 0 }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FlashcardFrontBlankPreview() {
    ThaiToAnkiTheme(
        darkTheme = false
    ) {
        FlashcardFront(
            flashcardInfo = TestDefinitions.definitions,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
                .verticalScroll(rememberScrollState()),
            currentDefinitionIndex = 0,
            currentExampleIndices = List(size = TestDefinitions.definitions.size) { 0 },
            currentSentenceIndices = List(size = TestDefinitions.definitions.size) { 0 }
        )
    }
}