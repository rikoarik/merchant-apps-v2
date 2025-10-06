package com.solusinegeri.merchant3.core.security

import android.content.Context
import android.content.SharedPreferences
import com.solusinegeri.merchant3.core.utils.PreferenceManager

/**
 * Manager untuk handle token expiration dan refresh
 */
object TokenManager {
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_TOKEN_EXPIRES_AT = "token_expires_at"
    private const val KEY_LAST_LOGIN_TIME = "last_login_time"
    
    private const val TOKEN_EXPIRY_THRESHOLD = 10 * 60 * 1000L // 10 menit dalam milliseconds
    
    private lateinit var sharedPreferences: SharedPreferences
    
    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
    }
    
    /**
     * Simpan token dan waktu login
     */
    fun saveTokens(accessToken: String, refreshToken: String, expiresIn: Long) {
        val currentTime = System.currentTimeMillis()
        val expiresAt = currentTime + (expiresIn * 1000) // Convert seconds to milliseconds
        
        sharedPreferences.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .putLong(KEY_TOKEN_EXPIRES_AT, expiresAt)
            .putLong(KEY_LAST_LOGIN_TIME, currentTime)
            .apply()
    }
    
    /**
     * Ambil access token
     */
    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }
    
    /**
     * Ambil refresh token
     */
    fun getRefreshToken(): String? {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }
    
    /**
     * Check apakah token sudah expired atau perlu refresh
     */
    fun isTokenExpiredOrNeedsRefresh(): Boolean {
        val lastLoginTime = sharedPreferences.getLong(KEY_LAST_LOGIN_TIME, 0)
        val currentTime = System.currentTimeMillis()
        
        return (currentTime - lastLoginTime) > TOKEN_EXPIRY_THRESHOLD
    }
    
    /**
     * Check apakah token benar-benar expired berdasarkan expires_at
     */
    fun isTokenExpired(): Boolean {
        val expiresAt = sharedPreferences.getLong(KEY_TOKEN_EXPIRES_AT, 0)
        val currentTime = System.currentTimeMillis()
        
        return currentTime >= expiresAt
    }
    
    /**
     * Check apakah user sudah login sebelumnya
     */
    fun hasValidTokens(): Boolean {
        val accessToken = getAccessToken()
        val refreshToken = getRefreshToken()
        
        return !accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()
    }
    
    /**
     * Update waktu login terakhir
     */
    fun updateLastLoginTime() {
        val currentTime = System.currentTimeMillis()
        sharedPreferences.edit()
            .putLong(KEY_LAST_LOGIN_TIME, currentTime)
            .apply()
    }
    
    /**
     * Clear semua token
     */
    fun clearTokens() {
        sharedPreferences.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_TOKEN_EXPIRES_AT)
            .remove(KEY_LAST_LOGIN_TIME)
            .apply()
    }
    
    /**
     * Get waktu tersisa token dalam menit
     */
    fun getTokenTimeRemainingMinutes(): Long {
        val expiresAt = sharedPreferences.getLong(KEY_TOKEN_EXPIRES_AT, 0)
        val currentTime = System.currentTimeMillis()
        val remaining = expiresAt - currentTime
        
        return if (remaining > 0) remaining / (60 * 1000) else 0
    }
    
    /**
     * Get waktu sejak login terakhir dalam menit
     */
    fun getTimeSinceLastLoginMinutes(): Long {
        val lastLoginTime = sharedPreferences.getLong(KEY_LAST_LOGIN_TIME, 0)
        val currentTime = System.currentTimeMillis()
        val timeSince = currentTime - lastLoginTime
        
        return timeSince / (60 * 1000)
    }
}
