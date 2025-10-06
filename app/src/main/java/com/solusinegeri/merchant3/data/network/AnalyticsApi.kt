package com.solusinegeri.merchant3.data.network

import com.solusinegeri.merchant3.data.responses.TransactionAnalyticsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Analytics API interface untuk data analitik transaksi
 */
interface AnalyticsApi {

    @GET("/balance/merchant/transaction/summary")
    suspend fun getTransactionSummary(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("balanceCode") balanceCode: String
    ): Response<TransactionAnalyticsResponse>

    @GET("/balance/merchant/transaction/analytics")
    suspend fun getTransactionAnalytics(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("balanceCode") balanceCode: String
    ): Response<TransactionAnalyticsResponse>
}
