package pt.rfernandes.loopuiux.ui.utils

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlin.math.abs


/**
 *   Class CustomOnSwipeTouchListener created at 5/24/21 22:00 for the project LOOP UI&UX
 *   By: rodrigofernandes
 */
open class CustomOnSwipeTouchListener(context: Context?) : OnTouchListener {
    private val gestureDetector: GestureDetector
    private val CLICK_ACTION_THRESHOLD = 200
    private var startX = 0f
    private var startY = 0f

    companion object {

        private val SWIPE_THRESHOLD = 50
        private val SWIPE_VELOCITY_THRESHOLD = 50
    }

    init {
        gestureDetector = GestureDetector(context, GestureListener())
    }


    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
            }
            MotionEvent.ACTION_UP -> {
                val endX = event.x
                val endY = event.y
                if (isAClick(startX, endX, startY, endY)) {
                    v.performClick() // WE HAVE A CLICK!!
                }
            }
        }
        return gestureDetector.onTouchEvent(event)
    }

    private fun isAClick(startX: Float, endX: Float, startY: Float, endY: Float): Boolean {
        val differenceX = abs(startX - endX)
        val differenceY = abs(startY - endY)
        return !(differenceX > CLICK_ACTION_THRESHOLD /* =5 */ || differenceY > CLICK_ACTION_THRESHOLD)
    }

    private inner class GestureListener : SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                        result = true
                    }
                } else if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom()
                    } else {
                        onSwipeTop()
                    }
                    result = true
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            return result
        }

    }

    open fun onSwipeRight() {}

    open fun onSwipeLeft() {}

    open fun onSwipeTop() {}

    open fun onSwipeBottom() {}

}