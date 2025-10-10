package com.solusinegeri.merchant3.data.repository

import android.content.Context
import com.solusinegeri.merchant3.core.utils.ErrorParser
import com.solusinegeri.merchant3.data.model.PasswordEditModel
import com.solusinegeri.merchant3.data.network.AuthService
import com.solusinegeri.merchant3.data.network.NetworkClient
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
                val responseNotNull    = passChangeResponse != null

                if(responseNotNull){ Result.success(passChangeResponse) }
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

}