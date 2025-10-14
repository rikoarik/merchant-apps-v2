package com.solusinegeri.merchant3.core.utils

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.solusinegeri.merchant3.data.responses.ErrorResponse
import com.solusinegeri.merchant3.data.responses.ValidationErrorResponse
import com.solusinegeri.merchant3.data.responses.getUserFriendlyMessage
import retrofit2.Response

/**
 * Utility class untuk parsing error response dari backend
 * Mendukung multiple format error response dan memberikan user-friendly messages
 */
object ErrorParser {
    
    private val gson = Gson()
    
    /**
     * Parse error response dari HTTP response
     * Mendukung format:
     * 1. Validation errors: {"errorCodes":[],"errors":{"companyId":["MustNotBlank"]}}
     * 2. Standard errors: {"detail":{"type":"COMPANY_NOT_FOUND","message":"Company not found","status_code":404,"error":"NOT_FOUND"}}
     * 3. Fallback ke HTTP status code messages
     */
    fun parseErrorResponse(response: Response<*>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            if (!errorBody.isNullOrEmpty()) {
                parseErrorBody(errorBody, response.code())
            } else {
                getHttpStatusMessage(response.code())
            }
        } catch (e: Exception) {
            "Terjadi kesalahan yang tidak diketahui: ${e.message}"
        }
    }
    
    /**
     * Parse error response dari error body string
     * Mendukung multiple format error response
     * Prioritas: message dari backend -> error code -> HTTP status
     */
    fun parseErrorBody(errorBody: String, httpCode: Int = 400): String {
        return try {
            try {
                val validationError = gson.fromJson(errorBody, ValidationErrorResponse::class.java)
                if (validationError.errors.isNotEmpty()) {
                    return validationError.getUserFriendlyMessage()
                }
            } catch (e: JsonSyntaxException) {
            }
            
            try {
                val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                return errorResponse.getUserFriendlyMessage()
            } catch (e: JsonSyntaxException) {
            }
            
            "Error $httpCode"
        } catch (e: Exception) {
            "Error $httpCode"
        }
    }
    
    /**
     * Get user-friendly message berdasarkan HTTP status code
     */
    fun getHttpStatusMessage(httpCode: Int): String {
        return when (httpCode) {
            400 -> "Data yang dimasukkan tidak valid. Silakan periksa kembali."
            401 -> "Username atau password salah. Silakan coba lagi."
            403 -> "Akses ditolak. Silakan hubungi administrator."
            404 -> "Data tidak ditemukan. Silakan periksa kembali."
            409 -> "Data sudah ada. Silakan gunakan data yang berbeda."
            422 -> "Data tidak valid. Silakan periksa kembali input Anda."
            429 -> "Terlalu banyak permintaan. Silakan tunggu sebentar."
            500 -> "Terjadi kesalahan pada server. Silakan coba lagi nanti."
            502 -> "Server sedang dalam maintenance. Silakan coba lagi nanti."
            503 -> "Service tidak tersedia. Silakan coba lagi nanti."
            else -> "Terjadi kesalahan: $httpCode"
        }
    }
    
    /**
     * Parse validation error response khusus untuk login
     * Prioritas: message dari backend -> error code -> HTTP status
     */
    fun parseLoginError(errorBody: String, httpCode: Int = 400): String {
        return try {
            try {
                val validationError = gson.fromJson(errorBody, ValidationErrorResponse::class.java)
                if (validationError.errors.isNotEmpty()) {
                    return validationError.getUserFriendlyMessage()
                }
            } catch (e: JsonSyntaxException) {
            }
            
            try {
                val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                return errorResponse.getUserFriendlyMessage()
            } catch (e: JsonSyntaxException) {
            }
            
            "Error $httpCode"
        } catch (e: Exception) {
            "Error $httpCode"
        }
    }
    
    /**
     * Parse company error response khusus untuk company validation
     * Prioritas: message dari backend -> error code -> HTTP status
     */
    fun parseCompanyError(errorBody: String, httpCode: Int = 400): String {
        return try {
            try {
                val validationError = gson.fromJson(errorBody, ValidationErrorResponse::class.java)
                if (validationError.errors.isNotEmpty()) {
                    return validationError.getUserFriendlyMessage()
                }
            } catch (e: JsonSyntaxException) {
            }
            
            try {
                val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                return errorResponse.getUserFriendlyMessage()
            } catch (e: JsonSyntaxException) {
            }
            
            "Error $httpCode"
        } catch (e: Exception) {
            "Error $httpCode"
        }
    }
}
