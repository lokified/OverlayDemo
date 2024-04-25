package com.loki.overlaydemo.window

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import com.loki.overlaydemo.OverlayApp
import com.loki.overlaydemo.R
import com.loki.overlaydemo.notes.Note
import com.loki.overlaydemo.util.registerDraggableListener

class Window(
    private val context: Context
) {

    val db = OverlayApp.appModule.noteRepository

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val layoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val rootView =
        layoutInflater.inflate(R.layout.floating_window_layout, null) as WindowContentLayout

    /**
     * FLAG_LAYOUT_NO_LIMITS - Allow window to extend outside of the screen. This one is optional, but I tend to use it and calculate limits on my own.
     * FLAG_NOT_FOCUSABLE - The window won’t ever get key input focus, so the user can not send key or other button events to it.
     *          Those will instead go to whatever focusable window is behind it. This one is extremely important, because it allows us to control the apps behind the floating window.
     * FLAG_NOT_TOUCH_MODAL - Allow any pointer events outside of the window to be sent to the windows behind it.
     * FLAG_WATCH_OUTSIDE_TOUCH - Receive events for touches that occur outside of your window.
     */
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
        calculateSizeAndPosition(windowParams, 300, 80)
    }

    private fun initWindow() {
        rootView.findViewById<View>(R.id.window_header).registerDraggableListener(
            initialPosition = { Point(windowParams.x, windowParams.y) },
            positionListener = { x, y -> setPosition(x, y) }
        )
        rootView.findViewById<ImageButton>(R.id.window_close).setOnClickListener { close() }
        rootView.findViewById<ImageButton>(R.id.content_button).setOnClickListener {

            val et = rootView.findViewById<EditText>(R.id.content_text)
            db.addNote(Note(0, et.text.toString()))
            et.setText("")
        }

        rootView.setListener {
            if (it) {
                enableKeyBoard()
            } else {
                disableKeyBoard()
            }
        }
    }

    init {
        initWindowParams()
        initWindow()
    }

    fun open() {
        try {
            windowManager.addView(rootView, windowParams)
        } catch (e: Exception) {
            // Ignore exception for now, but in production, you should have some
            // warning for the user here.
        }
    }

    fun close() {
        try {
            windowManager.removeView(rootView)
        } catch (e: Exception) {
            // Ignore exception for now, but in production, you should have some
            // warning for the user here.
        }
    }

    private fun setPosition(x: Int, y: Int) {
        windowParams.x = x
        windowParams.y = y
        update()
    }

    private fun update() {
        try {
            windowManager.updateViewLayout(rootView, windowParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun enableKeyBoard() {
        if (windowParams.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE != 0) {
            windowParams.flags =
                windowParams.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
            update()
        }
    }

    private fun disableKeyBoard() {
        if (windowParams.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE == 0) {
            windowParams.flags =
                windowParams.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            update()
        }
    }
}