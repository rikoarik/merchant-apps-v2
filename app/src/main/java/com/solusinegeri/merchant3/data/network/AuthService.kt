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
 */
interface AuthService {
    @POST("/authentication/merchant/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("/user/info/company/get/{nameOrInitial}")
    suspend fun getInitialCompany(
        @Path("nameOrInitial") nameOrInitial: String,
    ): Response<InitialCompanyResponse>
    
    @POST("/authentication/merchant/login")
    suspend fun refreshToken(@Body request: LoginRequest): Response<LoginResponse>
}