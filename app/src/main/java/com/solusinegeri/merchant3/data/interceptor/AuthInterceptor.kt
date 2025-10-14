package com.solusinegeri.merchant3.data.interceptor

import android.content.Context
import com.solusinegeri.merchant3.core.auth.TokenProvider
import com.solusinegeri.merchant3.core.network.ApiException
import com.solusinegeri.merchant3.core.security.SecurityLogger
import com.solusinegeri.merchant3.core.utils.ApiErrorHandler
import com.solusinegeri.merchant3.data.repository.AuthRepository
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Enhanced AuthInterceptor untuk menambahkan token autentikasi ke semua request API
 * 
 * SECURITY BEST PRACTICES:
 * - Uses TokenProvider for centralized token management
 * - Automatically adds Bearer token to requests
 * - Skips authentication for public endpoints
 * - Logs authentication events for security auditing
 * - Never logs actual token values
 * - Validates token before adding to requests
 */
class AuthInterceptor(private val context: Context) : Interceptor {
    
    private val tokenProvider = TokenProvider(context)
    private val authRepository by lazy { AuthRepository(context.applicationContext) }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        if (shouldSkipAuth(originalRequest.url.toString())) {
            return chain.proceed(originalRequest)
        }
        
        val token = tokenProvider.getAccessToken()
        
        val newRequest = if (!token.isNullOrEmpty()) {
            // Validate token before adding to request
            if (tokenProvider.isTokenValid()) {
                originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                // Token invalid - try to refresh if credentials available
                if (tokenProvider.hasCredentialsForRefresh()) {
                    attemptTokenRefresh(originalRequest)
                } else {
                    SecurityLogger.logSecurityEvent(
                        context, 
                        "AuthInterceptor", 
                        "Token invalid and no credentials available, proceeding without auth header"
                    )
                    originalRequest
                }
            }
        } else {
            SecurityLogger.logSecurityEvent(
                context, 
                "AuthInterceptor", 
                "No token available, proceeding without auth header"
            )
            originalRequest
        }
        
        return chain.proceed(newRequest)
    }
    
    /**
     * Check if request should skip authentication
     * 
     * SECURITY: Public endpoints that don't require authentication
     */
    private fun shouldSkipAuth(url: String): Boolean {
        val skipEndpoints = listOf(
            "/authentication/merchant/login",
            "/user/info/company/get"
        )
        
        return skipEndpoints.any { endpoint ->
            url.contains(endpoint, ignoreCase = true)
        }
    }

    private fun attemptTokenRefresh(originalRequest: Request): Request {
        return try {
            val refreshResult = kotlinx.coroutines.runBlocking {
                authRepository.refreshTokenIfNeeded()
            }

            refreshResult.fold(
                onSuccess = { refreshed ->
                    if (refreshed) {
                        val newToken = tokenProvider.getAccessToken()
                        if (!newToken.isNullOrEmpty()) {
                            originalRequest.newBuilder()
                                .addHeader("Authorization", "Bearer $newToken")
                                .build()
                        } else {
                            originalRequest
                        }
                    } else {
                        originalRequest
                    }
                },
                onFailure = { error ->
                    handleRefreshFailure(error)
                    originalRequest
                }
            )
        } catch (throwable: Throwable) {
            handleRefreshFailure(throwable)
            originalRequest
        }
    }

    private fun handleRefreshFailure(error: Throwable) {
        val apiError = when (error) {
            is ApiException -> error.error
            else -> ApiErrorHandler.resolve(error, context)
        }

        SecurityLogger.logSecurityEvent(
            context,
            "AuthInterceptor",
            "Token refresh failed: ${apiError.message}"
        )

        if (apiError.requiresLogout) {
            SecurityLogger.logSecurityEvent(
                context,
                "AuthInterceptor",
                "Clearing tokens because backend requested logout"
            )
            tokenProvider.clearAllTokens()
        }
    }
}

/**
 * Legacy AuthResponseInterceptor - DEPRECATED
 * 
 * SECURITY NOTE: This interceptor is replaced by TokenAuthenticator
 * which provides better handling of 401 responses with automatic
 * token refresh capabilities.
 * 
 * The TokenAuthenticator handles 401 responses by:
 * - Automatically attempting token refresh
 * - Using stored credentials for re-authentication
 * - Retrying failed requests with new token
 * - Failing securely if refresh cannot be completed
 */
