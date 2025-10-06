package com.solusinegeri.merchant3.core.utils

import android.content.Context
import android.content.SharedPreferences
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import android.util.Base64
import androidx.core.content.edit

/**
 * Utility class untuk mengelola SharedPreferences dengan enkripsi
 */
object PreferenceManager {
    
    private const val PREF_NAME = "merchant_preferences"
    private const val KEY_BALANCE_CODE = "balance_code"
    private const val KEY_USER_SESSION = "user_session"
    private const val KEY_ENCRYPTION_KEY = "encryption_key"
    
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var encryptionKey: SecretKey
    
    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        initializeEncryptionKey()
    }
    
    private fun initializeEncryptionKey() {
        val savedKey = sharedPreferences.getString(KEY_ENCRYPTION_KEY, null)
        if (savedKey != null) {
            val keyBytes = Base64.decode(savedKey, Base64.DEFAULT)
            encryptionKey = SecretKeySpec(keyBytes, "AES")
        } else {
            val keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(256)
            encryptionKey = keyGenerator.generateKey()
            val keyString = Base64.encodeToString(encryptionKey.encoded, Base64.DEFAULT)
            sharedPreferences.edit { putString(KEY_ENCRYPTION_KEY, keyString) }
        }
    }
    
    /**
     * Simpan balance code dengan enkripsi
     */
    fun saveBalanceCode(balanceCode: String) {
        val encryptedCode = encrypt(balanceCode)
        sharedPreferences.edit { putString(KEY_BALANCE_CODE, encryptedCode) }
    }
    
    /**
     * Ambil balance code dengan dekripsi
     */
    fun getBalanceCode(): String? {
        val encryptedCode = sharedPreferences.getString(KEY_BALANCE_CODE, null) ?: return null
        return try {
            decrypt(encryptedCode)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Simpan user session data
     */
    fun saveUserSession(sessionData: String) {
        val encryptedSession = encrypt(sessionData)
        sharedPreferences.edit { putString(KEY_USER_SESSION, encryptedSession) }
    }
    
    /**
     * Ambil user session data
     */
    fun getUserSession(): String? {
        val encryptedSession = sharedPreferences.getString(KEY_USER_SESSION, null) ?: return null
        return try {
            decrypt(encryptedSession)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Clear semua data
     */
    fun clearAll() {
        sharedPreferences.edit { clear() }
    }
    
    /**
     * Clear balance code saja
     */
    fun clearBalanceCode() {
        sharedPreferences.edit { remove(KEY_BALANCE_CODE) }
    }
    
    /**
     * Cek apakah balance code sudah tersimpan
     */
    fun hasBalanceCode(): Boolean {
        return sharedPreferences.contains(KEY_BALANCE_CODE)
    }
    
    private fun encrypt(text: String): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey)
        val encryptedBytes = cipher.doFinal(text.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }
    
    private fun decrypt(encryptedText: String): String {
        val encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT)
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, encryptionKey)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }


    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit { putBoolean(key, value) }
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }
}
