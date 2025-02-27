package com.example.thaitoanki.ui.screens

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thaitoanki.R
import com.example.thaitoanki.data.network.Definition
import com.example.thaitoanki.services.getActivity
import com.example.thaitoanki.ui.AppViewModelProvider
import com.example.thaitoanki.ui.screens.components.FlashcardFront
import kotlinx.coroutines.launch

@Composable
fun FlashcardScreen(
    loadingStatus: LoadingStatus,
    onSuccessfulFlashcardSave: () -> Unit,
    onFailedFlashcardSave: () -> Unit,
    onCancelButtonClicked: () -> Unit,
    errorRetryAction: () -> Unit,
    modifier: Modifier = Modifier,
    flashcardViewModel: FlashcardViewModel = viewModel(factory = AppViewModelProvider.Factory),
){

    val context = LocalContext.current

    val flashcardDefinitions by flashcardViewModel.definitionsState.collectAsState()
    val flashcardUiState by flashcardViewModel.uiState.collectAsState()

    // using the retrieved definition data from the repository, set the flashcardUiState
    flashcardViewModel.updateDefinitions(flashcardDefinitions)
    if(flashcardUiState.currentExampleIndices.isEmpty()){
        flashcardViewModel.initializeExampleIndices()
    }
    if(flashcardUiState.currentSentenceIndices.isEmpty()){
        flashcardViewModel.initializeSentenceIndices()
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if(loadingStatus == LoadingStatus.Loading){
            LoadingScreen()
        }
        else if(loadingStatus == LoadingStatus.Error){
            ErrorScreen(
                retryAction = errorRetryAction
            )
        }
        else{
            if(flashcardUiState.currentDefinitions.isEmpty()){
                Text("No definitions found")
            }
            else {
                Flashcard(
                    flashcardInfo = flashcardUiState.currentDefinitions,
                    currentDefinitionIndex = flashcardUiState.currentDefinitionIndex, //flashcardViewModel.currentDefinitionIndex,
                    currentExampleIndices = flashcardUiState.currentExampleIndices,
                    currentSentenceIndices = flashcardUiState.currentSentenceIndices,
                    onFlashcardClick = {
                        flashcardViewModel.increaseCurrentDefinitionIndex()
                    },
                    onLeftButtonClick = {
                        flashcardViewModel.decreaseCurrentDefinitionIndex()
                                        }, //onLeftButtonClick,
                    onRightButtonClick = {
                        flashcardViewModel.increaseCurrentDefinitionIndex()
                                         }, //onRightButtonClick,
                    onExampleClick = {
                        // viewModel function to increase the current example index
                        flashcardViewModel.increaseCurrentExampleIndex()
                    },
                    onSentenceClick = {
                        flashcardViewModel.increaseCurrentSentenceIndex()
                    },
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth()
                )
            }
            Spacer(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
            FlashcardScreenButtonGroup(
                //onSaveFlashcardButtonClick = onSaveFlashcardButtonClick
                onSaveFlashcardButtonClick = {
                    val responseCode = if (flashcardUiState.currentDefinitions.isNotEmpty()) flashcardViewModel.saveCard(
                        context
                        // TODO: add deckname, modelname?
                    ) else 0

                    if (responseCode > 0){
                        onSuccessfulFlashcardSave()
                    }
                    else{
                        onFailedFlashcardSave()
                    }
                },
                onCancelButtonClicked = onCancelButtonClicked
            )
        }
    }
}

@Composable
fun Flashcard(
    flashcardInfo: List<Definition>,
    currentDefinitionIndex: Int,
    currentExampleIndices: List<Int?>,
    currentSentenceIndices: List<Int?>,
    onFlashcardClick: () -> Unit,
    onLeftButtonClick: () -> Unit,
    onRightButtonClick: () -> Unit,
    onExampleClick: () -> Unit,
    onSentenceClick: () -> Unit,
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

    FlashcardFront(
        flashcardInfo,
        currentDefinitionIndex,
        currentExampleIndices,
        currentSentenceIndices,
        onClick = onFlashcardClick,
        onLeftClick = onLeftButtonClick,
        onRightClick = onRightButtonClick,
        onExampleClick = onExampleClick,
        onSentenceClick = onSentenceClick,
        modifier = Modifier
            .graphicsLayer {
                alpha = animateFront
                rotationY = rotation
            }
    )
}

//@Composable
//fun Flashcard(
//    flashcardInfo: List<Definition>,
//    currentDefinitionIndex: Int,
//    onFlashcardClick: () -> Unit,
//    onLeftButtonClick: () -> Unit,
//    onRightButtonClick: () -> Unit,
//    modifier: Modifier = Modifier
//){
//    var isRotated by rememberSaveable { mutableStateOf(false) }
//
//    val rotation by animateFloatAsState(
//        targetValue = if (isRotated) 180f else 0f,
//        animationSpec = tween(500)
//    )
//
//    val animateFront by animateFloatAsState(
//        targetValue = if (!isRotated) 1f else 0f,
//        animationSpec = tween(500)
//    )
//
//    val animateBack by animateFloatAsState(
//        targetValue = if (isRotated) 1f else 0f,
//        animationSpec = tween(500)
//    )
//
//    // TODO: make the column scrollable? or clickable to expand
//    Box(
//        modifier = modifier
//    ) {
//        Card(
//            colors = CardDefaults.cardColors(
//                containerColor = MaterialTheme.colorScheme.secondaryContainer,
//            ),
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(300.dp)
//
//        ) {
//            // push action row to bottom
//            Spacer(modifier = Modifier.weight(1f))
//            // TODO: refactor FlashcardActions into composable
//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                IconButton(
//                    onClick = {
//                        isRotated = !isRotated
//                    }
//                ) {
//                    Icon(
//                        painter = painterResource(R.drawable.ic_flip),
//                        contentDescription = "Flip flashcard"
//                    )
//                }
//                IconButton(
//                    onClick = onLeftButtonClick
//                ) {
//                    Icon(
//                        painter = painterResource(R.drawable.arrow_left_24px),
//                        contentDescription = "Previous definition"
//                    )
//                }
//                IconButton(
//                    onClick = onRightButtonClick
//                ) {
//                    Icon(
//                        painter = painterResource(R.drawable.arrow_right_24px),
//                        contentDescription = "Next definition"
//                    )
//                }
//                Spacer(modifier = Modifier.weight(1f))
//                // TODO: tap to jump to page?
//                Text(
//                    "${currentDefinitionIndex + 1} of ${flashcardInfo.size}",
//                    modifier = Modifier
//                        .padding(
//                            horizontal = dimensionResource(R.dimen.padding_small)
//                        )
//                )
//            }
//        }
//        Box(
//            modifier = Modifier
//                .graphicsLayer {
//                    rotationY = rotation
//                    cameraDistance = 8 * density
//                }
//                .clickable {
//                    //isRotated = !isRotated
//                    onFlashcardClick()
//                }
//        ) {
//            ElevatedCard(
////                colors = CardDefaults.cardColors(
////                    containerColor = MaterialTheme.colorScheme.primaryContainer,
////                ),
//                elevation = CardDefaults.cardElevation(
//                    defaultElevation = 6.dp
//                ),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(250.dp)
//            ) {
//                Column(
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier
//                        .padding(dimensionResource(R.dimen.padding_small))
//                        .fillMaxWidth()
//                        .weight(1f) // take up remaining space
//                    //.fillMaxSize()
//                ) {
//                    if (isRotated) {
//                        FlashcardBack(
//                            flashcardInfo,
//                            currentDefinitionIndex,
//                            modifier = Modifier
//                                .graphicsLayer {
//                                    alpha = animateBack
//                                    rotationY = rotation
//                                }
//                        )
//                    } else {
//                        FlashcardFront(
//                            flashcardInfo,
//                            currentDefinitionIndex,
//                            modifier = Modifier
//                                .graphicsLayer {
//                                    alpha = animateFront
//                                    rotationY = rotation
//                                })
//                    }
//                    //Spacer(modifier = Modifier.weight(1.0f))
//                }
////                Row(
////                    verticalAlignment = Alignment.CenterVertically,
////                    modifier = Modifier
////                        .padding(horizontal = dimensionResource(R.dimen.padding_small))
////                        .padding(bottom = dimensionResource(R.dimen.padding_small))
////                        .height(25.dp)
////                        .fillMaxWidth()
////                    //.padding(dimensionResource(R.dimen.padding_medium))
////                ) {
////                    IconButton(
////                        onClick = {
////                            isRotated = !isRotated
////                        }
////                    ) {
////                        Icon(
////                            painter = painterResource(R.drawable.ic_flip),
////                            contentDescription = "Flip flashcard",
////                            modifier = Modifier
////                                .graphicsLayer {
////                                    alpha = animateFront
////                                    rotationY = rotation
////                                }
////                        )
////                    }
////                    Spacer(modifier = Modifier.weight(1f))
////                    IconButton(
////                        onClick = {
////                            isRotated = !isRotated
////                        }
////                    ) {
////                        Icon(
////                            painter = painterResource(R.drawable.ic_flip),
////                            contentDescription = "Flip flashcard",
////                            modifier = Modifier
////                                .graphicsLayer {
////                                    alpha = animateBack
////                                    rotationY = rotation
////                                }
////                        )
////                    }
////                }
//            }
//        }
//    }
//}

//@Composable
//fun FlashcardFront(
//    flashcardInfo: List<Definition>,
//    currentDefinitionIndex: Int,
//    modifier: Modifier = Modifier
//){
//    val currentFlashcard = flashcardInfo[currentDefinitionIndex]
//
//    Column(
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = modifier
//        ) {
//        Text(
//            currentFlashcard.baseWord,
//            fontWeight = FontWeight.Bold,
//            fontSize = 30.sp
//        )
//    }
//}

@Composable
fun FlashcardBack(
    flashcardInfo: List<Definition>,
    currentDefinitionIndex: Int,
    modifier: Modifier = Modifier
){
    val currentFlashcard = flashcardInfo[currentDefinitionIndex]

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Column() {
                Text(
                    currentFlashcard.partOfSpeech
                )
                Text(
                    currentFlashcard.definition
                )
                //TODO: dropdown menu for sentences
                if(currentFlashcard.sentences.isNotEmpty()) {
                    Text(
                        currentFlashcard.sentences[0].baseWord
                    )
                }
            }
        }
    }
}

