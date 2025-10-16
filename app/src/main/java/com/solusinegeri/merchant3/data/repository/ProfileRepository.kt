package com.solusinegeri.merchant3.data.repository

import android.content.Context
import com.solusinegeri.merchant3.core.network.safeApiCall
import com.solusinegeri.merchant3.core.security.SecureStorage
import com.solusinegeri.merchant3.core.utils.ErrorParser
import com.solusinegeri.merchant3.data.model.PasswordEditModel
import com.solusinegeri.merchant3.data.model.UserData
import com.solusinegeri.merchant3.data.network.AuthService
import com.solusinegeri.merchant3.data.network.NetworkClient
import com.solusinegeri.merchant3.data.requests.UpdateUserRequest
import com.solusinegeri.merchant3.data.responses.LoginResponse
import com.solusinegeri.merchant3.data.responses.UserProfileResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody

class ProfileRepository(
    private val appContext: Context,
    private val userService: AuthService = NetworkClient.createService(AuthService::class.java),
    private val authRepositoryProvider: (Context) -> AuthRepository = { context -> AuthRepository(context) }
) {

    /**
     * Changes the User Password
     */
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

    /**
     * Fetches the Merchant's User Data
     */
    suspend fun getProfile(): Result<UserData> =
        safeApiCall(
            apiCall = { userService.getProfile() },
            onEmptyBody = { IllegalStateException("Result body is empty") },
            errorParser = { ErrorParser.parseErrorResponse(it) }
        ).mapCatching { response ->
            response.data ?: throw IllegalStateException("Data user tidak ditemukan")
        }

    /**
     * Updates the user profile data
     */
    suspend fun updateProfile(request: UpdateUserRequest): Result<ResponseBody> =
        safeApiCall(
            apiCall = { userService.updateProfile(request) },
            onEmptyBody = { IllegalStateException("Response body kosong") },
            errorParser = { ErrorParser.parseErrorResponse(it) }
        )

    /**
     * Refreshes auth token after successful change password by calling login
     */

    private suspend fun refreshToken(): Result<LoginResponse> {
        val authRepository = authRepositoryProvider(appContext)
        val (companyId, username, password) = authRepository.getLoginCredentials()
        if (companyId.isNullOrBlank() || username.isNullOrBlank() || password.isNullOrBlank()) {
            return Result.failure(IllegalStateException("Login credentials not found for refresh"))
        }
        return authRepository.login(companyId, username, password)
    }

    /**
     * Uploads the profile picture
     */
    suspend fun uploadProfilePicture(picture: MultipartBody.Part) : Result<UserData>{
        return safeApiCall(
            apiCall = { userService.postImage(picture) },
            onEmptyBody = { IllegalStateException("Result body is empty") },
            errorParser = { ErrorParser.parseErrorResponse(it) }
        ).mapCatching { response ->
            response.data ?: throw IllegalStateException("Tidak dapat mengupload image")
        }
    }

    /**
     * Saves password after change is successful
     */
    private fun savePassword(password: String) {
        SecureStorage.savePassword(appContext, password)
    }
}
