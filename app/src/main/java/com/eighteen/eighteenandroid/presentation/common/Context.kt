package com.eighteen.eighteenandroid.presentation.common

import android.content.Context
import android.util.TypedValue

fun Context.dp2Px(dp: Float): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
        .toInt()

fun Context.dp2Px(dp: Int): Int = dp2Px(dp.toFloat())

fun Context.px2DpF(px: Int): Float = px / resources.displayMetrics.density

fun Context.px2Dp(px: Int): Int = px2DpF(px).toInt()