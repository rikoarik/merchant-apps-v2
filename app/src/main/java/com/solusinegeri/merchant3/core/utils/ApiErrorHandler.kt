package com.solusinegeri.merchant3.core.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.solusinegeri.merchant3.core.network.ApiError
import com.solusinegeri.merchant3.core.network.ApiException
import com.solusinegeri.merchant3.core.security.SecurityLogger
import com.solusinegeri.merchant3.data.responses.ErrorDetail
import com.solusinegeri.merchant3.data.responses.ErrorResponse
import com.solusinegeri.merchant3.data.responses.ValidationErrorResponse
import com.solusinegeri.merchant3.data.responses.getUserFriendlyMessage
import retrofit2.HttpException
import java.io.IOException

/**
 * Centralised API error resolver that converts any [Throwable] or HTTP response payload into a
 * structured [ApiError]. This makes the error-handling flow easy to reuse for every network call.
 */
object ApiErrorHandler {

    private val gson = Gson()

    private val logoutKeywords = listOf(
        "not authenticated",
        "access_forbidden",
        "unauthorized",
        "forbidden",
        "token expired",
        "session expired"
    )

    /**
     * Convert any throwable into a user-facing error message.
     */
    fun handleErrorResponse(error: Throwable, context: Context? = null): String {
        return resolve(error, context).message
    }

    /**
     * Special handling for token refresh flow – forces a logout friendly message whenever the
     * backend indicates that the session is no longer valid.
     */
    fun handleTokenRefreshError(error: Throwable, context: Context? = null): String {
        val apiError = resolve(error, context)
        return if (apiError.requiresLogout || apiError.statusCode in listOf(401, 403)) {
            "Sesi Anda telah berakhir. Silakan login ulang."
        } else {
            apiError.message
        }
    }

    /**
     * Resolve a [Throwable] into [ApiError].
     */
    fun resolve(error: Throwable, context: Context? = null): ApiError {
        return when (error) {
            is ApiException -> error.error
            is HttpException -> {
                val rawBody = runCatching { error.response()?.errorBody()?.string() }.getOrNull()
                resolveHttpError(
                    statusCode = error.code(),
                    errorBody = rawBody,
                    defaultMessage = error.message(),
                    context = context,
                    cause = error
                )
            }
            is IOException -> {
                log(context, "Network error: ${error.message}")
                ApiError(
                    message = "Terjadi kesalahan jaringan. Periksa koneksi internet Anda.",
                    isNetworkError = true,
                    cause = error
                )
            }
            else -> {
                log(context, "Unknown error: ${error.message}")
                ApiError(
                    message = error.message ?: "Terjadi kesalahan yang tidak diketahui",
                    cause = error
                )
            }
        }
    }

    /**
     * Build [ApiError] from an HTTP error response.
     */
    fun resolveHttpError(
        statusCode: Int?,
        errorBody: String?,
        defaultMessage: String? = null,
        context: Context? = null,
        cause: Throwable? = null
    ): ApiError {
        log(context, "HTTP ${statusCode ?: "?"}: ${errorBody ?: "No error body"}")

        val parsedPayload = parseErrorBody(errorBody)
        val message = parsedPayload?.message
            ?.takeUnless { it.isBlank() }
            ?: defaultMessage?.takeUnless { it.isBlank() }
            ?: getHttpStatusMessage(statusCode)

        val requiresLogout = detectLogout(
            message = parsedPayload?.message,
            type = parsedPayload?.type,
            rawBody = errorBody,
            statusCode = statusCode
        )

        return ApiError(
            message = message,
            statusCode = statusCode,
            type = parsedPayload?.type,
            rawBody = errorBody,
            requiresLogout = requiresLogout,
            cause = cause
        )
    }

    /**
     * Determine whether the error requires the user to be logged out/force token refresh.
     */
    fun requiresLogout(error: Throwable): Boolean {
        return resolve(error).requiresLogout
    }

    /**
     * Determine whether a raw error payload requires logout.
     */
    fun requiresLogout(rawBody: String?, statusCode: Int?): Boolean {
        val parsedPayload = parseErrorBody(rawBody)
        return detectLogout(parsedPayload?.message, parsedPayload?.type, rawBody, statusCode)
    }

    // region Helpers

    private fun log(context: Context?, message: String) {
        context?.let {
            SecurityLogger.logSecurityEvent(it, "ApiErrorHandler", message)
        }
    }

    private data class ParsedPayload(
        val message: String?,
        val type: String?
    )

    private fun parseErrorBody(errorBody: String?): ParsedPayload? {
        if (errorBody.isNullOrBlank()) {
            return null
        }

        // Plain string payload
        if (!errorBody.trimStart().startsWith("{")) {
            return ParsedPayload(message = errorBody, type = null)
        }

        try {
            val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
            errorResponse?.let {
                val message = runCatching { it.getUserFriendlyMessage() }.getOrNull()
                val type = extractErrorType(it.detail)
                if (!message.isNullOrBlank() || !type.isNullOrBlank()) {
                    return ParsedPayload(message = message, type = type)
                }
            }
        } catch (ignored: JsonSyntaxException) {
            // Fall through to validation parsing
        } catch (ignored: IllegalStateException) {
            // Large or unexpected payloads
        }

        return try {
            val validationResponse = gson.fromJson(errorBody, ValidationErrorResponse::class.java)
            ParsedPayload(
                message = validationResponse.getUserFriendlyMessage(),
                type = "VALIDATION_ERROR"
            )
        } catch (ignored: Exception) {
            ParsedPayload(message = errorBody, type = null)
        }
    }

    private fun extractErrorType(detail: Any?): String? {
        return when (detail) {
            is ErrorDetail -> detail.type
            is Map<*, *> -> detail["type"] as? String
            is String -> detail.uppercase()
            else -> null
        }
    }

    private fun detectLogout(
        message: String?,
        type: String?,
        rawBody: String?,
        statusCode: Int?
    ): Boolean {
        if (statusCode == 401 || statusCode == 403) {
            return true
        }

        val haystack = buildString {
            if (!message.isNullOrBlank()) append(message.lowercase())
            if (!type.isNullOrBlank()) append(" ").append(type.lowercase())
            if (!rawBody.isNullOrBlank()) append(" ").append(rawBody.lowercase())
        }

        return logoutKeywords.any { haystack.contains(it) }
    }

    private fun getHttpStatusMessage(httpCode: Int?): String {
        return when (httpCode) {
            400 -> "Data yang dimasukkan tidak valid. Silakan periksa kembali."
            401 -> "Anda belum login. Silakan login terlebih dahulu."
            403 -> "Akses ditolak. Silakan login ulang."
            404 -> "Data tidak ditemukan. Silakan periksa kembali."
            409 -> "Data sudah ada. Silakan gunakan data yang berbeda."
            422 -> "Data tidak valid. Silakan periksa kembali input Anda."
            429 -> "Terlalu banyak permintaan. Silakan tunggu sebentar."
            500 -> "Terjadi kesalahan pada server. Silakan coba lagi nanti."
            502 -> "Server sedang dalam maintenance. Silakan coba lagi nanti."
            503 -> "Service tidak tersedia. Silakan coba lagi nanti."
            else -> httpCode?.let { "Terjadi kesalahan: $it" } ?: "Terjadi kesalahan yang tidak diketahui"
        }
    }

    // endregion
}
