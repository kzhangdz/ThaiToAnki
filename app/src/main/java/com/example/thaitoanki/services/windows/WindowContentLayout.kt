package com.example.thaitoanki.services.windows

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout

/*
Wrapper class to be used in XML, i.e. <WindowContentLayout></WindowContentLayout>
Acts as a LinearLayout that can take in a listener for actions on the window
 */

class WindowContentLayout : LinearLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var listener: ((activate: Boolean) -> Unit)? = null

    fun setListener(listener: (activate: Boolean) -> Unit) {
        this.listener = listener
    }

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

}