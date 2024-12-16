package com.example.thaitoanki.services

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.PermissionChecker

fun Context.startFloatingService(command: String = "") {
    // Android 33 and up requires the POST_NOTIFICATIONS permission to display the notification
//    val notificationPermission =
//        PermissionChecker.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
//    if (notificationPermission != PermissionChecker.PERMISSION_GRANTED) {
//        // Without permissions, the service cannot run in the foreground
//        // Consider informing user or updating your app UI if visible.
//        //stopSelf()
//        //return
//
//        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
//    }

    val intent = Intent(this, FloatingService::class.java)
    if (command.isNotBlank()) intent.putExtra(INTENT_COMMAND, command)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        this.startForegroundService(intent)
    } else {
        this.startService(intent)
    }
}