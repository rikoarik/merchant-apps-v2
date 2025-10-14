package com.solusinegeri.merchant3.data.repository

import android.content.Context
import androidx.compose.ui.geometry.Rect
import com.solusinegeri.merchant3.core.security.SecureStorage
import com.solusinegeri.merchant3.core.utils.ErrorParser
import com.solusinegeri.merchant3.data.model.PasswordEditModel
import com.solusinegeri.merchant3.data.model.UpdateUserModel
import com.solusinegeri.merchant3.data.model.UserData
import com.solusinegeri.merchant3.data.model.UserResponse
import com.solusinegeri.merchant3.data.network.AuthService
import com.solusinegeri.merchant3.data.network.NetworkClient
import com.solusinegeri.merchant3.data.responses.LoginResponse
import okhttp3.ResponseBody

class ProfileRepository(val appContext: Context) {

    private val userService: AuthService by lazy {
        NetworkClient.createService(AuthService::class.java)
    }

    /**
     * Changes user password
     */
    suspend fun changePassword(
        oldPassword    : String,
        newPassword    : String,
        confirmPassword: String
    ) : Result<ResponseBody>{
        return try {
            val request  = PasswordEditModel(oldPassword, newPassword, confirmPassword)
            val response = userService.changePassword(request)

            if(response.isSuccessful){
                val passChangeResponse = response.body()

                if(passChangeResponse != null){
                    savePassword(newPassword)
                    val refreshResponse =  refreshToken()
                    refreshResponse.fold(
                        onSuccess = { response -> Result.success(passChangeResponse) },
                        onFailure = { error    -> Result.failure(Exception("Refresh token failed: ${error.message}")) }
                    )
                }
                else{ Result.failure(Exception("Empty response")) }
            }
            else{
                val errMessage = ErrorParser.parseErrorBody(
                    response.errorBody()?.string() ?: "",
                    response.code()
                )
                Result.failure(Exception(errMessage))
            }

        } catch (err: Exception){
            Result.failure(Exception("Error dalam mengubah password ${err.message}"))
        }
    }

    /**
     * Fetches the user profile from BackEnd
     */
    suspend fun getProfile(): Result<UserData>{
        return try{
            val response = userService.getMerchantProfile()
            if(response.isSuccessful){
                val userResponse = response.body()
                if(userResponse?.data != null){
                    Result.success(userResponse.data)
                }
                else{
                    Result.failure(Exception("Result body is empty"))
                }
            }
            else{
                Result.failure(Exception("Nah bro, fuck you"))
            }
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    /**
     * Updates the user profile data
     */
    suspend fun updateProfile(request: UpdateUserModel): Result<ResponseBody>{
        return try {
            val response = userService.updateProfile(request)
            if(response.isSuccessful){
                val updateResponse = response.body()
                if(updateResponse != null){
                    Result.success(updateResponse)
                }
                else{
                    Result.failure(Exception("Empty response"))
                }
            }
            else{
                val errMessage = ErrorParser.parseErrorBody(
                    response.errorBody()?.string() ?: "",
                    response.code()
                )
                Result.failure(Exception(errMessage))
            }
        }
        catch (err: Exception){
            Result.failure(err)
        }
    }

    /**
     * Refreshes auth token after successful change password by calling login
     */
    private suspend fun refreshToken(): Result<LoginResponse>{
        return try{
            val authRepository = AuthRepository(appContext)
            val (companyId, username, password) = authRepository.getLoginCredentials()
            if (!companyId.isNullOrBlank() || !username.isNullOrBlank() || !password.isNullOrBlank()){
                AuthRepository(appContext).login(companyId!!, username!!, password!!)
            }
            else{
                Result.failure(Exception("Login credentials not found for refresh"))
            }
        }
        catch (e: Exception){
            Result.failure(Exception(e))
        }
    }

    /**
     * Saves password after change is successful
     */
    private fun savePassword(password: String) {
        SecureStorage.savePassword(appContext, password)
    }
}