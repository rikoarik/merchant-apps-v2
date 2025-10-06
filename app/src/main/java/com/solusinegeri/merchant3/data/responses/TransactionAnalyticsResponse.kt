package com.solusinegeri.merchant3.data.responses

/**
 * Response model untuk analitik transaksi merchant
 */
data class TransactionAnalyticsResponse(
    val data: TransactionAnalyticsData?,
    val message: String?,
    val status_code: String?,
    val type: String?
)

/**
 * Data model untuk analitik transaksi
 */
data class TransactionAnalyticsData(
    val totalIncome: Double?,
    val totalExpense: Double?,
    val transactionCount: Int?,
    val profitPercentage: Double?
)
