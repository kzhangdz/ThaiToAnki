package com.example.thaitoanki.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thaitoanki.R
import com.example.thaitoanki.network.Definition

// TODO: add animation for flipping card.
// The flip attribute does not need to be in the view model
// It should live entirely on this page

@Composable
fun FlashcardScreen(
    loadingStatus: LoadingStatus,
    flashcardInfo: List<Definition>,
    modifier: Modifier = Modifier,
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if(loadingStatus == LoadingStatus.Loading){
            LoadingScreen()
        }
        else if(loadingStatus == LoadingStatus.Error){
            // TODO: change to error screen
            Text("Temp")
        }
        else{
            if(flashcardInfo.isEmpty()){
                Text("No definition found")
            }
            else {
                // TODO: set hard height for flashcard
                Flashcard(
                    flashcardInfo = flashcardInfo,
                    modifier = Modifier
                        .height(250.dp)
                        .fillMaxWidth()
                )
            }
            Spacer(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
            FlashcardScreenButtonGroup(

            )
        }
    }
}

@Composable
fun Flashcard(
    flashcardInfo: List<Definition>,
    modifier: Modifier = Modifier
){
    var isRotated by rememberSaveable { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isRotated) 180f else 0f,
        animationSpec = tween(500)
    )

    val animateFront by animateFloatAsState(
        targetValue = if (!isRotated) 1f else 0f,
        animationSpec = tween(500)
    )

    val animateBack by animateFloatAsState(
        targetValue = if (isRotated) 1f else 0f,
        animationSpec = tween(500)
    )

    // TODO: make the column scrollable? or clickable to expand
    Box(
        modifier = Modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 8 * density
            }
            .clickable {
                isRotated = !isRotated
            }
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = modifier
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
                    .fillMaxSize()
            ) {
                if (isRotated) {
                    FlashcardBack(
                        flashcardInfo,
                        modifier = Modifier
                            .graphicsLayer {
                                alpha = animateBack
                                rotationY = rotation
                            }
                    )
                } else {
                    FlashcardFront(
                        flashcardInfo,
                        modifier = Modifier
                            .graphicsLayer {
                                alpha = animateFront
                                rotationY = rotation
                            })
                }
            }
        }
    }
}

@Composable
fun FlashcardFront(
    flashcardInfo: List<Definition>,
    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
        ) {
        Text(
            flashcardInfo[0].baseWord,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )
    }
}

@Composable
fun FlashcardBack(
    flashcardInfo: List<Definition>,
    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            flashcardInfo[0].partOfSpeech
        )
        Text(
            flashcardInfo[0].definition
        )
        //TODO: dropdown menu for sentences
        Text(
            flashcardInfo[0].sentences[0].baseWord
        )
    }
}

@Composable
fun FlashcardScreenButtonGroup(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
    ){
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.upload_to_anki_button))
        }
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.save_locally_button))
        }
        OutlinedButton(
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.cancel))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlashcardScreenPreview() {
    FlashcardScreen(
        flashcardInfo = listOf(Definition("test", "")),
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_medium))
            .verticalScroll(rememberScrollState()),
        loadingStatus = LoadingStatus.Success
    )
}

@Preview(showBackground = true)
@Composable
fun FlashcardScreenBackPreview() {
    FlashcardScreen(
        flashcardInfo = listOf(Definition("test", "")),
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_medium))
            .verticalScroll(rememberScrollState()),
        loadingStatus = LoadingStatus.Success
    )
}