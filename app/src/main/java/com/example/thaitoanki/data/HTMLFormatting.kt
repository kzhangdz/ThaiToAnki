package com.example.thaitoanki.data

import com.example.thaitoanki.data.network.Definition

object HTMLFormatting {
    /*
    Desired Output
    <span id="component-1" onclick="toggleElement('component-1-description')" class="pill">เฉย<span id="component-1-description" style="display: none"> (to ignore; to pay no attention to; to disregard)</span></span>
	<span id="component-2" onclick="toggleElement('component-2-description')" class="pill">ชา<span id="component-2-description" style="display: none"> ([is] numb)</span></span>
     */
    fun formatToPillHTML(items: List<Definition>, type: String): String{
        var HTMLString = ""
        for (i in 0..<items.size){
            val item = items[i]
            // id format: synonym-0
            val id = "$type-$i"
            val internalContentId = "$id-description"
            val currentHTMLString = """<span id="$id" class="pill" onclick="toggleElement('$internalContentId')">${item.baseWord}<span id="$internalContentId" style="display: none"> ${item.definition}</span></span>"""
            HTMLString += currentHTMLString
        }
        return HTMLString
    }

    // TODO: potential error where every string that matches the word gets highlighted.
    // could happen easily with งง, i.e. วงงง
    /*
    Desired Output
    <span class="highlight">พูด</span>เล่น - to joke; make a joke; speak jokingly; to kid
     */
    fun formatExamplesToHTML(examples: List<Definition>, word: String): String{
        var HTMLString = ""
        for(example in examples){
            var currentHTMLString = addHighlightHTML(example, word)

            currentHTMLString += " - ${example.definition}"

            HTMLString += currentHTMLString

            // TODO: for now, only use the first example
            break
        }
        return HTMLString
    }

    /*
    Desired Output
    <div><span class="highlight">พูด</span>ภาษาไทยได้ไหมครับ</div>
	<div class="description">Can you speak Thai?</div>
     */
    fun formatSentencesToHTML(sentences: List<Definition>, word: String): String{
        var HTMLString = ""
        for(sentence in sentences){
            // get output like <span class="highlight">พูด</span>ภาษาไทยได้ไหมครับ
            var currentHTMLString = addHighlightHTML(sentence, word)

            // add div around the output
            currentHTMLString = "<div>${currentHTMLString}</div>"

            // add the romanization <div class="description">innerHTML</div>
            currentHTMLString += """<div class="description">${sentence.romanization}</div>"""

            // add the translation <div class="description">Can you speak Thai?</div>
            currentHTMLString += """<div class="description">${sentence.definition}</div>"""

            HTMLString += currentHTMLString

            // TODO: for now, only use the first sentence
            break
        }
        return HTMLString
    }

    /*
    Given a word, add a <span class="highlight"></span> around the word, using the sliding window method
     */
    fun addHighlightHTML(definition: Definition, word: String): String{
        var currentHTMLString = ""

        var i = 0
        while(i < definition.baseWord.length){ //- word.length
            val windowEndIndex = i + word.length

            // at the end, if there isn't enough room left for the window
            if(windowEndIndex > definition.baseWord.length){
                // add the current character
                currentHTMLString += definition.baseWord[i]
                i++
            }
            else {
                val currentWindow: String = definition.baseWord.substring(i, windowEndIndex)

                if (currentWindow == word) {
                    currentHTMLString += """<span class="highlight">${currentWindow}</span>"""

                    // skip to the end of the window word
                    i = windowEndIndex
                } else {
                    // otherwise, add the current character
                    currentHTMLString += currentWindow[0]
                    i++
                }
            }
        }

        return currentHTMLString
    }
}