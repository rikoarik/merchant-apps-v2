package com.solusinegeri.merchant3.data.repository

import com.solusinegeri.merchant3.core.network.safeApiCall
import com.solusinegeri.merchant3.core.utils.ErrorParser
import com.solusinegeri.merchant3.data.network.AnalyticsApi
import com.solusinegeri.merchant3.data.network.NetworkClient
import com.solusinegeri.merchant3.data.responses.DetailTransactionResponse
import com.solusinegeri.merchant3.data.responses.SummaryAnalyticsResponse
import com.solusinegeri.merchant3.data.responses.TransactionAnalyticsResponse

class AnalyticsRepository(
    private val analyticsApi: AnalyticsApi = NetworkClient.createService(AnalyticsApi::class.java)
) {

    suspend fun getTransactionSummary(
        startDate: String,
        endDate: String,
        balanceCode: String
    ): Result<SummaryAnalyticsResponse> =
        safeApiCall(
            apiCall = { analyticsApi.getTransactionSummary(startDate, endDate, balanceCode) },
            onEmptyBody = { IllegalStateException("Data ringkasan transaksi tidak ditemukan") },
            errorParser = { ErrorParser.extractMessage(it.errorBody()) }
        )

    suspend fun getTransactionAnalytics(
        startDate: String,
        endDate: String,
        balanceCode: String
    ): Result<TransactionAnalyticsResponse> =
        safeApiCall(
            apiCall = { analyticsApi.getTransactionAnalytics(startDate, endDate, balanceCode) },
            onEmptyBody = { IllegalStateException("Data analitik transaksi tidak ditemukan") },
            errorParser = { ErrorParser.extractMessage(it.errorBody()) }
        )

    suspend fun getHistoryTransactionsAnalytics(
        startDate: String,
        endDate: String
    ): Result<DetailTransactionResponse> =
        safeApiCall(
            apiCall = { analyticsApi.getHistoryTransactionsAnalytics(startDate, endDate) },
            onEmptyBody = { IllegalStateException("Data riwayat transaksi tidak ditemukan") },
            errorParser = { ErrorParser.extractMessage(it.errorBody()) }
        )
}
