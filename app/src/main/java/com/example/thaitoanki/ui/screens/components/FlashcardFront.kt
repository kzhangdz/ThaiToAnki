package com.example.thaitoanki.ui.screens.components

import android.view.View
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.thaitoanki.R
import com.example.thaitoanki.data.network.Definition
import com.example.thaitoanki.data.network.TestDefinitions
import com.example.thaitoanki.ui.theme.ThaiToAnkiTheme

@Composable
fun FlashcardFront(
    flashcardInfo: List<Definition>,
    currentDefinitionIndex: Int,
    currentExampleIndices: List<Int?>,
    currentSentenceIndices: List<Int?>,
    onClick: () -> Unit = {},
    onLeftClick: () -> Unit = {},
    onRightClick: () -> Unit = {},
    onExampleClick: () -> Unit = {},
    onSentenceClick: () -> Unit = {},
    modifier: Modifier = Modifier
){
    val currentFlashcard = flashcardInfo[currentDefinitionIndex]
    val currentDefinitionExampleIndex = currentExampleIndices[currentDefinitionIndex]
    val currentSentenceIndex = currentSentenceIndices[currentDefinitionIndex]

    AndroidView(
        factory = { context ->
            View.inflate(context, R.layout.fragment_flashcard_front, null)
        },
        modifier = modifier,
        update = { view ->
            updateFlashcardFrontView(
                view,
                currentDefinitionIndex = currentDefinitionIndex,
                definitionCount = flashcardInfo.size,
                currentFlashcard,
                currentDefinitionExampleIndex,
                currentSentenceIndex,
                onClick,
                onLeftClick,
                onRightClick,
                onDefinitionClick = {},
                onExampleClick,
                onSentenceClick,
                displaySaveButton = false
            )
        }
    )
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
            currentDefinitionIndex = 2,
            currentExampleIndices = List(size = TestDefinitions.definitions.size) { 0 },
            currentSentenceIndices = List(size = TestDefinitions.definitions.size) { 0 }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FlashcardFrontBlankPreview() {
    ThaiToAnkiTheme(
        darkTheme = false
    ) {
        FlashcardFront(
            flashcardInfo = TestDefinitions.definitions,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
                .verticalScroll(rememberScrollState()),
            currentDefinitionIndex = 0,
            currentExampleIndices = List(size = TestDefinitions.definitions.size) { 0 },
            currentSentenceIndices = List(size = TestDefinitions.definitions.size) { 0 }
        )
    }
}