package com.example.mnews.util

import android.content.Context

object ReadTracker {
    private const val PREFS = "read_prefs"
    private const val KEY = "read_urls"

    fun markRead(context: Context, url: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val set = prefs.getStringSet(KEY, emptySet())?.toMutableSet() ?: mutableSetOf()
        set.add(url)
        prefs.edit().putStringSet(KEY, set).apply()
    }

    fun isRead(context: Context, url: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val set = prefs.getStringSet(KEY, emptySet()) ?: emptySet()
        return set.contains(url)
    }
}
