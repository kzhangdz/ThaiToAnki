package com.example.thaitoanki

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.thaitoanki.services.startFloatingService
import com.example.thaitoanki.ui.ThaiToAnkiApp
import com.example.thaitoanki.ui.theme.ThaiToAnkiTheme

/*
TODO: implement floating window
https://medium.com/localazy/floating-windows-on-android-1-jetpack-compose-room-a4f377c86dd5
 */

/*
TODO:
Left off on error message "Background activity launch blocked! "
which occurs after trying to open the app from the notification bar, on startMainActivity()
 */

/*
TODO:
Next steps list:
1. connect with Anki
2. save searched entries in the Room db, display on the History screen
3. Implement the floating window, which will communicate with the room db, grabbing the latest searched definitions
 */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        startFloatingService()

        setContent {
            ThaiToAnkiTheme {
                ThaiToAnkiApp()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}