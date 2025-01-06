package com.example.thaitoanki.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.app.NotificationCompat
import com.example.thaitoanki.R


const val INTENT_COMMAND = "com.example.thaitoanki.COMMAND" //"com.localazy.quicknote.COMMAND"
const val INTENT_COMMAND_EXIT = "EXIT"
const val INTENT_COMMAND_NOTE = "NOTE"

private const val NOTIFICATION_CHANNEL_GENERAL = "thaitoanki_general"
private const val CODE_FOREGROUND_SERVICE = 1
private const val CODE_EXIT_INTENT = 2
private const val CODE_NOTE_INTENT = 3

class FloatingService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    /**
     * Remove the foreground notification and stop the service.
     */
    private fun stopService() {
        stopForeground(STOP_FOREGROUND_DETACH) //stopForeground(true)
        stopSelf()
    }

    /**
     * Create and show the foreground notification.
     */
    private fun showNotification() {

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val exitIntent = Intent(this, FloatingService::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_EXIT)
        }

        val noteIntent = Intent(this, FloatingService::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_NOTE)
        }

        val exitPendingIntent = PendingIntent.getService(
            this, CODE_EXIT_INTENT, exitIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notePendingIntent = PendingIntent.getService(
            this, CODE_NOTE_INTENT, noteIntent, PendingIntent.FLAG_IMMUTABLE
        )

        // From Android O, it's necessary to create a notification channel first.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                with(
                    NotificationChannel(
                        NOTIFICATION_CHANNEL_GENERAL,
                        getString(R.string.notification_channel_general),
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                ) {
                    enableLights(false)
                    setShowBadge(false)
                    enableVibration(false)
                    setSound(null, null)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    manager.createNotificationChannel(this)
                }
            } catch (ignored: Exception) {
                // Ignore exception.
                Log.e("FloatingService", ignored.toString())
            }
        }

        with(
            NotificationCompat.Builder(
                this,
                NOTIFICATION_CHANNEL_GENERAL
            )
        ) {
            setTicker(null)
            setContentTitle(getString(R.string.app_name))
            setContentText(getString(R.string.notification_text))
            setAutoCancel(false)
            setOngoing(true)
            setWhen(System.currentTimeMillis())
            // TODO: change icon to a drawable
            setSmallIcon(R.mipmap.ic_launcher)
            priority = NotificationManager.IMPORTANCE_DEFAULT //Notification.PRIORITY_DEFAULT
            //setContentIntent(notePendingIntent)
            addAction(
                NotificationCompat.Action(
                    R.drawable.ic_connection_error,
                    getString(R.string.notification_text),
                    notePendingIntent
                )
            )
            addAction(
                NotificationCompat.Action(
                    R.drawable.ic_connection_error, //0,
                    getString(R.string.notification_exit),
                    exitPendingIntent
                )
            )
            startForeground(CODE_FOREGROUND_SERVICE, build())
            //startForegroundService()
        }

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val command = intent.getStringExtra(INTENT_COMMAND)

        // Exit the service if we receive the EXIT command.
        // START_NOT_STICKY is important here, we don't want
        // the service to be relaunched.
        if (command == INTENT_COMMAND_EXIT) {
            stopService()
            return START_NOT_STICKY
        }

        // Be sure to show the notification first for all commands.
        // Don't worry, repeated calls have no effects.
        showNotification()

        // Show the floating window for adding a new note.
        if (command == INTENT_COMMAND_NOTE) {
            if (!drawOverOtherAppsEnabled()) {
                //startMainActivity()

                // navigate to the overlay permission settings
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse(
                        "package:$packageName"
                    )
                )
                // needed to call startActivity outside of an activity
                // https://stackoverflow.com/questions/3918517/calling-startactivity-from-outside-of-an-activity-context
                // TODO: maybe switch this to using activity context?
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent)

                Toast.makeText(
                    this,
                    "Please enable overlay permissions from the Settings",
                    Toast.LENGTH_LONG
                ).show()

                //startPermissionActivity()
            }
            else {
                Toast.makeText(
                    this,
                    // TODO: add the floating window
                    "Floating window to be added in the next lessons.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return START_STICKY
    }

}