// TODO: button to merge flashcards into one
@Composable
fun FlashcardScreenButtonGroup(
    onSaveFlashcardButtonClick: () -> Unit,
    onCancelButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
    ){
        Button(
            onClick = onSaveFlashcardButtonClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.upload_to_anki_button))
        }
        OutlinedButton(
            onClick = {
                onCancelButtonClicked()
            },
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
        loadingStatus = LoadingStatus.Success,
//        flashcardInfo = listOf(Definition("test", "")),
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_medium))
            .verticalScroll(rememberScrollState()),
//        currentDefinitionIndex = 0,
        //        onLeftButtonClick = {},
//        onRightButtonClick = {},
        //onSaveFlashcardButtonClick = {},
        errorRetryAction = {},
        onSuccessfulFlashcardSave = {},
        onFailedFlashcardSave = {},
        onCancelButtonClicked = {},
    )
}

@Preview(showBackground = true)
@Composable
fun FlashcardScreenBackPreview() {
    FlashcardScreen(
        loadingStatus = LoadingStatus.Success,
//        flashcardInfo = listOf(
//            Definition("back", "back"),
//            Definition("back2", "back2")
//        ),
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_medium))
            .verticalScroll(rememberScrollState()),
//        currentDefinitionIndex = 0,
        //        onLeftButtonClick = {},
//        onRightButtonClick = {},
        //onSaveFlashcardButtonClick = {},
        errorRetryAction = {},
        onSuccessfulFlashcardSave = {},
        onFailedFlashcardSave = {},
        onCancelButtonClicked = {},
    )
}