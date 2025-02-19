package com.example.thaitoanki.services.windows

import android.content.Context
import android.content.ContextWrapper
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.annotation.LayoutRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import com.example.thaitoanki.R
import com.example.thaitoanki.services.listeners.registerDraggableTouchListener


// implement this to save view models. Allows us to initialize and pass our ViewModels to the Windows. Things like ComponentActivity and Fragment implement this
open class Window(
    context: ContextWrapper,
    serviceContext: Context,
    open val applicationContext: Context,
    @LayoutRes val layoutId: Int,
    val windowWidth: Int = 300,
    val windowHeight: Int = 400
    ):
    SavedStateRegistryOwner,
    ViewModelStoreOwner {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    protected val rootView = layoutInflater.inflate(layoutId, null) as WindowContentLayout // allows us to access setListener()

    //val viewModel

    private val windowParams = WindowManager.LayoutParams(
        0,
        0,
        0,
        0,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        },
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
        PixelFormat.TRANSLUCENT
    )


    private fun getCurrentDisplayMetrics(): DisplayMetrics {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        return dm
    }


    private fun calculateSizeAndPosition(
        params: WindowManager.LayoutParams,
        widthInDp: Int,
        heightInDp: Int
    ) {
        val dm = getCurrentDisplayMetrics()
        // We have to set gravity for which the calculated position is relative.
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.width = (widthInDp * dm.density).toInt()
        params.height = (heightInDp * dm.density).toInt()
        params.x = (dm.widthPixels - params.width) / 2
        params.y = (dm.heightPixels - params.height) / 2
    }


    private fun initWindowParams() {
        calculateSizeAndPosition(windowParams, windowWidth, windowHeight)
    }

    /*
    Function to set the behavior of the window, such as buttons
     */
    open fun initWindow() {
        // Using kotlin extension for views caused error, so good old findViewById is used

        rootView.findViewById<View>(R.id.window_close).setOnClickListener { close() }

        rootView.findViewById<View>(R.id.header).registerDraggableTouchListener(
            initialPosition = { Point(windowParams.x, windowParams.y) },
            positionListener = { x, y -> setPosition(x, y) }
        )

        // orientationEventListener
//        (rootView as View).registerOrientationChangeListener(
//            initialPosition = { Point(windowParams.x, windowParams.y) },
//            positionListener = {x, y -> setPosition(x, y)}
//        )
        // todo: refactor to be configuration listener
        rootView.setOrientationListener { orientation ->

            // calculate the best position to move to based on percentages of windowParams.x and y
            // triggers after the configuration change, so the widthPercent compare x against the original width in portrait, now represented as height in landscape
            Log.d("Window", "width: ${getCurrentDisplayMetrics().widthPixels} height: ${getCurrentDisplayMetrics().heightPixels}")
            Log.d("Window", "(${windowParams.x}, ${windowParams.y})")
            val widthPercent = windowParams.x.toDouble() / getCurrentDisplayMetrics().heightPixels
            val heightPercent = windowParams.y.toDouble() / getCurrentDisplayMetrics().widthPixels

            val targetX = widthPercent * getCurrentDisplayMetrics().widthPixels
            val targetY = heightPercent * getCurrentDisplayMetrics().heightPixels

            setPosition(targetX.toInt(), targetY.toInt())
            //setPosition(windowParams.y, windowParams.x)
//            if (orientation.orientation == Configuration.ORIENTATION_PORTRAIT){
//                setPosition(100, 200)
//            }
//            else if (orientation.orientation == Configuration.ORIENTATION_LANDSCAPE){
//                setPosition(0, 0)
//            }
        }

        rootView.setListener {
            if (it) {
                enableKeyboard()
            } else {
                disableKeyboard()
            }
        }
    }


    init {
        initWindowParams()
        initWindow()
    }


    protected fun enableKeyboard() {
        if (windowParams.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE != 0) {
            windowParams.flags =
                windowParams.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
            update()
        }
    }


    protected fun disableKeyboard() {
        if (windowParams.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE == 0) {
            windowParams.flags = windowParams.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            update()
        }
    }


    protected fun setPosition(x: Int, y: Int) {
        windowParams.x = x
        windowParams.y = y
        update()
    }


    protected fun update() {
        try {
            windowManager.updateViewLayout(rootView, windowParams)
        } catch (e: Exception) {
            // Ignore exception for now, but in production, you should have some
            // warning for the user here.
        }
    }


    fun open() {
        try {
            //windowManager.addView(overlayView, getLayoutParams())

            windowManager.addView(rootView, windowParams)
        } catch (e: Exception) {
            // Ignore exception for now, but in production, you should have some
            // warning for the user here.
        }
    }

    open fun minimize(){
        // switch to a different smaller window, or set the visibility of the content to none?

        // FloatingApps takes the view off screen and adds a new view called the bubble
        // I can probably set the bubble on the left at the same y coord as before.
    }

    open fun close() {
        try {
            windowManager.removeView(rootView)
        } catch (e: Exception) {
            // Ignore exception for now, but in production, you should have some
            // warning for the user here.
        }
    }

    private fun getLayoutParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )
    }

    override val viewModelStore: ViewModelStore = ViewModelStore()


    // overrides for SavedStateRegistryOwner
    private var _lifecycle = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle
        get() = _lifecycle
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

}