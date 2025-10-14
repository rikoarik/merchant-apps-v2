package com.solusinegeri.merchant3.data.responses

import com.google.gson.annotations.SerializedName

/**
 * Data model untuk error response dari backend
 * Format: {"detail":{"type":"COMPANY_NOT_FOUND","message":"Company not found","status_code":404,"error":"NOT_FOUND"}}
 * atau format sederhana: {"detail":"Not authenticated"}
 */
data class ErrorResponse(
    @SerializedName("detail")
    val detail: Any // Bisa ErrorDetail atau String
)

data class ErrorDetail(
    @SerializedName("type")
    val type: String,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("status_code")
    val statusCode: Int,
    
    @SerializedName("error")
    val error: String
)

/**
 * Data model untuk validation error response dari backend
 * Format: {"errorCodes":[],"errors":{"companyId":["MustNotBlank"]}}
 */
data class ValidationErrorResponse(
    @SerializedName("errorCodes")
    val errorCodes: List<String> = emptyList(),
    
    @SerializedName("errors")
    val errors: Map<String, List<String>> = emptyMap()
)

/**
 * Extension function untuk mendapatkan user-friendly error message dari validation errors
 * Prioritas: message dari backend -> error code -> value asli
 */
fun ValidationErrorResponse.getUserFriendlyMessage(): String {
    val errorMessages = mutableListOf<String>()
    
    errors.forEach { (field, messages) ->
        messages.forEach { message ->
            errorMessages.add("$field: $message")
        }
    }
    
    return if (errorMessages.isNotEmpty()) {
        errorMessages.joinToString(", ")
    } else {
        "Terjadi kesalahan validasi data"
    }
}

/**
 * Extension function untuk mendapatkan user-friendly error message dari ErrorResponse
 * Mendukung format: {"detail":"Not authenticated"} dan {"detail":{...}}
 */
fun ErrorResponse.getUserFriendlyMessage(): String {
    return when (detail) {
        is String -> {
            // Format sederhana: {"detail":"Not authenticated"}
            getUserFriendlyMessageFromString(detail)
        }
        is Map<*, *> -> {
            // Format kompleks: {"detail":{"type":"...","message":"..."}}
            try {
                val errorDetail = ErrorDetail(
                    type = detail["type"] as? String ?: "",
                    message = detail["message"] as? String ?: "",
                    statusCode = (detail["status_code"] as? Number)?.toInt() ?: 0,
                    error = detail["error"] as? String ?: ""
                )
                errorDetail.getUserFriendlyMessage()
            } catch (e: Exception) {
                getUserFriendlyMessageFromString(detail.toString())
            }
        }
        else -> {
            getUserFriendlyMessageFromString(detail.toString())
        }
    }
}

/**
 * Extension function untuk mendapatkan user-friendly error message
 * Priority: message -> error -> type
 */
fun ErrorDetail.getUserFriendlyMessage(): String {
    if (message.isNotEmpty()) {
        return message
    }
    
    if (error.isNotEmpty()) {
        return error
    }
    return when (type) {
        "COMPANY_NOT_FOUND" -> "Kode instansi tidak ditemukan. Silakan periksa kembali kode yang Anda masukkan."
        "INVALID_INPUT" -> "Format kode instansi tidak valid. Silakan masukkan kode yang benar."
        "NETWORK_ERROR" -> "Terjadi kesalahan jaringan. Periksa koneksi internet Anda."
        "SERVER_ERROR" -> "Terjadi kesalahan pada server. Silakan coba lagi nanti."
        "UNAUTHORIZED" -> "Anda tidak memiliki akses untuk mengakses data ini."
        "FORBIDDEN" -> "Akses ditolak. Silakan hubungi administrator."
        "VALIDATION_ERROR" -> "Data yang dimasukkan tidak valid. Silakan periksa kembali."
        "TIMEOUT" -> "Permintaan timeout. Silakan coba lagi."
        "RATE_LIMIT_EXCEEDED" -> "Terlalu banyak permintaan. Silakan tunggu sebentar."
        "ACCESS_FORBIDDEN" -> "Akses ditolak. Silakan login ulang."
        "NOT_AUTHENTICATED" -> "Anda belum login. Silakan login terlebih dahulu."
        else -> type.ifEmpty { "Terjadi kesalahan yang tidak diketahui" }
    }
}

/**
 * Extension function untuk mendapatkan error type description
 */
fun ErrorDetail.getErrorTypeDescription(): String {
    return when (type) {
        "COMPANY_NOT_FOUND" -> "Instansi Tidak Ditemukan"
        "INVALID_INPUT" -> "Input Tidak Valid"
        "NETWORK_ERROR" -> "Kesalahan Jaringan"
        "SERVER_ERROR" -> "Kesalahan Server"
        "UNAUTHORIZED" -> "Tidak Diizinkan"
        "FORBIDDEN" -> "Akses Ditolak"
        "VALIDATION_ERROR" -> "Kesalahan Validasi"
        "TIMEOUT" -> "Timeout"
        "RATE_LIMIT_EXCEEDED" -> "Terlalu Banyak Permintaan"
        else -> "Kesalahan Tidak Diketahui"
    }
}

/**
 * Helper function untuk mendapatkan user-friendly message dari string error
 */
private fun getUserFriendlyMessageFromString(errorString: String): String {
    return when {
        errorString.contains("Not authenticated", ignoreCase = true) -> 
            "Anda belum login. Silakan login terlebih dahulu."
        errorString.contains("ACCESS_FORBIDDEN", ignoreCase = true) -> 
            "Akses ditolak. Silakan login ulang."
        errorString.contains("Unauthorized", ignoreCase = true) -> 
            "Username atau password salah. Silakan coba lagi."
        errorString.contains("Forbidden", ignoreCase = true) -> 
            "Akses ditolak. Silakan hubungi administrator."
        errorString.contains("Not found", ignoreCase = true) -> 
            "Data tidak ditemukan. Silakan periksa kembali."
        errorString.contains("Invalid", ignoreCase = true) -> 
            "Data yang dimasukkan tidak valid. Silakan periksa kembali."
        errorString.contains("Timeout", ignoreCase = true) -> 
            "Permintaan timeout. Silakan coba lagi."
        errorString.contains("Server error", ignoreCase = true) -> 
            "Terjadi kesalahan pada server. Silakan coba lagi nanti."
        errorString.isNotEmpty() -> errorString
        else -> "Terjadi kesalahan yang tidak diketahui"
    }
}
