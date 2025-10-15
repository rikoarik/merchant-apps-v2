package com.solusinegeri.merchant3.data.repository

import android.content.Context
import com.solusinegeri.merchant3.core.network.safeApiCall
import com.solusinegeri.merchant3.core.security.SecureStorage
import com.solusinegeri.merchant3.core.utils.ErrorParser
import com.solusinegeri.merchant3.data.model.PasswordEditModel
import com.solusinegeri.merchant3.data.model.UpdateUserModel
import com.solusinegeri.merchant3.data.model.UserData
import com.solusinegeri.merchant3.data.network.AuthService
import com.solusinegeri.merchant3.data.network.NetworkClient
import com.solusinegeri.merchant3.data.responses.LoginResponse
import okhttp3.ResponseBody

class ProfileRepository(
    private val appContext: Context,
    private val userService: AuthService = NetworkClient.createService(AuthService::class.java),
    private val authRepositoryProvider: (Context) -> AuthRepository = { context -> AuthRepository(context) }
) {

    suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Result<ResponseBody> {
        val request = PasswordEditModel(oldPassword, newPassword, confirmPassword)

        return safeApiCall(
            apiCall = { userService.changePassword(request) },
            onEmptyBody = { IllegalStateException("Response body kosong") },
            errorParser = { ErrorParser.parseErrorResponse(it) }
        ).mapCatching { responseBody ->
            savePassword(newPassword)
            refreshToken().getOrElse { error -> throw error }
            responseBody
        }
    }

    suspend fun getProfile(): Result<UserData> =
        safeApiCall(
            apiCall = { userService.getMerchantProfile() },
            onEmptyBody = { IllegalStateException("Result body is empty") },
            errorParser = { ErrorParser.parseErrorResponse(it) }
        ).mapCatching { response ->
            response.data ?: throw IllegalStateException("Data user tidak ditemukan")
        }

    suspend fun updateProfile(request: UpdateUserModel): Result<ResponseBody> =
        safeApiCall(
            apiCall = { userService.updateProfile(request) },
            onEmptyBody = { IllegalStateException("Response body kosong") },
            errorParser = { ErrorParser.parseErrorResponse(it) }
        )

    private suspend fun refreshToken(): Result<LoginResponse> {
        val authRepository = authRepositoryProvider(appContext)
        val (companyId, username, password) = authRepository.getLoginCredentials()
        if (companyId.isNullOrBlank() || username.isNullOrBlank() || password.isNullOrBlank()) {
            return Result.failure(IllegalStateException("Login credentials not found for refresh"))
        }
        return authRepository.login(companyId, username, password)
    }

    private fun savePassword(password: String) {
        SecureStorage.savePassword(appContext, password)
    }
}
