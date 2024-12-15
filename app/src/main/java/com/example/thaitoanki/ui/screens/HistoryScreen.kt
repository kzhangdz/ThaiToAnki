package com.example.thaitoanki.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.thaitoanki.R
import com.example.thaitoanki.network.Definition

@Composable
fun HistoryScreen(
    definitions: List<Definition>,
    onNavigationButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO: switch to LazyVerticalGrid if horizontal
    //https://stackoverflow.com/questions/72546737/lazycolumn-inside-vertically-scrollable-column
    LazyColumn(
        modifier = modifier
    ) {
        items(definitions) { definition ->
            DefinitionRow(definition)
        }
    }
}

@Composable
fun DefinitionRow(
    definition: Definition,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small))
                .fillMaxWidth()
        ) {
            Text(definition.baseWord)
            Spacer(modifier = Modifier.weight(1.0f))
            // TODO: limit on text size, ellipses, clickable for alert
            Text(definition.definition)
        }
        HorizontalDivider(
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    HistoryScreen(
        definitions = listOf(
            Definition("test", "test"),
            Definition("test2", "test2")
        ),
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium))
            .verticalScroll(rememberScrollState()),
        onNavigationButtonClick = {}
    )
}