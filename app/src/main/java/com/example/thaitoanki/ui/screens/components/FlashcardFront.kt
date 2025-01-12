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

    // information sections

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

            val color = context.getColor(R.color.md_theme_primary)

            // modify the text view
            val examplesTextView = view.findViewById<TextView>(R.id.examples_text)
            val displayText = HTMLFormatting.addHighlightSpannable(
                stringToModify = currentFlashcard.examples[0].baseWord + " - " + currentFlashcard.examples[0].definition,
                word = currentFlashcard.baseWord,
                color = color
            )
            examplesTextView.text = displayText

            // on click, bring up a dialog to switch to other examples
            // TODO: temporarily
        })

    // sentences

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