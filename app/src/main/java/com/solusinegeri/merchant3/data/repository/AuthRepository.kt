package com.solusinegeri.merchant3.data.repository

import android.content.Context
import androidx.core.content.edit
import com.solusinegeri.merchant3.core.auth.TokenProvider
import com.solusinegeri.merchant3.core.base.BaseRepository
import com.solusinegeri.merchant3.core.network.ApiError
import com.solusinegeri.merchant3.core.network.ApiException
import com.solusinegeri.merchant3.core.security.SecureStorage
import com.solusinegeri.merchant3.core.security.SecurityLogger
import com.solusinegeri.merchant3.core.utils.ApiErrorHandler
import com.solusinegeri.merchant3.data.network.NetworkClient
import com.solusinegeri.merchant3.data.network.TokenManager
import com.solusinegeri.merchant3.data.requests.LoginRequest
import com.solusinegeri.merchant3.data.responses.LoginData
import com.solusinegeri.merchant3.data.responses.LoginResponse
import com.solusinegeri.merchant3.core.security.TokenManager as SecurityTokenManager

/**
 * Enhanced authentication repository dengan auto-refresh token mechanism
 * 
 * SECURITY FEATURES:
 * - Uses TokenProvider for centralized token management
 * - Automatic token refresh on expiration
 * - Encrypted credential storage
 * - Rate limiting for refresh attempts
 * - Comprehensive security logging
 * 
 * Menggunakan ApplicationContext untuk menghindari memory leaks
 */
class AuthRepository(val appContext: Context) : BaseRepository() {
    
    private val tokenProvider = TokenProvider(appContext)

    
    suspend fun login(companyId: String, username: String, password: String): Result<LoginResponse> {
        SecurityLogger.logSecurityEvent(appContext, "AuthRepository", "Login attempt for user: $username")

        val request = LoginRequest(companyId, username, password)

        return request(appContext) { NetworkClient.authService.login(request) }
            .mapCatching { loginResponse ->
                val loginData = loginResponse.data
                    ?: throw ApiException(
                        ApiError(
                            message = "Data login tidak ditemukan pada response.",
                            type = loginResponse.type
                        )
                    )

                val token = loginData.authToken
                    ?: throw ApiException(
                        ApiError(
                            message = "Token autentikasi tidak tersedia pada response login.",
                            type = loginResponse.type
                        )
                    )

                persistLoginResult(token, loginData, companyId, username, password)

                loginResponse
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
        if (tokenProvider.isTokenValid() && !tokenProvider.needsRefresh()) {
            return Result.success(false)
        }

        val (companyId, username, password) = tokenProvider.getStoredCredentials()
        if (companyId.isNullOrBlank() || username.isNullOrBlank() || password.isNullOrBlank()) {
            return Result.failure(
                ApiException(
                    ApiError(
                        message = "Login credentials not found for refresh",
                        requiresLogout = true
                    )
                )
            )
        }

        val loginResult = login(companyId, username, password)

        return loginResult.fold(
            onSuccess = {
                Result.success(true)
            },
            onFailure = { throwable ->
                val apiError = when (throwable) {
                    is ApiException -> throwable.error
                    else -> ApiErrorHandler.resolve(throwable, appContext)
                }
                Result.failure(
                    ApiException(
                        apiError.copy(
                            message = "Refresh token gagal: ${apiError.message}",
                            requiresLogout = apiError.requiresLogout || apiError.statusCode in listOf(401, 403)
                        )
                    )
                )
            }
        )
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
    
    private fun persistLoginResult(
        token: String,
        loginData: LoginData,
        companyId: String,
        username: String,
        password: String
    ) {
        tokenProvider.saveToken(token)
        SecureStorage.saveAuthToken(appContext, token)
        SecurityTokenManager.saveTokens(
            accessToken = token,
            refreshToken = token,
            expiresIn = 3600
        )

        SecureStorage.saveUserData(
            appContext,
            loginData.userId ?: "",
            loginData.companyId ?: "",
            loginData.name ?: "",
            loginData.email ?: ""
        )

        saveUserData(loginData)
        saveLoginCredentials(companyId, username, password)
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
