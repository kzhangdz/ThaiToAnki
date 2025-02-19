package com.example.thaitoanki.services.windows

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.LinearLayout
import com.example.thaitoanki.services.listeners.KeyEventListener
import com.example.thaitoanki.services.listeners.OrientationConfigurationListener

/*
Wrapper class to be used in XML, i.e. <WindowContentLayout></WindowContentLayout>
Acts as a LinearLayout that can take in a listener for actions on the window
 */

class WindowContentLayout : LinearLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var listener: ((activate: Boolean) -> Unit)? = null

    private var orientationListener: ((orientation: Configuration) -> Unit)? = null

    private var keyEventListener: ((keyCode: Int, event: KeyEvent) -> Unit)? = null

    fun setListener(listener: (activate: Boolean) -> Unit) {
        this.listener = listener
    }

    fun setOrientationListener(orientationListener: (orientation: Configuration) -> Unit){
        this.orientationListener = orientationListener
    }

    fun setKeyEventListener(keyEventListener: (keyCode: Int, event: KeyEvent) -> Unit){
        this.keyEventListener = keyEventListener
    }

    /**
     * From LinearLayout
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) listener?.invoke(true)
        if (event.action == MotionEvent.ACTION_OUTSIDE) listener?.invoke(false)
        return super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) listener?.invoke(true)
        if (event.action == MotionEvent.ACTION_OUTSIDE) listener?.invoke(false)
        return super.onInterceptTouchEvent(event)
    }

    /**
     * From implementing OrientationConfigurationListener
     *
     * Seems to be overriding something from View actually
     */
    override fun onConfigurationChanged(orientation: Configuration) {
        orientationListener?.invoke(orientation)
        super.onConfigurationChanged(orientation)
    }

    /**
     * From implementing KeyEventListener
     *
     * Seems to be overriding something from View actually
     */
    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        keyEventListener?.invoke(keyCode, event)
        super.onKeyUp(keyCode, event)
        return true
    }

}