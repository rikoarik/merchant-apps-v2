package com.solusinegeri.merchant3.core.security

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import androidx.core.content.edit

/**
 * Secure storage untuk menyimpan data sensitif dengan enkripsi
 * Menggunakan Android Keystore dan EncryptedSharedPreferences
 */
object SecureStorage {
    
    private const val PREFS_NAME = "secure_prefs"
    private const val MASTER_KEY_ALIAS = "master_key"
    
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_COMPANY_ID = "company_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_PASSWORD = "user_password"
    private const val KEY_LOGIN_TIMESTAMP = "login_timestamp"
    private const val KEY_SESSION_TIMEOUT = "session_timeout"
    private const val KEY_FAILED_LOGIN_ATTEMPTS = "failed_login_attempts"
    private const val KEY_LAST_FAILED_LOGIN = "last_failed_login"
    
    private const val SESSION_TIMEOUT_MS = 30 * 60 * 1000L
    
    private const val MAX_LOGIN_ATTEMPTS = 5
    private const val RATE_LIMIT_WINDOW_MS = 15 * 60 * 1000L
    
    /**
     * Get encrypted SharedPreferences
     */
    private fun getEncryptedPrefs(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context, MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        
        return EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    /**
     * Simpan auth token dengan enkripsi
     */
    fun saveAuthToken(context: Context, token: String) {
        val prefs = getEncryptedPrefs(context)
        prefs.edit {
            putString(KEY_AUTH_TOKEN, token)
                .putLong(KEY_LOGIN_TIMESTAMP, System.currentTimeMillis())
        }
    }
    
    /**
     * Ambil auth token
     */
    fun getAuthToken(context: Context): String? {
        val prefs = getEncryptedPrefs(context)
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }
    
    /**
     * Simpan refresh token dengan enkripsi
     */
    fun saveRefreshToken(context: Context, token: String) {
        val prefs = getEncryptedPrefs(context)
        prefs.edit()
            .putString(KEY_REFRESH_TOKEN, token)
            .apply()
    }
    
    /**
     * Ambil refresh token
     */
    fun getRefreshToken(context: Context): String? {
        val prefs = getEncryptedPrefs(context)
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }
    
    /**
     * Simpan user data dengan enkripsi
     */
    fun saveUserData(context: Context, userId: String, companyId: String, userName: String, userEmail: String) {
        val prefs = getEncryptedPrefs(context)
        prefs.edit {
            putString(KEY_USER_ID, userId)
                .putString(KEY_COMPANY_ID, companyId)
                .putString(KEY_USER_NAME, userName)
                .putString(KEY_USER_EMAIL, userEmail)
        }
    }
    
    /**
     * Simpan login credentials untuk refresh token
     */
    fun saveLoginCredentials(context: Context, companyId: String, username: String, password: String) {
        val prefs = getEncryptedPrefs(context)
        prefs.edit {
            putString(KEY_COMPANY_ID, companyId)
                .putString(KEY_USER_NAME, username)
                .putString(KEY_USER_PASSWORD, password)
        }
    }
    
    /**
     * Ambil password untuk refresh token
     */
    fun getPassword(context: Context): String? {
        val prefs = getEncryptedPrefs(context)
        return prefs.getString(KEY_USER_PASSWORD, null)
    }

    /**
     * Simpan Password setelah ubah
     */
    fun savePassword(context: Context, password: String) {
        val prefs = getEncryptedPrefs(context)
        prefs.edit {
            putString(KEY_USER_PASSWORD, password)
        }
    }
    
    /**
     * Clear login credentials
     */
    fun clearLoginCredentials(context: Context) {
        val prefs = getEncryptedPrefs(context)
        prefs.edit {
            remove(KEY_USER_PASSWORD)
        }
    }
    
    /**
     * Ambil user data
     */
    fun getUserData(context: Context): Map<String, String?> {
        val prefs = getEncryptedPrefs(context)
        return mapOf(
            "user_id" to prefs.getString(KEY_USER_ID, null),
            "company_id" to prefs.getString(KEY_COMPANY_ID, null),
            "user_name" to prefs.getString(KEY_USER_NAME, null),
            "user_email" to prefs.getString(KEY_USER_EMAIL, null)
        )
    }
    
    /**
     * Cek apakah session masih valid
     */
    fun isSessionValid(context: Context): Boolean {
        val prefs = getEncryptedPrefs(context)
        val loginTimestamp = prefs.getLong(KEY_LOGIN_TIMESTAMP, 0)
        
        if (loginTimestamp == 0L) return false
        
        val currentTime = System.currentTimeMillis()
        return (currentTime - loginTimestamp) < SESSION_TIMEOUT_MS
    }
    
    /**
     * Update session timestamp
     */
    fun updateSessionTimestamp(context: Context) {
        val prefs = getEncryptedPrefs(context)
        prefs.edit {
            putLong(KEY_LOGIN_TIMESTAMP, System.currentTimeMillis())
        }
    }
    
    /**
     * Cek rate limiting untuk login attempts
     */
    fun canAttemptLogin(context: Context): Boolean {
        val prefs = getEncryptedPrefs(context)
        val failedAttempts = prefs.getInt(KEY_FAILED_LOGIN_ATTEMPTS, 0)
        val lastFailedLogin = prefs.getLong(KEY_LAST_FAILED_LOGIN, 0)
        
        val currentTime = System.currentTimeMillis()
        
        if (currentTime - lastFailedLogin > RATE_LIMIT_WINDOW_MS) {
            prefs.edit {
                putInt(KEY_FAILED_LOGIN_ATTEMPTS, 0)
            }
            return true
        }
        
        return failedAttempts < MAX_LOGIN_ATTEMPTS
    }
    
    /**
     * Record failed login attempt
     */
    fun recordFailedLoginAttempt(context: Context) {
        val prefs = getEncryptedPrefs(context)
        val currentAttempts = prefs.getInt(KEY_FAILED_LOGIN_ATTEMPTS, 0)
        
        prefs.edit {
            putInt(KEY_FAILED_LOGIN_ATTEMPTS, currentAttempts + 1)
                .putLong(KEY_LAST_FAILED_LOGIN, System.currentTimeMillis())
        }
    }
    
    /**
     * Reset failed login attempts (setelah login berhasil)
     */
    fun resetFailedLoginAttempts(context: Context) {
        val prefs = getEncryptedPrefs(context)
        prefs.edit {
            putInt(KEY_FAILED_LOGIN_ATTEMPTS, 0)
                .putLong(KEY_LAST_FAILED_LOGIN, 0)
        }
    }
    
    /**
     * Clear semua data sensitif
     */
    fun clearAllData(context: Context) {
        val prefs = getEncryptedPrefs(context)
        prefs.edit { clear() }
    }
    
    /**
     * Generate secure random string untuk session ID
     */
    fun generateSecureSessionId(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Cek apakah device memiliki keamanan yang memadai
     */
    fun isDeviceSecure(context: Context): Boolean {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as android.app.KeyguardManager
        return keyguardManager.isKeyguardSecure
    }
    
    /**
     * Get remaining session time dalam menit
     */
    fun getRemainingSessionTime(context: Context): Long {
        val prefs = getEncryptedPrefs(context)
        val loginTimestamp = prefs.getLong(KEY_LOGIN_TIMESTAMP, 0)
        
        if (loginTimestamp == 0L) return 0
        
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - loginTimestamp
        val remainingTime = SESSION_TIMEOUT_MS - elapsedTime
        
        return if (remainingTime > 0) remainingTime / (60 * 1000) else 0
    }
}
