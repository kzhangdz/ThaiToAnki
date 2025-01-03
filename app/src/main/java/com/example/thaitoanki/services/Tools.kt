package com.example.thaitoanki.services

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.provider.Settings
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.UnderlineSpan
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift

import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
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

/*

Reads asset files as strings. Primarily used for the html and css for Anki, located in the assets folder for

https://stackoverflow.com/questions/9544737/read-file-from-assets
Adding Context. allows us to call it as an extension function of context
i.e. context.readTextFromAsset("my file name")
assets is usually the function getAssets()
*/
fun Context.readTextFromAsset(fileName : String) : String{
    return assets.open(fileName).bufferedReader().use {
        it.readText()}
}

fun Context.readArrayFromAsset(fileName : String) : Array<String>{
    val values: MutableList<String> = mutableListOf()
    val bReader = assets.open(fileName).bufferedReader()

    var line: String = bReader.readLine()
    while (line != null) {
        values.add(line)
        line = bReader.readLine()
    }
    bReader.close()

    return values.toTypedArray()
}

// TODO: check if this does not result in overlaps
// write a unit test for it
public fun String?.indexesOf(substr: String, ignoreCase: Boolean = true): List<Pair<Int,Int>> {
    return this?.let {
        val regex = if (ignoreCase) Regex(substr, RegexOption.IGNORE_CASE) else Regex(substr)
        regex.findAll(this).map {
            Pair(it.range.start, it.range.last)
        }.toList()
    } ?: emptyList()
}

// convert HTML to AnnotatedString, which can be displayed in a Text composable
fun Spanned.toAnnotatedString(): AnnotatedString = buildAnnotatedString {
    val spanned = this@toAnnotatedString
    append(spanned.toString())
    getSpans(0, spanned.length, Any::class.java).forEach { span ->
        val start = getSpanStart(span)
        val end = getSpanEnd(span)
        when (span) {
            is StyleSpan -> when (span.style) {
                Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                Typeface.BOLD_ITALIC -> addStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic), start, end)
            }
            is UnderlineSpan -> addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
            is ForegroundColorSpan -> addStyle(SpanStyle(color = Color(span.foregroundColor)), start, end)
            is SuperscriptSpan -> addStyle(SpanStyle(
                baselineShift = BaselineShift.Superscript,
                fontSize = 12.sp
            ), start, end)
            is SubscriptSpan -> addStyle(SpanStyle(
                baselineShift = BaselineShift.Subscript,
                fontSize = 12.sp
            ), start, end)
        }
    }
}