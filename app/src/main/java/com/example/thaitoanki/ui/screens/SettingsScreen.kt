package com.example.thaitoanki.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.alorma.compose.settings.ui.SettingsSwitch
import com.example.thaitoanki.R

//https://stackoverflow.com/questions/68718655/building-a-preference-screen-with-android-jetpack-compose
//https://github.com/zhanghai/ComposePreference

//https://tomas-repcik.medium.com/making-extensible-settings-screen-in-jetpack-compose-from-scratch-2558170dd24d
// For permanent storage, you would pass the new value to the SharedPreferences or DataStore in ViewModel method.
// will need to look into loading settings on app load. likely can check during the onCreate()

//https://github.com/alorma/Compose-Settings

//https://www.youtube.com/watch?v=vUf0cIRtV8A

@Composable
fun SettingsScreen(
    hasNotificationPermission: Boolean,
    onNotificationCheckedChange: (Boolean) -> Unit,
    hasOverlayPermission: Boolean,
    onOverlayCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        SettingsSwitch(
            state = hasNotificationPermission, // true or false
            title = { Text(text = stringResource(R.string.notification_settings)) },
            subtitle = { Text(text = stringResource(R.string.notification_subtitle)) },
            modifier = Modifier
                .clickable {  },
            enabled = true,
            //icon = { Icon(...) },
            onCheckedChange = onNotificationCheckedChange //{ newState: Boolean -> },
        )
        SettingsSwitch(
            state = hasOverlayPermission, // true or false
            title = { Text(text = stringResource(R.string.overlay_settings)) },
            subtitle = { Text(text = stringResource(R.string.notification_subtitle)) },
            modifier = Modifier
                .clickable {  },
            enabled = true,
            //icon = { Icon(...) },
            onCheckedChange = onNotificationCheckedChange //{ newState: Boolean -> },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(
        hasNotificationPermission = false,
        onNotificationCheckedChange = {},
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium))
            .verticalScroll(rememberScrollState()),
        hasOverlayPermission = false,
        onOverlayCheckedChange = { },
    )
}