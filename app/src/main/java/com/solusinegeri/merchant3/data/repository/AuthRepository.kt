package com.solusinegeri.merchant3.data.repository

import android.content.Context
import com.solusinegeri.merchant3.core.security.SecureStorage
import com.solusinegeri.merchant3.core.security.SecurityLogger
import com.solusinegeri.merchant3.core.utils.ErrorParser
import com.solusinegeri.merchant3.data.network.NetworkClient
import com.solusinegeri.merchant3.data.network.TokenManager
import com.solusinegeri.merchant3.data.requests.LoginRequest
import com.solusinegeri.merchant3.data.responses.LoginData
import com.solusinegeri.merchant3.data.responses.LoginResponse
import com.solusinegeri.merchant3.core.security.TokenManager as SecurityTokenManager
import androidx.core.content.edit
import retrofit2.Response

/**
 * Simple authentication repository dengan TokenManager
 * Menggunakan ApplicationContext untuk menghindari memory leaks
 */
class AuthRepository(val appContext: Context) {

    
    suspend fun login(companyId: String, username: String, password: String): Result<LoginResponse> {
        return try {
            val request = LoginRequest(companyId, username, password)
            val response = NetworkClient.authService.login(request)

            if (response.isSuccessful) {
                val loginResponse = response.body()
                if (loginResponse != null) {
                    loginResponse.data?.authToken?.let { token ->
                        SecureStorage.saveAuthToken(appContext, token)
                        SecurityTokenManager.saveTokens(
                            accessToken = token,
                            refreshToken = loginResponse.data.authToken,
                            expiresIn = 3600
                        )
                    }
                    
                    loginResponse.data?.let { loginData ->
                        saveUserDataSecurely(loginData)
                    }
                    
                    saveLoginCredentials(companyId, username, password)
                    
                    Result.success(loginResponse)
                } else {
                    Result.failure(Exception("Empty response"))
                }
            } else {
                val errorMessage = ErrorParser.parseLoginError(
                    response.errorBody()?.string() ?: "",
                    response.code()
                )
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun isLoggedIn(): Boolean {
        return SecurityTokenManager.hasValidTokens() && !SecurityTokenManager.isTokenExpired()
    }
    
    fun getToken(): String? {
        return SecurityTokenManager.getAccessToken()
    }
    
    /**
     * Refresh token jika diperlukan - menggunakan login endpoint yang sama
     */
    suspend fun refreshTokenIfNeeded(): Result<Boolean> {
        return try {
            if (SecurityTokenManager.isTokenExpiredOrNeedsRefresh()) {
                val (companyId, username, password) = getLoginCredentials()
                
                if (!companyId.isNullOrBlank() && !username.isNullOrBlank() && !password.isNullOrBlank()) {
                    val loginResult = login(companyId, username, password)
                    
                    loginResult.fold(
                        onSuccess = { loginResponse ->
                            Result.success(true)
                        },
                        onFailure = { error ->
                            Result.failure(Exception("Refresh token failed: ${error.message}"))
                        }
                    )
                } else {
                    Result.failure(Exception("Login credentials not found for refresh"))
                }
            } else {
                Result.success(false)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Check apakah perlu refresh token
     */
    fun needsTokenRefresh(): Boolean {
        return SecurityTokenManager.isTokenExpiredOrNeedsRefresh()
    }
    
    /**
     * Check apakah user sudah login dan token masih valid
     */
    fun isUserLoggedIn(): Boolean {
        return SecurityTokenManager.hasValidTokens() && !SecurityTokenManager.isTokenExpired()
    }
    
    fun logout() {
        SecurityLogger.logSessionEvent(appContext, SecurityLogger.EVENT_LOGOUT)
        
        SecureStorage.clearAllData(appContext)
        SecurityTokenManager.clearTokens()
        
        TokenManager.clearTokens(appContext)
        val prefs = appContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit { clear() }
        
        SecureStorage.clearLoginCredentials(appContext)
    }
    
    /**
     * Save additional user data after login dengan secure storage
     */
    private fun saveUserDataSecurely(loginData: LoginData) {
        SecureStorage.saveUserData(
            appContext,
            loginData.userId ?: "",
            loginData.companyId ?: "",
            loginData.name ?: "",
            loginData.email ?: ""
        )
        
        saveUserData(loginData)
    }
    
    /**
     * Save additional user data after login (traditional method)
     */
    fun saveUserData(loginData: LoginData) {
        val prefs = appContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            loginData.userId?.let { putString("user_id", it) }
            loginData.companyId?.let { putString("company_id", it) }
            loginData.name?.let { putString("user_name", it) }
            loginData.email?.let { putString("user_email", it) }
            apply()
        }
    }
    
    /**
     * Save login credentials untuk refresh token
     */
    fun saveLoginCredentials(companyId: String, username: String, password: String) {
        SecureStorage.saveLoginCredentials(appContext, companyId, username, password)
        
        val prefs = appContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("company_id", companyId)
            putString("user_name", username)
            apply()
        }
    }
    
    /**
     * Get login credentials untuk refresh token
     */
    fun getLoginCredentials(): Triple<String?, String?, String?> {
        val prefs = appContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val companyId = prefs.getString("company_id", null)
        val username = prefs.getString("user_name", null)
        
        val password = SecureStorage.getPassword(appContext)
        
        return Triple(companyId, username, password)
    }
    
    /**
     * Get user data
     */
    fun getUserData(): Map<String, String?> {
        val prefs = appContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return mapOf(
            "user_id" to prefs.getString("user_id", null),
            "company_id" to prefs.getString("company_id", null),
            "user_name" to prefs.getString("user_name", null),
            "user_email" to prefs.getString("user_email", null)
        )
    }
    
}
