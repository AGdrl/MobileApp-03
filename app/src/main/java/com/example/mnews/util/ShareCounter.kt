package com.example.mnews.util

import android.content.Context

object ShareCounter {

    private const val PREFS = "share_prefs"
    private const val KEY_TOTAL = "total_shares"

    fun increment(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val current = prefs.getInt(KEY_TOTAL, 0)
        prefs.edit().putInt(KEY_TOTAL, current + 1).apply()
    }

    fun getTotal(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_TOTAL, 0)
    }
}
