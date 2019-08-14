package com.dm.yzy.countdowndemo

import android.content.Context

/**
 * Function:
 *
 * @author yangzhiying
 * @date  2019/8/14
 *
 */

fun Float.dpToPx(context: Context): Int {
    val scale = context.resources.displayMetrics.density
    return (this * scale + 0.5f * if (this >= 0) 1 else -1).toInt()
}

fun Float.spToPx(context: Context): Int {
    val fontScale = context.resources.displayMetrics.scaledDensity
    return (this * fontScale + 0.5f).toInt()
}