package com.solusinegeri.merchant3.core.utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.solusinegeri.merchant3.data.responses.SummaryAnalyticsData
import com.solusinegeri.merchant3.data.responses.SummaryAnalyticsResponse
import com.solusinegeri.merchant3.data.responses.TransactionAnalyticsData
import com.solusinegeri.merchant3.data.responses.TransactionAnalyticsResponse

/**
 * Utility untuk parsing JSON response yang fleksibel
 * Menangani berbagai struktur response dari server
 */
object JsonResponseParser {
    
    private const val TAG = "JsonResponseParser"
    
    /**
     * Parse SummaryAnalyticsResponse dengan handling untuk berbagai struktur data
     */
    fun parseSummaryResponse(jsonString: String): SummaryAnalyticsResponse? {
        return try {
            val gson = Gson()
            val jsonElement = JsonParser.parseString(jsonString)
            
            if (jsonElement.isJsonObject) {
                val jsonObject = jsonElement.asJsonObject
                
                // Parse basic fields
                val message = jsonObject.get("message")?.asString
                val statusCode = jsonObject.get("status_code")?.asString
                val type = jsonObject.get("type")?.asString
                
                // Handle data field - bisa berupa array atau object
                val dataElement = jsonObject.get("data")
                val data: Any? = when {
                    dataElement?.isJsonArray == true -> {
                        // Data is array
                        val listType = object : TypeToken<List<SummaryAnalyticsData>>() {}.type
                        gson.fromJson<List<SummaryAnalyticsData>>(dataElement, listType)
                    }
                    dataElement?.isJsonObject == true -> {
                        // Data is single object
                        gson.fromJson(dataElement, SummaryAnalyticsData::class.java)
                    }
                    else -> null
                }
                
                SummaryAnalyticsResponse(
                    data = data,
                    message = message,
                    statusCode = statusCode,
                    type = type
                )
            } else {
                Log.e(TAG, "Invalid JSON structure - not an object")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing summary response: ${e.message}", e)
            null
        }
    }
    
    /**
     * Parse TransactionAnalyticsResponse dengan handling untuk berbagai struktur data
     */
    fun parseTransactionResponse(jsonString: String): TransactionAnalyticsResponse? {
        return try {
            val gson = Gson()
            val jsonElement = JsonParser.parseString(jsonString)
            
            if (jsonElement.isJsonObject) {
                val jsonObject = jsonElement.asJsonObject
                
                // Parse basic fields
                val message = jsonObject.get("message")
                val statusCode = jsonObject.get("status_code")?.asString
                val type = jsonObject.get("type")?.asString
                
                // Parse paging if exists
                val pagingElement = jsonObject.get("paging")
                val paging = if (pagingElement?.isJsonObject == true) {
                    gson.fromJson(pagingElement, com.solusinegeri.merchant3.data.responses.Paging::class.java)
                } else null
                
                // Handle data field - bisa berupa array atau object
                val dataElement = jsonObject.get("data")
                val data: Any? = when {
                    dataElement?.isJsonArray == true -> {
                        // Data is array
                        val listType = object : TypeToken<List<TransactionAnalyticsData>>() {}.type
                        gson.fromJson<List<TransactionAnalyticsData>>(dataElement, listType)
                    }
                    dataElement?.isJsonObject == true -> {
                        // Data is single object
                        gson.fromJson(dataElement, TransactionAnalyticsData::class.java)
                    }
                    else -> null
                }
                
                TransactionAnalyticsResponse(
                    data = data,
                    message = message,
                    paging = paging,
                    statusCode = statusCode,
                    type = type
                )
            } else {
                Log.e(TAG, "Invalid JSON structure - not an object")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing transaction response: ${e.message}", e)
            null
        }
    }
    
    /**
     * Log JSON structure untuk debugging
     */
    fun logJsonStructure(jsonString: String, responseType: String) {
        try {
            val jsonElement = JsonParser.parseString(jsonString)
            Log.d(TAG, "$responseType JSON Structure:")
            
            if (jsonElement.isJsonObject) {
                val jsonObject = jsonElement.asJsonObject
                jsonObject.entrySet().forEach { entry ->
                    val value = entry.value
                    when {
                        value.isJsonArray -> Log.d(TAG, "  ${entry.key}: Array (${value.asJsonArray.size()} items)")
                        value.isJsonObject -> Log.d(TAG, "  ${entry.key}: Object")
                        value.isJsonPrimitive -> Log.d(TAG, "  ${entry.key}: ${value.asJsonPrimitive}")
                        else -> Log.d(TAG, "  ${entry.key}: null")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error logging JSON structure: ${e.message}")
        }
    }
}
