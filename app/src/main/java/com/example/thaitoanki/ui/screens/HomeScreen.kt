package com.example.thaitoanki.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.thaitoanki.R

@Composable
fun HomeScreen(
    searchValue: String,
    onSearchValueChanged: (String) -> Unit,
    onSearchButtonClicked: () -> Unit,
    onKeyboardSearch: () -> Unit,
    onClearButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
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
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_medium))
            .verticalScroll(rememberScrollState())
    )
}