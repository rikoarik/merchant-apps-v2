package com.solusinegeri.merchant3.data.repository

import com.solusinegeri.merchant3.core.base.BaseRepository
import com.solusinegeri.merchant3.data.network.AnalyticsApi
import com.solusinegeri.merchant3.data.network.NetworkClient
import com.solusinegeri.merchant3.data.responses.DetailTransactionResponse
import com.solusinegeri.merchant3.data.responses.SummaryAnalyticsResponse
import com.solusinegeri.merchant3.data.responses.TransactionAnalyticsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AnalyticsRepository : BaseRepository() {

    private val analyticsApi: AnalyticsApi by lazy {
        NetworkClient.createService(AnalyticsApi::class.java)
    }

    suspend fun getTransactionSummary(
        startDate: String,
        endDate: String,
        balanceCode: String
    ): Result<SummaryAnalyticsResponse> = withContext(Dispatchers.IO) {
        request { analyticsApi.getTransactionSummary(startDate, endDate, balanceCode) }
    }

    suspend fun getTransactionAnalytics(
        startDate: String,
        endDate: String,
        balanceCode: String
    ): Result<TransactionAnalyticsResponse> = withContext(Dispatchers.IO) {
        request { analyticsApi.getTransactionAnalytics(startDate, endDate, balanceCode) }
    }

    suspend fun getHistoryTransactionsAnalytics(
        startDate: String,
        endDate: String
    ): Result<DetailTransactionResponse> = withContext(Dispatchers.IO) {
        request { analyticsApi.getHistoryTransactionsAnalytics(startDate, endDate) }
    }
}
