package com.app.statuscontrol.utils

import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.app.statuscontrol.R

infix fun View.click(click: () -> Unit) {
    setOnClickListener { click() }
}

infix fun EditText.onKeyEventListener(onKeyListener: (view: View, keyCode: Int, keyEvent: KeyEvent) -> Boolean) {
    setOnKeyListener { view, keyCode, keyEvent -> onKeyListener(view, keyCode, keyEvent) }
}

fun View.setVisible(): View {
    this.visibility = View.VISIBLE
    return this
}

fun View.setInvisible(): View {
    this.visibility = View.INVISIBLE
    return this
}

fun View.setGone(): View {
    this.visibility = View.GONE
    return this
}

fun ImageView.showOnline(): ImageView {
    this.setImageResource(R.drawable.ic_green_dot)
    return this
}

fun ImageView.showOffline(): ImageView {
    this.setImageResource(R.drawable.ic_red_dot)
    return this
}