package com.example.thaitoanki.ui.screens.components

import android.util.Log
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

//https://medium.com/@astamato/swipe-and-savor-building-a-swipeable-snackbar-in-compose-c696cbe72135
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AppSwipeableSnackbarWrapper(
    state: SnackbarHostState,
    modifier: Modifier = Modifier,
    dismissSnackbarState: DismissState = rememberDismissState { value ->
        if (value != DismissValue.Default) {
            state.currentSnackbarData?.dismiss()
            true
        } else {
            false
        }
    },
    dismissContent: @Composable RowScope.() -> Unit
) {
    LaunchedEffect(dismissSnackbarState.currentValue) {
        if (dismissSnackbarState.currentValue != DismissValue.Default) {
            dismissSnackbarState.reset()
        }
    }
    SwipeToDismiss(
        modifier = modifier,
        state = dismissSnackbarState,
        background = {},
        dismissContent = dismissContent,
    )
}

suspend fun handleSnackbar(message: String, snackbarHostState: SnackbarHostState){
    val snackbarResult = snackbarHostState.showSnackbar(
        message = message,
        actionLabel = "Dismiss",
        duration = SnackbarDuration.Short
    )
    when (snackbarResult) {
        SnackbarResult.Dismissed -> Log.d("Snackbar", "Snackbar dismissed")
        SnackbarResult.ActionPerformed -> snackbarHostState.currentSnackbarData?.dismiss()
    }
}