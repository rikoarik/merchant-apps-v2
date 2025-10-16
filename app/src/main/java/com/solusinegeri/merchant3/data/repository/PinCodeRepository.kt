package com.solusinegeri.merchant3.data.repository

import com.solusinegeri.merchant3.data.model.ChangePinRequest
import com.solusinegeri.merchant3.data.network.NetworkClient
import com.solusinegeri.merchant3.data.network.NewsApi
import com.solusinegeri.merchant3.data.network.PinCodeApi
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response


/**
 * Repository untuk manajemen PIN
 */
class PinCodeRepository {


    private val pinApi: PinCodeApi by lazy {
        NetworkClient.createService(PinCodeApi::class.java)
    }
    /**
     * Ganti PIN
     */
    suspend fun changePin(oldPin: String, newPin: String): Result<String> {
        return try {
            val request = ChangePinRequest(
                newSecurityCode = newPin,
                oldSecurityCode = oldPin
            )
            val response = pinApi.changePin(request)
            handleResponse(response, "PIN berhasil diubah")
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Gagal mengubah PIN"))
        }
    }

    /**
     * Helper function untuk handle response
     */
    private fun handleResponse(response: Response<ResponseBody>, successMessage: String): Result<String> {
        return if (response.isSuccessful) {
            Result.success(successMessage)
        } else {
            val errorMsg = parseErrorMessage(response.errorBody())
            Result.failure(Exception(errorMsg))
        }
    }


    private fun parseErrorMessage(errorBody: ResponseBody?): String {
        return try {
            val errorJson = errorBody?.string()
            if (!errorJson.isNullOrEmpty()) {
                val jsonObject = JSONObject(errorJson)
                jsonObject.optString("message", "Terjadi kesalahan")
            } else {
                "Terjadi kesalahan"
            }
        } catch (e: Exception) {
            "Terjadi kesalahan"
        }
    }
}