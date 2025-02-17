package com.example.thaitoanki.services

import android.content.res.Configuration

interface OrientationConfigurationListener {
    fun onConfigurationChanged(orientation: Configuration): Unit
}