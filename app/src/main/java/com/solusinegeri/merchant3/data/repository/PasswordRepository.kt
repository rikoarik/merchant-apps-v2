package com.solusinegeri.merchant3.data.repository

import android.content.Context
import com.solusinegeri.merchant3.core.security.SecureStorage
import com.solusinegeri.merchant3.core.utils.ErrorParser
import com.solusinegeri.merchant3.data.model.PasswordEditModel
import com.solusinegeri.merchant3.data.network.AuthService
import com.solusinegeri.merchant3.data.network.NetworkClient
import com.solusinegeri.merchant3.data.responses.LoginResponse
import okhttp3.ResponseBody

class PasswordRepository(val appContext: Context) {

    private val authApi: AuthService by lazy {
        NetworkClient.createService(AuthService::class.java)
    }

    suspend fun changePassword(
        oldPassword    : String,
        newPassword    : String,
        confirmPassword: String
    ) : Result<ResponseBody>{
        return try {
            val request  = PasswordEditModel(oldPassword, newPassword, confirmPassword)
            val response = authApi.changePassword(request)

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


    private fun savePassword(password: String) {
        SecureStorage.savePassword(appContext, password)
    }

}