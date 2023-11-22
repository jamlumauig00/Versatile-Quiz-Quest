package jamsx.versatile.quizquest.Activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import jamsx.versatile.quizquest.databinding.ActivityQuizwebBinding
import kotlin.math.abs

class QuizWebActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizwebBinding

    private val quizQuestTag = "quizQuestTag"
    private var quizWebView: WebView? = null
    private val quiz_IS_FULLSCREEN = "is_fullscreen_pref"
    private var quizLastTouch: Long = 0
    private val quizTouchThreshold: Long = 2000
    var quizExitTime: Long = 0
    private var quizUrl: String = ""

    @SuppressLint(
        "SetJavaScriptEnabled", "ShowToast", "ClickableViewAccessibility",
        "MissingInflatedId"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityQuizwebBinding.inflate(layoutInflater)
        setContentView(binding.root)


        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )

        quizUrl = intent.getStringExtra("category").toString()
        quizWebView = binding.wvContent
        Log.e("quizUrl", quizUrl)

        val quizIsOrientationEnabled = try {
            Settings.System.getInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION) == 1
        } catch (e: Settings.SettingNotFoundException) {
            Log.d(quizQuestTag, "Settings could not be loaded")
            false
        }

        val quizScreenLayout =
            resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK

        if ((quizScreenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE || quizScreenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE)
            && quizIsOrientationEnabled
        ) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }

        val quizSettings = quizWebView?.settings
        val quizCM = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val quizAni = quizCM.activeNetworkInfo

        if (quizAni != null && quizAni.isConnected) {
            quizSettings?.cacheMode = WebSettings.LOAD_DEFAULT
        }

        if (quizSettings != null) {
            quizSettings.javaScriptEnabled = true
            quizSettings.domStorageEnabled = true
            quizSettings.databaseEnabled = true
            quizSettings.setRenderPriority(WebSettings.RenderPriority.HIGH)
            quizSettings.allowFileAccess = true
            quizSettings.cacheMode = WebSettings.LOAD_DEFAULT
            quizSettings.setRenderPriority(WebSettings.RenderPriority.HIGH)
            quizSettings.builtInZoomControls = false
            quizSettings.setSupportZoom(false)
        }


        if (savedInstanceState != null) {
            quizWebView?.restoreState(savedInstanceState)
        } else {
            quizWebView?.loadUrl(quizUrl)
        }

        quizWebView?.setOnTouchListener { _, quizEvent ->
            val quizCurrentTime = System.currentTimeMillis()
            if (quizEvent.action == MotionEvent.ACTION_UP && abs(quizCurrentTime - quizLastTouch) > quizTouchThreshold) {
                val quizToggledFullScreen = !quizIsFullScreen()
                quizSaveFullScreen(quizToggledFullScreen)
                quizApplyFullScreen(quizToggledFullScreen)
            } else if (quizEvent.action == MotionEvent.ACTION_DOWN) {
                quizLastTouch = quizCurrentTime
            }
            false
        }
    }

    override fun onResume() {
        super.onResume()
        quizWebView?.loadUrl(quizUrl)
    }

    private fun quizSaveFullScreen(quizIsFullScreen: Boolean) {
        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
        editor.putBoolean(quiz_IS_FULLSCREEN, quizIsFullScreen)
        editor.apply()
    }

    private fun quizIsFullScreen(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(quiz_IS_FULLSCREEN, true)
    }

    private fun quizApplyFullScreen(quizIsFullScreen: Boolean) {
        if (quizIsFullScreen) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    override fun onKeyDown(quizKeyCode: Int, quizEvent: KeyEvent): Boolean {
        if (quizKeyCode == KeyEvent.KEYCODE_BACK && quizEvent.action == KeyEvent.ACTION_DOWN) {
            if (quizWebView!!.canGoBack()) {
                quizWebView!!.goBack()
            } else {
                try {
                    if (System.currentTimeMillis() - quizExitTime > 2000) {
                        Toast.makeText(
                            applicationContext,
                            "Press back again to Exit",
                            Toast.LENGTH_SHORT
                        ).show()
                        quizExitTime = System.currentTimeMillis()
                    } else {
                        finishAffinity()
                    }
                } catch (_: Exception) {
                }
            }
            return true
        }
        return super.onKeyDown(quizKeyCode, quizEvent)
    }

    override fun onPause() {
        super.onPause()
        quizWebView?.destroy()
    }
}
