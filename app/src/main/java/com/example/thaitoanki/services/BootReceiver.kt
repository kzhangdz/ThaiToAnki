package com.example.thaitoanki.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/*
Class for starting the notification service whenever the device boots up, without needed to open the app.
 */

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        //context.startFloatingService()
    }

}