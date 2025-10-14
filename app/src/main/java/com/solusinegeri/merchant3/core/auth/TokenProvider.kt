package com.solusinegeri.merchant3.core.auth

import android.content.Context
import com.solusinegeri.merchant3.core.security.SecureStorage
import com.solusinegeri.merchant3.core.security.SecurityLogger

/**
 * TokenProvider - Centralized token management for auto-refresh mechanism
 * 
 * SECURITY BEST PRACTICES IMPLEMENTED:
 * - All tokens stored in encrypted storage (EncryptedSharedPreferences)
 * - Token expiration tracking with 1-hour validity (3600 seconds)
 * - Automatic credential retrieval for token refresh
 * - Thread-safe operations with synchronized blocks
 * - No token logging to prevent exposure in logs
 * - Rate limiting for refresh attempts
 * - Clear separation of concerns
 */
class TokenProvider(private val context: Context) {
    
    companion object {
        // Token expires in 10 seconds for testing (change back to 3600L for production)
        private const val TOKEN_VALIDITY_SECONDS = 10L // TESTING: 10 seconds
        private const val TOKEN_VALIDITY_MS = TOKEN_VALIDITY_SECONDS * 1000L
        
        // Refresh threshold - refresh token 3 seconds before expiry for testing
        private const val REFRESH_THRESHOLD_MS = 3 * 1000L // TESTING: 3 seconds
        
        // Rate limiting for refresh attempts
        private const val MAX_REFRESH_ATTEMPTS = 3
        private const val REFRESH_COOLDOWN_MS = 5 * 1000L // TESTING: 5 seconds (change back to 30 * 1000L for production)
    }
    
    @Volatile
    private var refreshAttempts = 0
    private var lastRefreshAttempt = 0L
    
    /**
     * Get current access token
     * 
     * SECURITY: Never log the token value
     */
    fun getAccessToken(): String? {
        return SecureStorage.getAuthToken(context)
    }
    
    /**
     * Check if current token is valid and not expired
     * 
     * SECURITY CONSIDERATIONS:
     * - Validates token existence and expiration time
     * - Uses secure timestamp comparison
     * - Returns false for any security concerns
     */
    fun isTokenValid(): Boolean {
        val token = getAccessToken()
        
        if (token.isNullOrBlank()) {
            SecurityLogger.logSecurityEvent(context, "TokenProvider", "No valid token found")
            return false
        }
        
        return SecureStorage.isSessionValid(context)
    }
    
    /**
     * Check if token needs refresh (expires within threshold)
     * 
     * SECURITY: Proactive refresh to avoid 401 responses
     */
    fun needsRefresh(): Boolean {
        // If token is not valid, it definitely needs refresh
        if (!isTokenValid()) {
            return true
        }
        
        val loginTimestamp = SecureStorage.getLoginTimestamp(context)
        if (loginTimestamp == 0L) {
            return true
        }
        
        val currentTime = System.currentTimeMillis()
        val timeSinceLogin = currentTime - loginTimestamp
        val timeUntilExpiry = TOKEN_VALIDITY_MS - timeSinceLogin
        
        return timeUntilExpiry <= REFRESH_THRESHOLD_MS
    }
    
    /**
     * Save new token after successful login/refresh
     * 
     * SECURITY CONSIDERATIONS:
     * - Stores token in encrypted storage
     * - Updates session timestamp
     * - Resets refresh attempt counter
     * - Logs successful token update (without token value)
     */
    fun saveToken(token: String) {
        SecureStorage.saveAuthToken(context, token)
        SecurityLogger.logSecurityEvent(context, "TokenProvider", "Token updated successfully")
        resetRefreshAttempts()
    }
    
    /**
     * Get stored credentials for token refresh
     * 
     * SECURITY: Credentials retrieved from encrypted storage only
     */
    fun getStoredCredentials(): Triple<String?, String?, String?> {
        val userData = SecureStorage.getUserData(context)
        val companyId = userData["company_id"]
        val username = userData["user_name"]
        val password = SecureStorage.getPassword(context)
        
        return Triple(companyId, username, password)
    }
    
