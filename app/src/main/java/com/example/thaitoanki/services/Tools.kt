package com.example.thaitoanki.services

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.thaitoanki.MainActivity

/*
Functions that we want to use on app start up or outside of the app, in the notifications tray

Context is the environment data for the app
base class for Activity, Service, Application, etc
 */

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

// TODO: call the check if notifications are enabled as well
//areNotificationsEnabled(NotificationManagerCompat.from(this))
//https://stackoverflow.com/questions/38198775/android-app-detect-if-app-push-notification-is-offt
fun areNotificationsEnabled(notificationManager: NotificationManagerCompat) = when {
    notificationManager.areNotificationsEnabled().not() -> false
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
        notificationManager.notificationChannels.firstOrNull { channel ->
            channel.importance == NotificationManager.IMPORTANCE_NONE
        } == null
    }
    else -> true
}

fun Context.drawOverOtherAppsEnabled(): Boolean {

    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        true
    } else {
        Settings.canDrawOverlays(this)
    }
}

fun Context.startMainActivity() {
    startActivity(
        Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    )
}

//fun Context.startPermissionActivity() {
//    startActivity(
//        Intent(this, PermissionActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        }
//    )
//}