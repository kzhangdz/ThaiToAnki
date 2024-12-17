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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.alorma.compose.settings.ui.SettingsSwitch
import com.example.thaitoanki.R
import com.example.thaitoanki.network.Definition

//https://stackoverflow.com/questions/68718655/building-a-preference-screen-with-android-jetpack-compose
//https://github.com/zhanghai/ComposePreference

//https://tomas-repcik.medium.com/making-extensible-settings-screen-in-jetpack-compose-from-scratch-2558170dd24d
// For permanent storage, you would pass the new value to the SharedPreferences or DataStore in ViewModel method.
// will need to look into loading settings on app load. likely can check during the onCreate()

//https://github.com/alorma/Compose-Settings

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
){
    Column(
        //verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        SettingsSwitch(
            state = false, // true or false
            title = { Text(text = stringResource(R.string.notification_settings)) },
            subtitle = { Text(text = "Setting subtitle") },
            modifier = Modifier,
            enabled = true,
            //icon = { Icon(...) },
            onCheckedChange = { newState: Boolean -> },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium))
            .verticalScroll(rememberScrollState()),
    )
}