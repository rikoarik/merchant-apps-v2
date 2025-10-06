package com.solusinegeri.merchant3.data.network

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Manager untuk mengelola token autentikasi
 * Menyediakan method untuk save, get, dan clear token
 */
object TokenManager {
    
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_TOKEN_EXPIRY = "token_expiry"
    
    /**
     * Save authentication tokens
     */
    fun saveTokens(
        context: Context,
        authToken: String,
        refreshToken: String? = null,
        expiryTime: Long? = null
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putString(KEY_AUTH_TOKEN, authToken)
            refreshToken?.let { putString(KEY_REFRESH_TOKEN, it) }
            expiryTime?.let { putLong(KEY_TOKEN_EXPIRY, it) }
            putBoolean(KEY_IS_LOGGED_IN, true)
        }
    }
    
    /**
     * Get authentication token
     */
    fun getAuthToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }
    
    /**
     * Get refresh token
     */
    fun getRefreshToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    /**
     * Check if token is expired
     */
    fun isTokenExpired(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val expiryTime = prefs.getLong(KEY_TOKEN_EXPIRY, 0L)
        
        if (expiryTime == 0L) {
            return false
        }
        
        return System.currentTimeMillis() > expiryTime
    }
    
    /**
     * Clear all authentication data
     */
    fun clearTokens(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
    
    /**
     * Update only auth token (keep other data)
     */
    fun updateAuthToken(context: Context, newToken: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putString(KEY_AUTH_TOKEN, newToken)
        }
    }
}