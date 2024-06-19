package com.demo.myapplication.extensions

import android.app.Activity
import android.graphics.Color
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.RemoteViews
import androidx.core.content.ContextCompat.getColor
import com.demo.myapplication.R


fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.finishActivityOnClick() {
    setOnClickListener {
        if (context is Activity) {
            (context as Activity).finish()
        }
    }
}

fun RemoteViews.setBackgroundColor(id: Int, color: Int) {
    setInt(id, "setBackgroundColor", color)
}

fun RemoteViews.setTextSize(id: Int, size: Float) {
    setFloat(id, "setTextSize", size)
}

fun RemoteViews.setText(id: Int, text: String) {
    setTextViewText(id, text)
}

fun RemoteViews.setVisibleIf(id: Int, beVisible: Boolean) {
    val visibility = if (beVisible) View.VISIBLE else View.GONE
    setViewVisibility(id, visibility)
}

fun RemoteViews.applyColorFilter(id: Int, color: Int) {
    setInt(id, "setColorFilter", color)
    setInt(id, "setImageAlpha", Color.alpha(color))
}

fun View.performHapticFeedback() = performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)

fun View.isVisible() = visibility == View.VISIBLE

fun View.isInvisible() = visibility == View.INVISIBLE

fun View.isGone() = visibility == View.GONE

fun View.beInvisibleIf(beInvisible: Boolean) = if (beInvisible) beInvisible() else beVisible()

fun View.beVisibleIf(beVisible: Boolean) = if (beVisible) beVisible() else beGone()

fun View.beGoneIf(beGone: Boolean) = beVisibleIf(!beGone)

//fun View.setBackGroundColorProfile() = setBackgroundColor(getColor(context, R.color.profile_color))
//
//fun View.setBackGroundColorMain() = setBackgroundColor(getColor(context, R.color.main_layout_color))

fun View.beInvisible() {
    visibility = View.INVISIBLE
}

fun View.beVisible() {
    visibility = View.VISIBLE
}

fun View.beGone() {
    visibility = View.GONE
}

fun View.setOnClickListener(debounceInterval: Long, listenerBlock: (View) -> Unit) =
    setOnClickListener(DebounceOnClickListener(debounceInterval, listenerBlock))