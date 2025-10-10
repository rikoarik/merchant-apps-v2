package com.solusinegeri.merchant3.data.network

import com.solusinegeri.merchant3.data.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

/**
 * PIN API interface untuk manajemen PIN
 */
interface PinCodeApi {

    @PUT("/user/account/merchant/security-code")
    suspend fun changePin(
        @Body request: ChangePinRequest
    ): Response<ResponseBody>

}