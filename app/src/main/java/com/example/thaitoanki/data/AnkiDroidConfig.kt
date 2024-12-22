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
    val TAGS: Set<String> = HashSet(listOf("ThaiToAnki"))

    // TODO: add a full list of fields when ready
    // List of field names that will be used in AnkiDroid model
    val FIELDS: Array<String> = arrayOf(
        "Word", "Meaning"
    )

    // List of card names that will be used in AnkiDroid (one for each direction of learning)
    //val CARD_NAMES: Array<String> = arrayOf("Japanese>English", "English>Japanese")
    val CARD_NAMES: Array<String> = arrayOf("Thai>English")

    // CSS to share between all the cards (optional). User will need to install the NotoSans font by themselves
    const val CSS: String = """
        color: black;
    """
//        ".card {\n" +
//            " font-family: NotoSansJP;\n" +
//            " font-size: 24px;\n" +
//            " text-align: center;\n" +
//            " color: black;\n" +
//            " background-color: white;\n" +
//            " word-wrap: break-word;\n" +
//            "}\n" +
//            "@font-face { font-family: \"NotoSansJP\"; src: url('_NotoSansJP-Regular.otf'); }\n" +
//            "@font-face { font-family: \"NotoSansJP\"; src: url('_NotoSansJP-Bold.otf'); font-weight: bold; }\n" +
//            "\n" +
//            ".big { font-size: 48px; }\n" +
//            ".small { font-size: 18px;}\n"

    // Template for the question of each card
    const val QFMT1: String = "<div class=big>{{Word}}</div>" //"<div class=big>{{Expression}}</div><br>{{Grammar}}"
    //const val QFMT2: String = "{{Meaning}}" //"{{Meaning}}<br><br><div class=small>{{Grammar}}<br><br>({{SentenceMeaning}})</div>"
    val QFMT: Array<String> = arrayOf(QFMT1) //arrayOf(QFMT1, QFMT2)

    // Template for the answer (use identical for both sides)
    const val AFMT1: String = """
        <div class=big>{{Word}}</div>
        <br>
        {{Meaning}}
    """

//        "<div class=big>{{furigana:Furigana}}</div><br>{{Meaning}}\n" +
//            "<br><br>\n" +
//            "{{furigana:SentenceFurigana}}<br>\n" +
//            "<a href=\"#\" onclick=\"document.getElementById('hint').style.display='block';return false;\">Sentence Translation</a>\n" +
//            "<div id=\"hint\" style=\"display: none\">{{SentenceMeaning}}</div>\n" +
//            "<br><br>\n" +
//            "{{Grammar}}<br><div class=small>{{Tags}}</div>"
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