package com.example.thaitoanki.network

import android.util.Log
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/*
class to hold data retrieved from thai-language.com

Because there is no serializer for HTML, we will need to perform transformations ourselves
https://github.com/Kotlin/kotlinx.serialization/blob/master/formats/README.md#properties

*/
class ThaiLanguageSearchResults(
    private val searchWord: String,
    private val htmlResults: Document
) {
    val topResultId: Int?
    var isDefinitionRetrieved: Boolean = false
    private val TABLE_CLASS: String = "gridtable"
    private val LOG_TAG: String = "ThaiLanguageSearchResults"

    init {
        topResultId = parseTopResult()
    }


    // TODO: some entries skip the search page and go directly to the word
    // those entries have the word in the title <title>thai-language.com - ดิกชันนารี่</title>
    // <script> has tt_dict var
    // if that's the case, generate a ThaiLanguageData object?
    private fun parseTopResult(): Int? {
        try {
            val htmlTitle = htmlResults.getElementsByTag("title")
            val title = htmlTitle.text()

            // TODO: still doesn't work. only compounds have arrows. i.e. search เฉย
            // maybe search for the number 1?
            // if we directly went to a definition page
            if (title.contains(searchWord)) {
                //select <link rel="canonical" href="http://www.thai-language.com/id/133189" />
                val link = htmlResults.select("link[rel]")[0]

                val href = link.attr("href")

                // extract the id from the href
                val id = extractId(href)

                // set a value saying we already have the definition
                isDefinitionRetrieved = true

                return id
            }
            // if we went to a search results page
            else {
                val htmlTable = htmlResults.getElementsByClass(TABLE_CLASS)
                if (!htmlTable.isEmpty()) {
                    // get the row marked with 1.
                    val td = htmlTable[0].select("td:contains(1.)")
                    val tr = td.parents()[0]

                    // check for arrow
                    //<a href="/id/224923"><img src="/img/phr_link.gif"></a>
                    val arrow = tr.select("a[href]>img[src*=phr_link]")
                    var link: Element = Element("temp")
                    if (arrow.size > 0) {
                        link = arrow[0].parent()
                    } else {
                        // if there is no arrow, click on the link directly
                        link = tr.select("a[href]")[0]
                    }

                    val href = link.attr("href")

                    // extract the id from the href
                    val id = extractId(href)
                    return id

                } else {
                    Log.e(LOG_TAG, "No results table found")
                    return null
                }
            }
        }
        catch (e: Exception){
            Log.e(LOG_TAG, e.toString())
            return null
        }
    }

    private fun extractId(href: String): Int?{
        val idString = href.split("/").last()
        val id = idString.toIntOrNull()
        return id
    }
}