package com.nschirmer.widgets

/**
 * Possible view states list
 * **/
internal enum class ViewState (val state: IntArray) {
    PRESSED (intArrayOf(android.R.attr.state_pressed)),
    FOCUSED (intArrayOf(android.R.attr.state_focused)),
    DISABLED (intArrayOf(-android.R.attr.state_enabled)),
    CHECKED (intArrayOf(-android.R.attr.state_checked)),
    DEFAULT (intArrayOf())
}