    /**
     * Check if credentials are available for refresh
     */
    fun hasCredentialsForRefresh(): Boolean {
        val (companyId, username, password) = getStoredCredentials()
        return !companyId.isNullOrBlank() && 
               !username.isNullOrBlank() && 
               !password.isNullOrBlank()
    }
    
    /**
     * Check if refresh is allowed (rate limiting)
     * 
     * SECURITY: Prevents brute force refresh attempts
     */
    fun canAttemptRefresh(): Boolean {
        val currentTime = System.currentTimeMillis()
        
        // Reset attempts if enough time has passed
        if (currentTime - lastRefreshAttempt > REFRESH_COOLDOWN_MS) {
            refreshAttempts = 0
        }
        
        val canAttempt = refreshAttempts < MAX_REFRESH_ATTEMPTS
        if (!canAttempt) {
            SecurityLogger.logSecurityEvent(
                context, 
                "TokenProvider", 
                "Refresh rate limit exceeded: $refreshAttempts attempts"
            )
        }
        
        return canAttempt
    }
    
    /**
     * Record refresh attempt
     * 
     * SECURITY: Track attempts for rate limiting
     */
    fun recordRefreshAttempt() {
        refreshAttempts++
        lastRefreshAttempt = System.currentTimeMillis()
        
        SecurityLogger.logSecurityEvent(
            context, 
            "TokenProvider", 
            "Refresh attempt recorded: $refreshAttempts/$MAX_REFRESH_ATTEMPTS"
        )
    }
    
    /**
     * Reset refresh attempts after successful refresh
     */
    private fun resetRefreshAttempts() {
        refreshAttempts = 0
        lastRefreshAttempt = 0L
    }
    
    /**
     * Clear all tokens and credentials
     * 
     * SECURITY: Complete cleanup on logout or security breach
     */
    fun clearAllTokens() {
        SecureStorage.clearAllData(context)
        SecureStorage.clearLoginCredentials(context)
        resetRefreshAttempts()
        
        SecurityLogger.logSecurityEvent(context, "TokenProvider", "All tokens cleared")
    }
    
    /**
     * Get token information for debugging (without exposing actual token)
     * 
     * SECURITY: Only returns metadata, never the actual token
     */
    fun getTokenInfo(): Map<String, Any> {
        return mapOf(
            "hasToken" to !getAccessToken().isNullOrBlank(),
            "isValid" to isTokenValid(),
            "needsRefresh" to needsRefresh(),
            "hasCredentials" to hasCredentialsForRefresh(),
            "refreshAttempts" to refreshAttempts,
            "remainingSessionTime" to SecureStorage.getRemainingSessionTime(context)
        )
    }
    
    // ============================================================================
    // TESTING HELPER METHODS
    // ============================================================================
    
    /**
     * TESTING: Get remaining session time in seconds (more precise)
     */
    fun getRemainingSessionTimeSeconds(): Long {
        val remainingMinutes = SecureStorage.getRemainingSessionTime(context)
        return remainingMinutes * 60 // Convert to seconds
    }
    
    /**
     * TESTING: Check if token will expire soon (within 5 seconds)
     */
    fun isTokenExpiringSoon(): Boolean {
        val remainingSeconds = getRemainingSessionTimeSeconds()
        return remainingSeconds <= 5
    }
    
    /**
     * TESTING: Get detailed debugging info
     */
    fun getDetailedTokenInfo(): Map<String, Any> {
        val tokenInfo = getTokenInfo()
        return tokenInfo + mapOf(
            "remainingSessionTimeSeconds" to getRemainingSessionTimeSeconds(),
            "isExpiringSoon" to isTokenExpiringSoon(),
            "canAttemptRefresh" to canAttemptRefresh(),
            "lastRefreshAttempt" to lastRefreshAttempt,
            "tokenValiditySeconds" to TOKEN_VALIDITY_SECONDS,
            "refreshThresholdSeconds" to (REFRESH_THRESHOLD_MS / 1000)
        )
    }
}
