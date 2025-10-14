package com.solusinegeri.merchant3.data.responses

import com.google.gson.annotations.SerializedName

/**
 * Response model untuk analitik transaksi merchant
 */
data class TransactionAnalyticsResponse(
    @SerializedName("data")
    val `data`: Any? = null, // Accept both List and Object
    val message: Any? = null,
    val paging: Paging? = null,
    @SerializedName("status_code")
    val statusCode: String? = null,
    val type: String? = null
) {
    // Helper function to get data as list
    fun getDataAsList(): List<TransactionAnalyticsData> {
        return when (data) {
            is List<*> -> {
                data.filterIsInstance<Map<String, Any>>().mapNotNull { map ->
                    try {
                        TransactionAnalyticsData(
                            date = map["date"] as? String,
                            totalAmountTransaction = (map["totalAmountTransaction"] as? Number)?.toInt(),
                            totalNumberTransaction = (map["totalNumberTransaction"] as? Number)?.toInt()
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            is Map<*, *> -> {
                // If data is a single object, wrap it in a list
                try {
                    listOf(
                        TransactionAnalyticsData(
                            date = data["date"] as? String,
                            totalAmountTransaction = (data["totalAmountTransaction"] as? Number)?.toInt(),
                            totalNumberTransaction = (data["totalNumberTransaction"] as? Number)?.toInt(),
                            totalAmountCredit = (data["totalAmountCredit"] as? Number)?.toInt(),
                            totalAmountDebt = (data["totalAmountDebt"] as? Number)?.toInt()
                        )
                    )
                } catch (e: Exception) {
                    emptyList()
                }
            }
            else -> emptyList()
        }
    }
}

/**
 * Data model untuk analitik transaksi
 */
data class TransactionAnalyticsData(
    val date: String? = null,
    val totalAmountTransaction: Int? = null,
    val totalNumberTransaction: Int? = null,
    val totalAmountCredit: Int? = null,
    val totalAmountDebt: Int? = null
)

data class Paging(
    val dir: Int? = null,
    val page: Int? = null,
    val size: Int? = null,
    val sortBy: String? = null,
    val total: Int? = null
)
