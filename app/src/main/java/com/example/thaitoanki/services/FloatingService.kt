package com.example.thaitoanki.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.thaitoanki.R
import com.example.thaitoanki.ThaiLanguageApplication
import com.example.thaitoanki.data.DefaultAppContainer
import com.example.thaitoanki.data.database.OfflineWordsRepository
import com.example.thaitoanki.data.database.WordsDatabase
import com.example.thaitoanki.services.windows.FlashcardWindow
import com.example.thaitoanki.services.windows.SearchWindow
import com.example.thaitoanki.services.windows.Window
import com.example.thaitoanki.ui.ThaiToAnkiAppBar
import com.example.thaitoanki.ui.screens.FlashcardViewModel
import com.example.thaitoanki.ui.screens.HistoryViewModel
import com.example.thaitoanki.ui.screens.ThaiViewModel
import com.example.thaitoanki.ui.thaiLanguageApplication


const val INTENT_COMMAND = "com.example.thaitoanki.COMMAND" //"com.localazy.quicknote.COMMAND"
const val INTENT_COMMAND_EXIT = "EXIT"
const val INTENT_COMMAND_NOTE = "NOTE"

private const val NOTIFICATION_CHANNEL_GENERAL = "thaitoanki_general"
private const val CODE_FOREGROUND_SERVICE = 1
private const val CODE_EXIT_INTENT = 2
private const val CODE_NOTE_INTENT = 3

