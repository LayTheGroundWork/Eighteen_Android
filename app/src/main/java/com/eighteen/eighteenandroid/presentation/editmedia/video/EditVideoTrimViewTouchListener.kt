package com.eighteen.eighteenandroid.presentation.editmedia.video

import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import com.eighteen.eighteenandroid.common.safeLet

open class EditVideoTrimViewTouchListener : OnTouchListener {
    private var actionDownMarginStartPx: Int? = null
    private var actionDownMarginEndPx: Int? = null
    private var actionDownTouchRawXPos: Float? = null
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                actionDownMarginStartPx = view.marginStart
                actionDownMarginEndPx = view.marginEnd
                actionDownTouchRawXPos = motionEvent.rawX
            }
            MotionEvent.ACTION_MOVE -> {
                safeLet(
                    actionDownMarginStartPx,
                    actionDownMarginEndPx,
                    actionDownTouchRawXPos
                ) { marginStart, marginEnd, touchRawXPos ->
                    onActionMove(
                        actionDownMarginStartPx = marginStart,
                        actionDownMarginEndPx = marginEnd,
                        diff = (motionEvent.rawX - touchRawXPos).toInt()
                    )
                }
            }
            MotionEvent.ACTION_UP -> {
                view.performClick()
            }
        }
        return true
    }

    open fun onActionMove(
        actionDownMarginStartPx: Int,
        actionDownMarginEndPx: Int,
        diff: Int
    ) {

    }
}