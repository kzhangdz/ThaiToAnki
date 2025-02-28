package com.example.thaitoanki.services.windows

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Point
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.thaitoanki.R
import com.example.thaitoanki.data.network.ThaiLanguageRepository
import com.example.thaitoanki.data.database.WordsRepository
import com.example.thaitoanki.services.ServiceViewModelProvider
import com.example.thaitoanki.services.listeners.registerDraggableTouchListener
import com.example.thaitoanki.ui.screens.ThaiViewModel
import kotlinx.coroutines.launch


class MinimizedWindow(
    context: ContextWrapper,
    override val serviceContext: Context,
    override val applicationContext: Context,
): Window(
    context = context,
    serviceContext = serviceContext,
    applicationContext = applicationContext,
    layoutId = R.layout.window_minimized,
    windowWidth = 300,
    windowHeight = 80
) {

    override fun initWindow() {
        // super init for window commands, like closing and minimizing
        super.initWindow()

        rootView.findViewById<View>(R.id.minimized_window)?.registerDraggableTouchListener(
            initialPosition = { Point(windowParams.x, windowParams.y) },
            positionListener = { x, y -> setPosition(x, y) }
        )

        rootView.findViewById<View>(R.id.minimized_icon).registerDraggableTouchListener(
            initialPosition = { Point(windowParams.x, windowParams.y) },
            positionListener = { x, y -> setPosition(x, y) }
        )

        rootView.findViewById<View>(R.id.minimized_icon).setOnClickListener{

            // code to bring the hidden view back
        }
    }

    init {
        //initWindowParams()
        initWindow()
    }
}