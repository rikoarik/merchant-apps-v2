package com.solusinegeri.merchant3.core.domain

/**
 * Simple error handling
 */
object ErrorHandler {
    fun getErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is java.net.UnknownHostException -> "No internet connection"
            is java.net.SocketTimeoutException -> "Request timeout"
            is java.io.IOException -> "Network error"
            is retrofit2.HttpException -> "Server error: ${throwable.code()}"
            else -> throwable.message ?: "Unknown error"
        }
    }
}
