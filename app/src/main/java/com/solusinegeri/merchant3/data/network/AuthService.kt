package com.solusinegeri.merchant3.data.network

import com.solusinegeri.merchant3.data.requests.LoginRequest
import com.solusinegeri.merchant3.data.responses.InitialCompanyResponse
import com.solusinegeri.merchant3.data.responses.LoginResponse
import retrofit2.http.Header
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Authentication service for API calls
 * 
 * SECURITY NOTES:
 * - This service handles all authentication-related API calls
 * - Login endpoint is used for both initial login and token refresh
 * - No separate refresh endpoint as per requirements
 * - All sensitive operations should be logged for security auditing
 */
interface AuthService {
    
    /**
     * Login endpoint - used for both initial login and token refresh
     * 
     * SECURITY CONSIDERATIONS:
     * - Credentials are sent over HTTPS only
     * - Response contains auth token that expires in 3600 seconds (1 hour)
     * - Failed attempts should be rate-limited
     * - Never log request/response body containing credentials or tokens
     */
    @POST("/authentication/merchant/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    /**
     * Get company information by name or initial
     * 
     * SECURITY NOTES:
     * - This endpoint doesn't require authentication
     * - Used for company lookup during initial setup
     */
    @GET("/user/info/company/get/{nameOrInitial}")
    suspend fun getInitialCompany(
        @Path("nameOrInitial") nameOrInitial: String,
    ): Response<InitialCompanyResponse>
    
    /**
     * Token refresh endpoint - uses same login endpoint
     * 
     * SECURITY CONSIDERATIONS:
     * - Uses stored credentials for automatic token refresh
     * - Called automatically when token expires or 401 response received
     * - Credentials are retrieved from encrypted storage
     * - This is essentially a re-login with stored credentials
     */
    @POST("/authentication/merchant/login")
    suspend fun refreshToken(@Body request: LoginRequest): Response<LoginResponse>
}