package com.example.thaitoanki.network

import android.util.Log
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.jsoup.select.Selector

/*
class to hold data retrieved from thai-language.com

Because there is no serializer for HTML, we will need to perform transformations ourselves
https://github.com/Kotlin/kotlinx.serialization/blob/master/formats/README.md#properties

*/

enum class DefinitionCategory(){
    Definition,
    Synonym,
    Example,
    Sample
}

class ThaiLanguageData(
    val word: String,
    val htmlResults: Document
) {
    val LOG_TAG = "ThaiLanguageData"

    // list of Definition
    val definitions: List<Definition>

    // parse the definition page
    init {
        definitions = parseDefinitions()
    }

    // separate out the page into blocks for each definition
    private fun parseDefinitions(): List<Definition>{
        try {
            // select the last table in the old-content div, which has all blocks
            val contentDiv = htmlResults.select("div[id=old-content]")[0]
            val table = contentDiv.select("table").last()
            val tableBody = table.child(0)
            val tableRows = tableBody.children()

            // grab the indices of the <tr> that are line breaks. they are <tr> w/ a style: background-color
            // the table starts w/ this <tr> and ends with this <tr>
            // therefore, the blockIndices will always have at least 2 entries
            // need to make slices based on a start index and final index
            val blockIndices:MutableList<Int> = mutableListOf()
            for (i in 0..< tableRows.size){
                val currentElement: Element = tableRows[i]

                // grab the indices of the <tr> that are line breaks. they are <tr> w/ a background style
                if (currentElement.attr("style").contains("background-color")){
                    blockIndices.add(i)
                }
            }

            Log.d(LOG_TAG, blockIndices.toString())

            // slice table elements based on a start index and final index
            val definitionBlocks: MutableList<List<Element>> = mutableListOf()
            for (i in 1..< blockIndices.size){
                val previousIndex = blockIndices[i - 1]
                val currentIndex = blockIndices[i]

                val block = tableRows.slice(previousIndex..currentIndex)

                // TODO: maybe I should make a DefinitionBlock data class
                definitionBlocks.add(block)
            }

            // parse the definition blocks
            val returnedDefinitions: MutableList<Definition> = mutableListOf()
            for (definitionBlock in definitionBlocks){
                val definition = parseDefinitionBlock(definitionBlock)
                returnedDefinitions.add(definition)
            }

            Log.d(LOG_TAG, definitionBlocks.toString())

            return returnedDefinitions
        }
        catch (e: Selector.SelectorParseException){
            Log.e(LOG_TAG, e.toString())
            return emptyList()
        }
    }

    // get the data within a definition block
    private fun parseDefinitionBlock(definitionBlock: List<Element>): Definition{

        // first row should contain the alternate word and part of speech

        // change this to a map of maps
        val categories: Map<String, Pair<Int, Int>> = mapOf(
            "definition" to Pair(0, 0),
            "synonyms" to Pair(0, 0),
            "examples" to Pair(0, 0),
            "sample" to Pair(0, 0), // sentences
        )

        // TODO: the code will break if sections are missing. maybe store starting indices as a value in a map?
        // or loop to get the end indices as well? That would be when the category changes.
        // when the current category is not found, but the tr contains a different one

        // break up the parts of the list by their categories
        var startingIndex = 0
        val startingIndices: MutableList<Int> = mutableListOf()
        for (category in categories){

            for(i in startingIndex..<definitionBlock.size){
                val row = definitionBlock[i]
                val matchingSection = row.getElementsMatchingText(category)

                if (matchingSection.size > 0){
                    startingIndices.add(i)
                    startingIndex = i
                    continue
                }
            }
        }

        for(index in startingIndices){
            Log.d(LOG_TAG, definitionBlock[index].toString())
        }

        // slice the sections based on the starting indices. last one, force it to definitionBlock.size - 1
        val sections: MutableMap<String, List<Element>> = mutableMapOf()
        for (i in 0..<categories.size){
            val category = categories[i]

            val startIndex = startingIndices[i]
            val endIndex = startingIndices.getOrNull(i + 1) ?: categories.size

            val section: List<Element> = definitionBlock.slice(startIndex..<endIndex)

            sections[category] = section

            Log.d(LOG_TAG, section.toString())
        }

        // iterate through the sections to build the Definition object
        sections.forEach{ section ->
            when (section.key){
                "definition" -> {
                    // TODO: get the values back
                    parseDefinitionFromSection(section.value)
                }
                "synonyms" -> {
                    parseSynonymsFromSection(section.value)
                }
            }
        }

//        for(row in definitionBlock){
//            val test = row.getElementsMatchingText("definition")
//        }

        // extract the word. if it's null, use the passed-in word argument

        // extract the definition
        //definitionBlock.select("tr")[0].get

//        <tr>
//            <td style="background-color:#808080; color:white; padding-left:7px; padding-right:7px; max-width:100px;">definition</td>
//            <td colspan="3" class="df"><br><b>to quit; to stop; to give up; cancel; suspend; discontinue; abolish; cease; exit; split up</b><br><br></td>
//        </tr>

        return Definition("", "")
    }

    private fun parseDefinitionFromSection(definitionSection: List<Element>): String{
        try {
            val element: Element = definitionSection[0]
            val b = element.select("b")
            val definition = b.text()

            return definition
        }
        catch (e: Exception){
            return ""
        }
    }

    private fun parseSynonymsFromSection(definitionSection: List<Element>): List<Definition>{
        try{
            for (element in definitionSection){

                //TODO: parsing

            }

            return emptyList()
        }
        catch(e: Exception){
            return emptyList()
        }

    }
}