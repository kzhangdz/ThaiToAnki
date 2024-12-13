package com.example.thaitoanki.ui.screens

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.thaitoanki.R
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    searchValue: String,
    onSearchValueChanged: (String) -> Unit,
    onSearchButtonClicked: () -> Unit,
    onKeyboardSearch: () -> Unit,
    onClearButtonClicked: () -> Unit,
    onDragUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    //var offsetX by remember { mutableStateOf(0f) }
    //var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                //.offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit){
                    detectDragGestures { change, dragAmount ->
                        // TODO: dragging is too sensitive. creates multiple pages. switch to a verticalpager?
                        change.consume()

                        val (x,y) = dragAmount
                        when {
                            x > 0 ->{ /* right */ }
                            x < 0 ->{ /* left */ }
                        }
                        when {
                            y > 0 -> { /* down */ }
                            y < 0 -> {
                                /* up */
                                onDragUp()
                            }
                        }

                        //offsetX += dragAmount.x
                        //offsetY += dragAmount.y
                    }
                }
        )
        {
            SearchForm(
                searchValue = searchValue,
                onSearchValueChanged = onSearchValueChanged,
                onSearchButtonClicked = onSearchButtonClicked,
                onKeyboardSearch = onKeyboardSearch,
                onClearButtonClicked = onClearButtonClicked,
                modifier = Modifier
//                .padding(
//                    horizontal = dimensionResource(R.dimen.padding_medium)
//                )
                    .fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchForm(
    searchValue: String,
    onSearchValueChanged: (String) -> Unit,
    onSearchButtonClicked: () -> Unit,
    onKeyboardSearch: () -> Unit,
    onClearButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    )
    {
        // TODO: change this into a SearchBar
        // TODO: null/"" validation
        OutlinedTextField(
            value = searchValue,
            label = { Text(text = stringResource(R.string.search_bar_label)) },
            shape = RoundedCornerShape(50.dp),
            singleLine = true,
            trailingIcon = {
                IconButton(
                    enabled = searchValue != "",
                    onClick = {
                        onClearButtonClicked()
                    }
                ) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = stringResource(R.string.clear)
                    )
                }
                           },
            onValueChange = onSearchValueChanged,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onKeyboardSearch() }
            ),
            modifier = Modifier.fillMaxWidth()
        )
//        SearchBar(
//            query = "",
//            onQueryChange = {},
//            onSearch = { },
//            active = true,
//            onActiveChange = {},
//            enabled = true
//
//        ) { }
        Spacer(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        )
        Button(
            onClick = onSearchButtonClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.search_button))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        searchValue = "",
        onSearchValueChanged = {},
        onSearchButtonClicked = {},
        onKeyboardSearch = {},
        onClearButtonClicked = {},
        onDragUp = {},
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_medium))
            .verticalScroll(rememberScrollState())
    )
}