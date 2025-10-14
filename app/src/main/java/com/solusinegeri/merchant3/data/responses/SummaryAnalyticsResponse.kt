package com.solusinegeri.merchant3.data.responses

import com.google.gson.annotations.SerializedName

data class SummaryAnalyticsResponse (
    @SerializedName("data")
    val `data`: Any?, // Accept both List and Object
    val message: String? = null,
    @SerializedName("status_code")
    val statusCode: String? = null,
    val type: String? = null
) {
    // Helper function to get data as list
    fun getDataAsList(): List<SummaryAnalyticsData> {
        return when (data) {
            is List<*> -> {
                data.filterIsInstance<Map<String, Any>>().mapNotNull { map ->
                    try {
                        SummaryAnalyticsData(
                            amount = (map["amount"] as? Number)?.toInt() ?: 0,
                            total = (map["total"] as? Number)?.toInt() ?: 0,
                            transactionName = map["transactionName"] as? String ?: "",
                            transactionType = map["transactionType"] as? String ?: ""
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
                        SummaryAnalyticsData(
                            amount = (data["amount"] as? Number)?.toInt() ?: 0,
                            total = (data["total"] as? Number)?.toInt() ?: 0,
                            transactionName = data["transactionName"] as? String ?: "",
                            transactionType = data["transactionType"] as? String ?: ""
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

data class SummaryAnalyticsData(
    val amount: Int,
    val total: Int,
    val transactionName: String,
    val transactionType: String
)