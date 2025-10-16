package com.solusinegeri.merchant3.data.repository

import android.content.Context
import androidx.core.content.edit
import com.solusinegeri.merchant3.core.network.safeApiCall
import com.solusinegeri.merchant3.core.security.SecureStorage
import com.solusinegeri.merchant3.core.security.SecurityLogger
import com.solusinegeri.merchant3.core.utils.ErrorParser
import com.solusinegeri.merchant3.data.network.AuthService
import com.solusinegeri.merchant3.data.network.NetworkClient
import com.solusinegeri.merchant3.data.network.TokenManager
import com.solusinegeri.merchant3.data.requests.LoginRequest
import com.solusinegeri.merchant3.data.responses.LoginData
import com.solusinegeri.merchant3.data.responses.LoginResponse
import com.solusinegeri.merchant3.core.security.TokenManager as SecurityTokenManager

/**
 * Simple authentication repository dengan TokenManager.
 * Menggunakan ApplicationContext untuk menghindari memory leaks.
 */
class AuthRepository(
    val appContext: Context,
    private val authService: AuthService = NetworkClient.authService
) {

    suspend fun login(companyId: String, username: String, password: String): Result<LoginResponse> {
        val request = LoginRequest(companyId, username, password)

        val result = safeApiCall(
            apiCall = { authService.login(request) },
            onEmptyBody = { IllegalStateException("Response login kosong") },
            errorParser = { ErrorParser.parseLoginError(it) }
        )

        return result.mapCatching { loginResponse ->
            val loginData = loginResponse.data ?: throw IllegalStateException("Data login tidak ditemukan")
            val token = loginData.authToken ?: throw IllegalStateException("Token tidak ditemukan")

            SecureStorage.saveAuthToken(appContext, token)
            SecurityTokenManager.saveTokens(
                accessToken = token,
                refreshToken = token,
                expiresIn = 3600
            )

            saveUserDataSecurely(loginData)
            saveLoginCredentials(companyId, username, password)

            loginResponse
        }
    }

    fun isLoggedIn(): Boolean =
        SecurityTokenManager.hasValidTokens() && !SecurityTokenManager.isTokenExpired()

    fun getToken(): String? = SecurityTokenManager.getAccessToken()

    /**
     * Refresh token jika diperlukan - menggunakan login endpoint yang sama.
     */
    suspend fun refreshTokenIfNeeded(): Result<Boolean> {
        return if (SecurityTokenManager.isTokenExpiredOrNeedsRefresh()) {
            val (companyId, username, password) = getLoginCredentials()

            if (!companyId.isNullOrBlank() && !username.isNullOrBlank() && !password.isNullOrBlank()) {
                login(companyId, username, password).fold(
                    onSuccess = { Result.success(true) },
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
    }

    fun needsTokenRefresh(): Boolean = SecurityTokenManager.isTokenExpiredOrNeedsRefresh()

    fun isUserLoggedIn(): Boolean =
        SecurityTokenManager.hasValidTokens() && !SecurityTokenManager.isTokenExpired()

    fun logout() {
        SecurityLogger.logSessionEvent(appContext, SecurityLogger.EVENT_LOGOUT)

        SecureStorage.clearAllData(appContext)
        SecurityTokenManager.clearTokens()

        TokenManager.clearTokens(appContext)
        val prefs = appContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit { clear() }

        SecureStorage.clearLoginCredentials(appContext)
    }

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

    fun saveLoginCredentials(companyId: String, username: String, password: String) {
        SecureStorage.saveLoginCredentials(appContext, companyId, username, password)

        val prefs = appContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("company_id", companyId)
            putString("user_name", username)
            apply()
        }
    }

    fun getLoginCredentials(): Triple<String?, String?, String?> {
        val prefs = appContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val companyId = prefs.getString("company_id", null)
        val username = prefs.getString("user_name", null)
        val password = SecureStorage.getPassword(appContext)

        return Triple(companyId, username, password)
    }

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
