package com.example.thaitoanki.services

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.util.Log
import android.view.OrientationEventListener
import android.view.View

fun View.registerOrientationChangeListener(
    initialPosition: () -> Point,
    positionListener: (x: Int, y: Int) -> Unit
) {
    OrientationChangeListener(context, this, initialPosition, positionListener)
}

private const val LOG_TAG = "OrientationListener"

class OrientationChangeListener(
    val context: Context,
    private val view: View,
    private val initialPosition: () -> Point,
    private val positionListener: (x: Int, y: Int) -> Unit
) : OrientationEventListener(context) {

    private var initialX = 0
    private var initialY = 0

    init{
        if(this.canDetectOrientation()){
            this.enable()
        }
    }

    /**
     * https://developer.android.com/reference/android/view/OrientationEventListener#onOrientationChanged(int)
     *
     * @param orientationDegree 0 to 359. 90 degrees when its left side is at the top
     */
    override fun onOrientationChanged(orientationDegree: Int) {
        with(initialPosition()) {
            initialX = x
            initialY = y
        }

        // if in portrait mode and the screen is moved to be portrait
        if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
            && orientationDegree == 0){
            // switch position of x and y
            val targetX = 100
            val targetY = 100

            positionListener(targetX, targetY)
            Log.d(LOG_TAG, "Portrait. position switched to ($targetX, $targetY)")
        }
        else if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            && orientationDegree == 90){

        }
    }
}