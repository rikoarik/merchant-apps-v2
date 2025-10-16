package com.solusinegeri.merchant3.data.responses

import com.google.gson.annotations.SerializedName

data class DetailTransactionResponse (
    @SerializedName("data")
    val `data`: Any?, // Accept both List and Object
    val message: String? = null,
    @SerializedName("status_code")
    val statusCode: String? = null,
    val type: String? = null
) {
    // Helper function to get data as list
    fun getDataAsList(): List<DetailTransactionData> {
        return when (data) {
            is List<*> -> {
                data.filterIsInstance<Map<String, Any>>().mapNotNull { map ->
                    try {
                        DetailTransactionData(
                            transactionName = map["transactionName"] as? String ?: "",
                            transactionType = map["transactionType"] as? String ?: "",
                            amount = (map["amount"] as? Number)?.toInt() ?: 0
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
                        DetailTransactionData(
                            transactionName = data["transactionName"] as? String ?: "",
                            transactionType = data["transactionType"] as? String ?: "",
                            amount = (data["amount"] as? Number)?.toInt() ?: 0
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

data class DetailTransactionData(
    val transactionName: String,
    val transactionType: String,
    val amount: Int  // This is the total amount, not count
)