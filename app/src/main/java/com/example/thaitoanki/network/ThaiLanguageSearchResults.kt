package com.example.thaitoanki.network

import android.util.Log
import org.jsoup.nodes.Document

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

    init {
        topResultId = parseTopResult()
    }


    // TODO: some entries skip the search page and go directly to the word
    // those entries have the word in the title <title>thai-language.com - ดิกชันนารี่</title>
    // <script> has tt_dict var
    // if that's the case, generate a ThaiLanguageData object?
    private fun parseTopResult(): Int? {
        val htmlTitle = htmlResults.getElementsByTag("title")
        val title = htmlTitle.text()

        // if we directly went to a definition page
        if (title.contains(searchWord)){
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
        else{
            val htmlTable = htmlResults.getElementsByClass(TABLE_CLASS)
            if (htmlTable != null){
                // get a list of hrefs from the table. the top one should be the top result

                // need to grab the first arrow. Should be in this element
                // <a href="/id/224923"><img src="/img/phr_link.gif"></a>
                val arrow = htmlTable.select("a[href]>img")[0]
                val link = arrow.parent()

                val href = link.attr("href")

                // extract the id from the href
                val id = extractId(href)
                return id



                // get the row with the top result, the second row
//                val row = htmlTable.select("tr")[1]
//                Log.d("ThaiLanguageSearchResults", row.`val`())
//
//                // select the <a href="/id/1234"> under the second <td>
//                val link = row.select("a[href]")
//
//                val href = link.attr("href")
//
//                // extract the id from the href
//                val id = extractId(href)
//                return id
            }
            else{
                return null
            }
        }
    }

    private fun extractId(href: String): Int?{
        val idString = href.split("/").last()
        val id = idString.toIntOrNull()
        return id
    }
}