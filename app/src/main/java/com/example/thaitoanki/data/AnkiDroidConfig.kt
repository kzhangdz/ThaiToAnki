package com.example.thaitoanki.data


/** Some fields to store configuration details for AnkiDroid  */
internal object AnkiDroidConfig {
//    // Name of deck which will be created in AnkiDroid
//    const val DECK_NAME: String = "API Sample"
//
//    // Name of model which will be created in AnkiDroid
//    const val MODEL_NAME: String = "com.ichi2.apisample"
//
//    // Optional space separated list of tags to add to every note
    val TAGS: Set<String> = HashSet(listOf("ThaiToAnki", "note-maroon"))

    // TODO: add a full list of fields when ready
    // List of field names that will be used in AnkiDroid model
    val FIELDS_FILE_LOCATION = "fields.txt"
    val FIELDS: Array<String> = arrayOf(
        "Word",
        "Pronunciation",
        "Romanization",
        "PartOfSpeech",
        "Definition",
        "Synonyms",
        "RelatedWords",
        "Examples",
        "Sentences",
        "WordId"
    )

    // List of card names that will be used in AnkiDroid (one for each direction of learning)
    //val CARD_NAMES: Array<String> = arrayOf("Japanese>English", "English>Japanese")
    val CARD_NAMES: Array<String> = arrayOf("Thai>English")

    // CSS to share between all the cards
    const val CSS_FILE_LOCATION = "style.css"
    const val CSS: String = """
        
    """

//        """
//        color: black;
//    """

    // Template for the question of each card
    const val QFMT_FILE_LOCATION = "front.html"
    const val QFMT1: String = """
        <div class="note front {{Tags}}">
          <div class="body center">
            <div class="flex flex-col symbol">{{Word}}
                <a class="replay-button soundLink" href="#" title="Ankiweb replay button">
                <svg class="playImage" viewBox="0 0 64 64" version="1.1">
                <circle cx="32" cy="32" r="29"></circle>
                    <path d="M56.502,32.301l-37.502,20.101l0.329,-40.804l37.173,20.703Z"></path>
              </svg>
            </a>
        </div>
                <a class="hint" href="#">{{hint:Pronunciation}}</a>
                <a class="hint" href="#">{{hint:Romanization}}</a>
          </div>
        </div>
    """


        //"<div class=big>{{Word}}</div>" //"<div class=big>{{Expression}}</div><br>{{Grammar}}"
    //const val QFMT2: String = "{{Meaning}}" //"{{Meaning}}<br><br><div class=small>{{Grammar}}<br><br>({{SentenceMeaning}})</div>"
    val QFMT: Array<String> = arrayOf(QFMT1) //arrayOf(QFMT1, QFMT2)

    // Template for the answer (use identical for both sides)
    const val AFMT_FILE_LOCATION = "back.html"
    const val AFMT1: String = """
        <div class="note back {{Tags}}">
            {{FrontSide}}
        
            <hr id="answer">
        
            <!-- Anything in the content section is what you will send from ThaiToAnki -->
            <div class="body">
                <div id="definition-section" class="text-block">
                    <div class="content flex">{{PartOfSpeech}} {{Definition}}</div>
                </div>
                <div id="synonym-section" class="text-block">
                    <div class="subtitle">Synonyms</div>
                    <!-- send the data over as pills -->
                    <div class="content">
                    {{Synonyms}}
                    </div>
                </div>
                <div id="related-words-section" class="text-block">
                    <div class="subtitle">Related Words</div>
                    <div class="content">
                    {{RelatedWords}}
                    </div>
                </div>
                <div id="example-section" class="text-block">
                    <div class="subtitle">Example</div>
                    <div class="content">
                    {{Examples}}
                    </div>
                </div>
                <div id="sentence-section" class="text-block">
                    <div class="subtitle">Sentence</div>
                    <div class="content">
                    {{Sentences}}
                    </div>
                </div>
                    <div class="text-block">
                        <div class="subtitle">Reference</div>
                        <a href="http://thai-language.com/id/{{WordId}}" target="_blank">thai-language.com</a>
                        <br>
                        <a href="https://en.wiktionary.org/wiki/{{Word}}" target="_blank">Wiktionary</a>
                    </div>
                </div>
        </div>

        <script>
        // generate the list of section ids
        var section_elements = document.querySelectorAll('[id*=-section]');
        var sections = [];
        for (const element of section_elements){
            sections.push(element.id)
        }
        
        //clear sections that have no data
        for (const section of sections){
            var element = document.getElementById(section)
            var content = element.querySelector(".content")
        
            if(content.innerHTML.trim() == ""){
                element.remove()
            }
        }
        
        </script>

    """



    /*
        """
        <div class=big>{{Word}}</div>
        <br>
        {{Meaning}}
    """
     */


    val AFMT: Array<String> = arrayOf(AFMT1) //arrayOf(AFMT1, AFMT1)

    // Define two keys which will be used when using legacy ACTION_SEND intent
    //val FRONT_SIDE_KEY: String = FIELDS[0]
    //val BACK_SIDE_KEY: String = FIELDS[2]

    val exampleData: List<Map<String, String>>
        /**
         * Generate the ArrayList<HashMap> example data which will be sent to AnkiDroid
        </HashMap> */
        get() {
            val EXAMPLE_WORDS = arrayOf("例", "データ", "送る")
            val EXAMPLE_TRANSLATIONS = arrayOf("Example", "Data", "To send")
            val EXAMPLE_SENTENCE =
                arrayOf("そんな先例はない。", "きゃ～データが消えた！", "放蕩生活を送る。")
            val EXAMPLE_SENTENCE_MEANING = arrayOf(
                "We have no such example", "Oh, I lost the data！",
                "I lead a fast way of living."
            )

            val data: MutableList<Map<String, String>> = ArrayList()
            for (idx in EXAMPLE_WORDS.indices) {
                val hm: MutableMap<String, String> = HashMap()
                hm[FIELDS[0]] = EXAMPLE_WORDS[idx]
                hm[FIELDS[1]] = EXAMPLE_TRANSLATIONS[idx]
                data.add(hm)
            }
            return data
        }
}