// https://stackoverflow.com/questions/63405673/how-to-call-suspend-function-from-service-android
// Lifecycle service to allow use of lifecyclescope for coroutines
class FloatingService: LifecycleService(),
    // two dependencies added to allow Jetpack Compose in a service
    LifecycleOwner,
    SavedStateRegistryOwner,

    ViewModelStoreOwner
{
    // variables for showing an overlay with Jetpack Compose
    lateinit var windowManager: WindowManager
    private val _lifecycleRegistry = LifecycleRegistry(this)
    private val _savedStateRegistryController: SavedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry = _savedStateRegistryController.savedStateRegistry
    override val lifecycle: Lifecycle = _lifecycleRegistry
    private var overlayView: View? = null

    lateinit var layoutInflater: LayoutInflater
    var testView: View? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        _savedStateRegistryController.performAttach()
        _savedStateRegistryController.performRestore(null)
        //_lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        layoutInflater = this.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        testView = layoutInflater.inflate(R.layout.fragment_search_bar, null)

        // temporarily set this up here just to get it working
        overlayView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@FloatingService) // pass in a LifecycleOwner
            setViewTreeSavedStateRegistryOwner(this@FloatingService) // pass in a SavedStateRegistryOwner
            setContent {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Text("""
                        Large test text to help make sure everything is visible
                        AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                        AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                        AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                        AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                        AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                        AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                        AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                        AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                        AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                        AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                        AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                        AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                        AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                        AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                        AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                    """.trimIndent())
                }
            }
        }
        //ViewTreeLifecycleOwner.set(contentView, this)
        (overlayView as ComposeView).setViewTreeLifecycleOwner(this@FloatingService)
    }


    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    /**
     * Remove the foreground notification and stop the service.
     */
    private fun stopService() {
        stopForeground(STOP_FOREGROUND_DETACH) //stopForeground(true)
        stopSelf()
    }

    /**
     * Create and show the foreground notification.
     */
    private fun showNotification() {

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val exitIntent = Intent(this, FloatingService::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_EXIT)
        }

        val noteIntent = Intent(this, FloatingService::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_NOTE)
        }

        val exitPendingIntent = PendingIntent.getService(
            this, CODE_EXIT_INTENT, exitIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notePendingIntent = PendingIntent.getService(
            this, CODE_NOTE_INTENT, noteIntent, PendingIntent.FLAG_IMMUTABLE
        )

        // From Android O, it's necessary to create a notification channel first.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                with(
                    NotificationChannel(
                        NOTIFICATION_CHANNEL_GENERAL,
                        getString(R.string.notification_channel_general),
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                ) {
                    enableLights(false)
                    setShowBadge(false)
                    enableVibration(false)
                    setSound(null, null)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    manager.createNotificationChannel(this)
                }
            } catch (ignored: Exception) {
                // Ignore exception.
                Log.e("FloatingService", ignored.toString())
            }
        }

        with(
            NotificationCompat.Builder(
                this,
                NOTIFICATION_CHANNEL_GENERAL
            )
        ) {
            setTicker(null)
            setContentTitle(getString(R.string.app_name))
            setContentText(getString(R.string.notification_text))
            setAutoCancel(false)
            setOngoing(true)
            setWhen(System.currentTimeMillis())
            // TODO: change icon to a drawable
            setSmallIcon(R.mipmap.ic_launcher)
            priority = NotificationManager.IMPORTANCE_DEFAULT //Notification.PRIORITY_DEFAULT
            //setContentIntent(notePendingIntent)
            addAction(
                NotificationCompat.Action(
                    R.drawable.ic_connection_error,
                    getString(R.string.notification_text),
                    notePendingIntent
                )
            )
            addAction(
                NotificationCompat.Action(
                    R.drawable.ic_connection_error, //0,
                    getString(R.string.notification_exit),
                    exitPendingIntent
                )
            )
            startForeground(CODE_FOREGROUND_SERVICE, build())
            //startForegroundService()
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        super.onStartCommand(
            intent = intent,
            flags = flags,
            startId = startId,
        )

        val command = intent?.getStringExtra(INTENT_COMMAND)

        // Exit the service if we receive the EXIT command.
        // START_NOT_STICKY is important here, we don't want
        // the service to be relaunched.
        if (command == INTENT_COMMAND_EXIT) {
            // TODO: don't remove if it doesn't exist
            windowManager.removeView(testView)

            stopService()

            return START_NOT_STICKY
        }

        // Be sure to show the notification first for all commands.
        // Don't worry, repeated calls have no effects.
        showNotification()

        // Show the floating window for adding a new note.
        if (command == INTENT_COMMAND_NOTE) {
            if (!drawOverOtherAppsEnabled()) {
                //startMainActivity()

                // navigate to the overlay permission settings
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse(
                        "package:$packageName"
                    )
                )
                // needed to call startActivity outside of an activity
                // https://stackoverflow.com/questions/3918517/calling-startactivity-from-outside-of-an-activity-context
                // TODO: maybe switch this to using activity context?
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent)

                Toast.makeText(
                    this,
                    "Please enable overlay permissions from the Settings",
                    Toast.LENGTH_LONG
                ).show()

                //startPermissionActivity()
            }
            else {
                // not working
                //windowManager.addView(overlayView, getLayoutParams())

                // working
                //windowManager.addView(testView, getLayoutParams())

                // TODO: Do the repositories need to be passed using dependency injection?
                //https://stackoverflow.com/questions/63766576/injecting-a-repository-into-a-service-in-android-using-hilt

                // TODO: pull these out into member variables
                val container = DefaultAppContainer(applicationContext)
                val languageRepo = container.thaiLanguageRepository //ThaiLanguageApplication().container.thaiLanguageRepository
                val wordRepo = container.wordsRepository

                val viewModelProvider = ServiceViewModelProvider(applicationContext)

                //val window = Window(this, lifecycleScope, languageRepo, wordRepo)
                //window.open()

                // Pass ContextThemeWrapper so you can inflate components with Material Themes
                //https://stackoverflow.com/questions/38712073/android-set-theme-for-view-that-created-by-service
                val window = SearchWindow(
                    ContextThemeWrapper(this, R.style.Theme_ThaiToAnki),
                    applicationContext,
                    lifecycleScope = lifecycleScope,
                    languageRepository = languageRepo,
                    wordsRepository = wordRepo
                )
                window.open()


                // TODO: test opening another window
                val flashcardWindow = FlashcardWindow(
                    ContextThemeWrapper(this, R.style.Theme_ThaiToAnki),
                    applicationContext,
                    lifecycleScope = lifecycleScope,
                    languageRepository = languageRepo,
                    wordsRepository = wordRepo
                )
                flashcardWindow.open()
            }
        }

        return START_STICKY
    }

    private fun getLayoutParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,//WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,//WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.OPAQUE
            //PixelFormat.TRANSLUCENT
        )
    }

    override val viewModelStore: ViewModelStore = ViewModelStore()

    /*
    @NonNull ViewModelStore getViewModelStore(@NonNull Fragment f) {
        ViewModelStore viewModelStore = mViewModelStores.get(f.mWho);
        if (viewModelStore == null) {
            viewModelStore = new ViewModelStore();
            mViewModelStores.put(f.mWho, viewModelStore);
        }
        return viewModelStore;
    }
     */
}

class ServiceViewModelProvider(applicationContext: Context) {
    val Factory = viewModelFactory {

        val container = DefaultAppContainer(applicationContext)
        val languageRepo = container.thaiLanguageRepository //ThaiLanguageApplication().container.thaiLanguageRepository

        val wordRepo = container.wordsRepository
        // Initializer for ThaiViewModel
        initializer {
            ThaiViewModel(languageRepo, wordRepo)
        }
        // Initializer for FlashcardViewModel
        initializer {
            FlashcardViewModel(this.createSavedStateHandle(), wordRepo)
        }
        // Initializer for HistoryViewModel
        initializer {
            HistoryViewModel(wordRepo)
        }
    }
}