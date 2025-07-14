package com.example.thaitoanki.services.windows

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Point
import android.view.*
import com.example.thaitoanki.R
import com.example.thaitoanki.services.listeners.registerDraggableTouchListener


class MinimizedWindow(
    context: ContextWrapper,
    override val serviceContext: Context,
    override val applicationContext: Context,
    val onClick: (MinimizedWindow) -> Unit = {},
    startingX: Int? = null,
    startingY: Int? = null
): Window(
    context = context,
    serviceContext = serviceContext,
    applicationContext = applicationContext,
    layoutId = R.layout.window_minimized,
    windowWidth = 52,//80, //1000,//300, // if we use a width that's too large, the window will have a giant blank section that stops when it hits the right edge. Therefore, we only want a window just as big as the icon itself
    windowHeight = 52,//80, //1000,//80,
    inScreen = true, // minimized bubble will always stay on screen
    startingX = startingX,
    startingY = startingY
) {

    override fun initWindow() {
        // super init for window commands, like closing and minimizing
        super.initWindow()

        rootView.findViewById<View>(R.id.minimized_window)?.registerDraggableTouchListener(
            initialPosition = { Point(windowParams.x, windowParams.y) },
            positionListener = { x, y -> setPosition(x, y) },
            isMagnetized = true
        )

        rootView.findViewById<View>(R.id.minimized_icon).registerDraggableTouchListener(
            initialPosition = { Point(windowParams.x, windowParams.y) },
            positionListener = { x, y -> setPosition(x, y) },
            isMagnetized = true
        )

        rootView.findViewById<View>(R.id.minimized_icon).setOnClickListener{

            // code to bring the hidden view back
            onClick(this)
        }
    }

    init {
        //initWindowParams()
        initWindow()
    }
}