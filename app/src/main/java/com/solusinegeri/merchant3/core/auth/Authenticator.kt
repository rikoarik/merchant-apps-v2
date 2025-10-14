package com.solusinegeri.merchant3.core.auth

import android.content.Context
import com.solusinegeri.merchant3.core.network.ApiError
import com.solusinegeri.merchant3.core.network.ApiException
import com.solusinegeri.merchant3.core.network.safeApiCall
import com.solusinegeri.merchant3.core.security.SecurityLogger
import com.solusinegeri.merchant3.core.utils.ApiErrorHandler
import com.solusinegeri.merchant3.data.network.NetworkClient
import com.solusinegeri.merchant3.data.requests.LoginRequest
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * Authenticator implementation for automatic token refresh on 401 responses
 * 
 * SECURITY BEST PRACTICES:
 * - Handles 401 Unauthorized responses automatically
 * - Uses stored credentials to refresh token via login endpoint
 * - Thread-safe with mutex to prevent concurrent refresh attempts
 * - Rate limiting to prevent abuse
 * - Comprehensive security logging
 * - Fails securely if refresh cannot be completed
 */
class TokenAuthenticator(private val context: Context) : Authenticator {
    
    private val tokenProvider = TokenProvider(context)
    private val refreshMutex = Mutex()
    
    companion object {
        private const val MAX_RETRY_ATTEMPTS = 1
    }
    
    /**
     * Handle authentication challenge (401 response)
     * 
     * SECURITY CONSIDERATIONS:
     * - Only attempts refresh if credentials are available
     * - Uses mutex to prevent concurrent refresh attempts
     * - Logs all authentication events for security auditing
     * - Fails securely if refresh cannot be completed
     */
    override fun authenticate(route: Route?, response: Response): Request? {
        val responseCode = response.code
        SecurityLogger.logSecurityEvent(
            context, 
            "TokenAuthenticator", 
            "${responseCode} response received, attempting token refresh"
        )
        
        // Handle both 401 (Unauthorized) and 403 (Forbidden) responses
        if (responseCode != 401 && responseCode != 403) {
            SecurityLogger.logSecurityEvent(
                context, 
                "TokenAuthenticator", 
                "Response code $responseCode not handled, aborting refresh"
            )
            return null
        }
        
        // Check if this is already a retry attempt
        val responseCount = responseCount(response)
        if (responseCount >= MAX_RETRY_ATTEMPTS) {
            SecurityLogger.logSecurityEvent(
                context, 
                "TokenAuthenticator", 
                "Max retry attempts reached, aborting refresh"
            )
            return null
        }
        
        // Check if we can attempt refresh
        if (!tokenProvider.canAttemptRefresh()) {
            SecurityLogger.logSecurityEvent(
                context, 
                "TokenAuthenticator", 
                "Refresh rate limit exceeded, cannot attempt refresh"
            )
            return null
        }
        
        // Check if credentials are available
        if (!tokenProvider.hasCredentialsForRefresh()) {
            SecurityLogger.logSecurityEvent(
                context, 
                "TokenAuthenticator", 
                "No stored credentials available for refresh"
            )
            return null
        }
        
        return try {
            // Use suspend function in a runBlocking context for the authenticator
            kotlinx.coroutines.runBlocking {
                refreshMutex.withLock {
                    performTokenRefresh(response.request)
                }
            }
        } catch (e: Exception) {
            SecurityLogger.logSecurityEvent(
                context, 
                "TokenAuthenticator", 
                "Token refresh failed: ${e.message}"
            )
            null
        }
    }
    
    /**
     * Perform the actual token refresh
     * 
     * SECURITY: Uses stored credentials to call login endpoint
     */
    private suspend fun performTokenRefresh(originalRequest: Request): Request? {
        tokenProvider.recordRefreshAttempt()
        
        val (companyId, username, password) = tokenProvider.getStoredCredentials()
        
        if (companyId.isNullOrBlank() || username.isNullOrBlank() || password.isNullOrBlank()) {
            SecurityLogger.logSecurityEvent(
                context, 
                "TokenAuthenticator", 
                "Incomplete credentials for refresh"
            )
            return null
        }
        
        SecurityLogger.logSecurityEvent(
            context, 
            "TokenAuthenticator", 
            "Attempting token refresh for user: $username"
        )
        
        val loginRequest = LoginRequest(companyId, username, password)
        val refreshResult = safeApiCall(context) { NetworkClient.authService.refreshToken(loginRequest) }
        
        return refreshResult.fold(
            onSuccess = { loginResponse ->
                val newToken = loginResponse.data?.authToken
                    ?: return@fold handleRefreshFailure(
                        ApiException(
                            ApiError(
                                message = "Token autentikasi tidak tersedia pada response refresh.",
                                requiresLogout = true
                            )
                        )
                    )
                
                tokenProvider.saveToken(newToken)
                
                SecurityLogger.logSecurityEvent(
                    context, 
                    "TokenAuthenticator", 
                    "Token refresh successful"
                )
                
                createAuthenticatedRequest(originalRequest, newToken)
            },
            onFailure = { error ->
                handleRefreshFailure(error)
            }
        )
    }
    
    /**
     * Create new request with updated authorization header
     * 
     * SECURITY: Only adds Bearer token, no other modifications
     */
    private fun createAuthenticatedRequest(originalRequest: Request, token: String): Request {
        return originalRequest.newBuilder()
            .removeHeader("Authorization")
            .addHeader("Authorization", "Bearer $token")
            .build()
    }

    private fun handleRefreshFailure(error: Throwable): Request? {
        val apiError = when (error) {
            is ApiException -> error.error
            else -> ApiErrorHandler.resolve(error, context)
        }

        SecurityLogger.logSecurityEvent(
            context,
            "TokenAuthenticator",
            "Token refresh failed: ${apiError.message}"
        )

        if (apiError.requiresLogout || apiError.statusCode in listOf(401, 403)) {
            SecurityLogger.logSecurityEvent(
                context,
                "TokenAuthenticator",
                "Clearing tokens due to refresh failure that requires logout"
            )
        }

        tokenProvider.clearAllTokens()
        return null
    }
    
    /**
     * Count response chain to prevent infinite retries
     * 
     * SECURITY: Prevents infinite retry loops
     */
    private fun responseCount(response: Response): Int {
        var result = 1
        var current = response.priorResponse
        while (current != null) {
            result++
            current = current.priorResponse
        }
        return result
    }
    
    /**
     * Check if authenticator is ready to handle requests
     * 
     * SECURITY: Validates authenticator state
     */
    fun isReady(): Boolean {
        return tokenProvider.hasCredentialsForRefresh() && 
               tokenProvider.canAttemptRefresh()
    }
    
    /**
     * Get authenticator status for debugging
     * 
     * SECURITY: Returns status without exposing sensitive data
     */
    fun getStatus(): Map<String, Any> {
        return mapOf(
            "isReady" to isReady(),
            "hasCredentials" to tokenProvider.hasCredentialsForRefresh(),
            "canAttemptRefresh" to tokenProvider.canAttemptRefresh(),
            "tokenInfo" to tokenProvider.getTokenInfo()
        )
    }
}
