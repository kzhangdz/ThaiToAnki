package com.example.thaitoanki.services.listeners

import android.view.KeyEvent

interface KeyEventListener {
    fun onKeyPress(keyCode: Int, event: KeyEvent): Unit
}