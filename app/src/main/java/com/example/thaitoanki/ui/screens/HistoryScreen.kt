package com.example.thaitoanki.ui.screens

import android.util.Log
import android.widget.TextView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import com.example.thaitoanki.R
import com.example.thaitoanki.network.Definition
import com.example.thaitoanki.services.toAnnotatedString

const val LOG_TAG = "HistoryScreen"

@Composable
fun HistoryScreen(
    definitions: List<Definition>,
    onWordClick: (String) -> Unit, 
    onNavigationButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO: switch to LazyVerticalGrid if horizontal
    //https://stackoverflow.com/questions/72546737/lazycolumn-inside-vertically-scrollable-column
    if(definitions.isEmpty()){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            Text(stringResource(R.string.no_history))
        }
    }
    else {
        LazyColumn(
            //contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small)),
            modifier = modifier
        ) {
            items(definitions) { definition ->
                DefinitionRow(
                    definition,
                    modifier = Modifier
                        .padding(
                            bottom = dimensionResource(R.dimen.padding_small) / 2
                        )
                        .clickable {
                            onWordClick(definition.baseWord)
                            Log.d(LOG_TAG, "${definition.baseWord} clicked")
                        }
                )
            }
        }
    }
}

@Composable
fun DefinitionRow(
    definition: Definition,
    modifier: Modifier = Modifier
){
    Card(
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
                    .fillMaxWidth()
            ) {
                DefinitionInfo(
                    word = definition.baseWord,
                    romanization = definition.romanization
                )
                Spacer(modifier = Modifier.weight(1.0f))
                // TODO: limit on text size, ellipses, clickable for alert
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    stringResource(R.string.view_definition_button)
                )
            }
        }
    }
}

@Composable
fun DefinitionInfo(
    word: String,
    romanization: String,
    modifier: Modifier = Modifier
){
    val html = HtmlCompat.fromHtml(romanization, HtmlCompat.FROM_HTML_MODE_COMPACT)
    val annotatedRomanization = html.toAnnotatedString()

    Row(
        //verticalAlignment = Alignment.Bottom,
        modifier = modifier
    ) {
        Text(
            word,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.alignByBaseline()
        )
        Spacer(
            modifier = Modifier.width(4.dp)
        )
        Text(
            // TODO: convert string to AnnotatedString, need to replace <sup> tags with a SpanStyle that will denote superscript
            annotatedRomanization,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alignByBaseline()
        )
    }
}

@Composable
fun HtmlText(html: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context -> TextView(context) },
        update = { it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT) }
    )
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    HistoryScreen(
        definitions = listOf(
            Definition("เฉย", "test", "cheeuy<sup>R</sup> chaa<sup>M</sup>"),
            Definition("ฟหกด่าส.ว", "test2", "rom2")
        ),
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium)),
        onNavigationButtonClick = {},
        onWordClick = {}
    )
}