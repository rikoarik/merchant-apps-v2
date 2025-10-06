package com.solusinegeri.merchant3.core.domain

import android.util.Log

/**
 * Simple logging utility
 */
object Logger {
    fun d(tag: String, message: String) {
        Log.d(tag, message)
    }
    
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }
}
