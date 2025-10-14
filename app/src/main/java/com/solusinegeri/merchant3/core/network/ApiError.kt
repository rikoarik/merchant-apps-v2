package com.solusinegeri.merchant3.core.network

/**
 * Structured representation of an API error that can be safely reused across layers.
 *
 * @property message            User-friendly message that can be rendered in the UI.
 * @property statusCode         Optional HTTP status code returned by the backend.
 * @property type               Optional error type provided by the backend payload.
 * @property rawBody            Raw response body (if available) to assist with debugging.
 * @property isNetworkError     Flag indicating whether the error was caused by network issues.
 * @property requiresLogout     Flag indicating whether the error should force a logout/token clear.
 * @property cause              Root exception that triggered this error, preserved for logging.
 */
data class ApiError(
    val message: String,
    val statusCode: Int? = null,
    val type: String? = null,
    val rawBody: String? = null,
    val isNetworkError: Boolean = false,
    val requiresLogout: Boolean = false,
    val cause: Throwable? = null
)

/**
 * Exception wrapper used to surface [ApiError] instances through Kotlin's [Result] API.
 */
class ApiException(val error: ApiError) : Exception(error.message, error.cause)
