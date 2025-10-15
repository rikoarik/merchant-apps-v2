package com.solusinegeri.merchant3.core.network

import com.solusinegeri.merchant3.core.utils.ErrorParser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Helper untuk menyatukan pola pemanggilan API berbasis Retrofit.
 *
 * - Jalankan di dispatcher IO secara default.
 * - Tangani error standar dan parsing body error via [ErrorParser].
 * - Berikan hook ketika body `null` supaya caller bisa menyesuaikan pesan.
 */
suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    apiCall: suspend () -> Response<T>,
    onEmptyBody: (() -> Throwable)? = null,
    errorParser: (Response<T>) -> String = { ErrorParser.parseErrorResponse(it) }
): Result<T> {
    return withContext(dispatcher) {
        try {
            apiCall().toResult(onEmptyBody, errorParser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

fun <T> Response<T>.toResult(
    onEmptyBody: (() -> Throwable)? = null,
    errorParser: (Response<T>) -> String = { ErrorParser.parseErrorResponse(it) }
): Result<T> {
    return if (isSuccessful) {
        val body = body()
        if (body != null) {
            Result.success(body)
        } else {
            Result.failure(onEmptyBody?.invoke() ?: IllegalStateException("Response body is empty"))
        }
    } else {
        Result.failure(Exception(errorParser(this)))
    }
}
