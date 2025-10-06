package com.solusinegeri.merchant3.data.network

import com.solusinegeri.merchant3.data.responses.BalanceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Balance API interface untuk data saldo
 */
interface BalanceApi {

    @GET("/balance/merchant/balance/{balanceCode}")
    suspend fun getMerchantBalance(
        @Path("balanceCode") balanceCode: String
    ): Response<BalanceResponse>
}
