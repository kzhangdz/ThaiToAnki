package com.example.thaitoanki.network

import android.util.Log
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.jsoup.select.Selector
import kotlin.math.truncate

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
        val definitionBlockElements = Elements(definitionBlock)

        val sectionStartTds = definitionBlockElements.select("td[style*=background-color]")

        val sectionNames = sectionStartTds.map {
            td ->
            td.text()
        }

        // TODO: need a function to modify sectionNames a little bit.
        // i.e. sometimes 'sample sentences' is just 'sample sentence'

        val sectionStartRows = sectionStartTds.map {
            td ->
            td.parent()
        }

        val sectionStartIndices = sectionStartRows.map{
            row ->
            definitionBlock.indexOf(row)
        }

        val sectionEndIndices: MutableList<Int> = mutableListOf()
        for (i in sectionStartRows.indices){
            val nextIndex = i + 1
            if (nextIndex >= sectionStartIndices.size){
                val lastIndex = definitionBlock.size

                // go back two elements
                //<tr> <td style="height:9px"></td> </tr>
                //<tr style="background-color:#C0C0C0"> <td colspan="4" style="padding-bottom:1px; padding-top:0px"></td> </tr>

                sectionEndIndices.add(lastIndex - 2)
            }
            else{
                sectionEndIndices.add(sectionStartIndices[nextIndex])
            }
        }

        Log.d(LOG_TAG, sectionStartIndices.toString())

        // TODO: get the part of speech and alternate word
        val partOfSpeechIndex: Int = sectionStartIndices[0] - 1
        val partOfSpeechSection: Element = definitionBlock[partOfSpeechIndex]
        val partOfSpeech: String = parsePartOfSpeechFromSection(partOfSpeechSection)
        val alternateWord: String = parseAlternateWordFromSection(partOfSpeechSection, word)




        // extract the elements for each remaining section
        val sections: MutableMap<String, List<Element>> = mutableMapOf()

        for (i in sectionStartRows.indices){
            val category = sectionNames[i]

            val startIndex = sectionStartIndices[i]
            val endIndex = sectionEndIndices[i]

            val section: List<Element> = definitionBlock.slice(startIndex..<endIndex)

            sections[category] = section

            Log.d(LOG_TAG, section.toString())
        }

        // iterate through the sections to build the Definition object
        var definition = ""
        var sentences: List<Definition> = listOf()
        sections.forEach{ section ->
            when (section.key){
                // TODO: more flexible keys, like synonym vs. synonyms
                "definition" -> {
                    // TODO: get the values back
                    definition = parseDefinitionFromSection(section.value)
                }
                "synonym", "synonyms" -> {
                    parseSynonymsFromSection(section.value)
                }
                "example", "examples" -> {

                }
                "sample sentence", "sample sentences" -> {
                    sentences = parseSentencesFromSection(section.value)
                }
            }
        }

        // TODO: replace word w/ alternate word
        val wordDefinition = Definition(
            baseWord = alternateWord,
            definition = definition,
            partOfSpeech = partOfSpeech,
            sentences = sentences
        )

        return wordDefinition
    }

    // the next section has been reached if the passed-in <tr> element
    // has a style attr w/ background-color
    private fun hasReachedNextSection(row: Element, category: String, categories: List<String>): Boolean{
        val td = row.select("td[style*=background-color]")
        val returnedCategory = td[0].text()

        // no results
        if(td.size <= 0){
            return false
        }

//        // if the current cell isn't a category we're counting, skip it
//        if (returnedCategory !in categories){
//            return false
//        }

        // if we've reached the next category
        if (!returnedCategory.contains(category)){
            return true
        }

        return false
    }

    private fun parseAlternateWordFromSection(definitionSection: Element, baseWord: String): String{
        try {
            val span = definitionSection.select("span[class=th2]")
            val alternateWord = span.text()

            if(alternateWord == ""){
                return baseWord
            }
            return alternateWord
        }
        catch (e: Exception){
            return baseWord
        }
    }

    private fun parsePartOfSpeechFromSection(definitionSection: Element): String{
        try {
            val span = definitionSection.select("span[style=font-size:x-small]")
            val partOfSpeech = span.text()

            return partOfSpeech
        }
        catch (e: Exception){
            return ""
        }
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

    private fun parseSentencesFromSection(definitionSection: List<Element>): List<Definition> {
        try{
            val sentences: MutableList<Definition> = mutableListOf()

            for (element in definitionSection){
                val div = element.select("div[class=igt]")
                val children = div[0].children()

                val text = div.text()
                val sentence = children[0].text()
                val romanization = children[2].text()
                val meaning = text.replace(sentence, "").replace(romanization, "").trim()

                val definition = Definition(
                    sentence,
                    definition = meaning,
                    romanization = romanization
                )

                Log.d(LOG_TAG, sentence)

                sentences.add(definition)
            }

            return sentences
        }
        catch(e: Exception){
            Log.e(LOG_TAG, e.toString())
            return emptyList()
        }
    }
}