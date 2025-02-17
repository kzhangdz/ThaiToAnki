package com.example.thaitoanki.services.windows

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout
import com.example.thaitoanki.services.OrientationConfigurationListener

/*
Wrapper class to be used in XML, i.e. <WindowContentLayout></WindowContentLayout>
Acts as a LinearLayout that can take in a listener for actions on the window
 */

class WindowContentLayout : LinearLayout, OrientationConfigurationListener {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var listener: ((activate: Boolean) -> Unit)? = null

    private var orientationListener: ((orientation: Configuration) -> Unit)? = null

    fun setListener(listener: (activate: Boolean) -> Unit) {
        this.listener = listener
    }

    fun setOrientationListener(orientationListener: (orientation: Configuration) -> Unit){
        this.orientationListener = orientationListener
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
     */
    override fun onConfigurationChanged(orientation: Configuration) {
        orientationListener?.invoke(orientation)
        super.onConfigurationChanged(orientation)
    }

}