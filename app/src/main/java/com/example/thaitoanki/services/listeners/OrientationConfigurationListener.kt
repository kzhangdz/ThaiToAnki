package com.example.thaitoanki.services.listeners

import android.content.res.Configuration

interface OrientationConfigurationListener {
    fun onConfigurationChanged(orientation: Configuration): Unit
}