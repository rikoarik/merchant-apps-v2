package com.solusinegeri.merchant3.data.responses

import com.google.gson.annotations.SerializedName

/**
 * Data model untuk error response dari backend
 * Format: {"detail":{"type":"COMPANY_NOT_FOUND","message":"Company not found","status_code":404,"error":"NOT_FOUND"}}
 */
data class ErrorResponse(
    @SerializedName("detail")
    val detail: ErrorDetail
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
