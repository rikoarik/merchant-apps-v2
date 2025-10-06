package com.solusinegeri.merchant3.data.responses

/**
 * Response model untuk balance - sesuai dengan API response yang sebenarnya
 */
data class BalanceResponse(
    val subCompanyId: String?,
    val _id: String?,
    val balanceName: String?,
    val balanceCode: String?,
    val amount: Double?,
    val isBlocked: Boolean?,
    val vaNumbers: List<VaNumber>?,
    val dailyLimit: DailyLimit?,
    val monthlyLimit: MonthlyLimit?,
    val name: String?,
    val noId: String?
)

/**
 * Data model untuk Virtual Account Number
 */
data class VaNumber(
    val id: String?,
    val virtualAccountNumber: String?,
    val provider: String?,
    val bank: String?,
    val customizeId: String?,
    val amount: Double?,
    val expiredDate: String?,
    val name: String?,
    val desc: String?,
    val billingType: String?,
    val status: String?,
    val bankCode: String?
)

/**
 * Data model untuk Daily Limit
 */
data class DailyLimit(
    val liveLimit: Double?
)

/**
 * Data model untuk Monthly Limit
 */
data class MonthlyLimit(
    val liveLimit: Double?
)

/**
 * Data model untuk balance yang digunakan di UI
 */
data class BalanceData(
    val balance: Double?,
    val balanceCode: String?,
    val currency: String?,
    val isBlocked: Boolean?,
    val vaNumbers: List<VaNumber>?,
    val dailyLimit: DailyLimit?,
    val monthlyLimit: MonthlyLimit?
)
