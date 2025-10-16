package com.solusinegeri.merchant3.data.network

import com.solusinegeri.merchant3.data.model.PasswordEditModel
import com.solusinegeri.merchant3.data.model.StrengthPasswordResponse
import com.solusinegeri.merchant3.data.requests.LoginRequest
import com.solusinegeri.merchant3.data.requests.UpdateUserRequest
import com.solusinegeri.merchant3.data.responses.InitialCompanyResponse
import com.solusinegeri.merchant3.data.responses.LoginResponse
import com.solusinegeri.merchant3.data.responses.UserProfileResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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

    @Headers("Content-Type: application/json")
    @POST("/user/account/merchant/change_password")
    suspend fun changePassword(@Body info: PasswordEditModel): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("/user/account/merchant/profile")
    suspend fun updateProfile(@Body info: UpdateUserRequest): Response<ResponseBody>

    @GET("/user/info/company/get_password_strength/{companyId}")
    suspend fun getConfigStrengthPassword(
        @Path("companyId") companyId: String
    ): Response<StrengthPasswordResponse>

    @POST("/user/account/merchant/change_profile_picture")
    @Multipart
    suspend fun postImage(
        @Part file: MultipartBody.Part
    ): Response<UserProfileResponse>

    @GET("/user/account/merchant/profile")
    suspend fun getProfile(): Response<UserProfileResponse>
}