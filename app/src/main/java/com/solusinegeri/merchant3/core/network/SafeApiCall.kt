package com.solusinegeri.merchant3.core.network

import android.content.Context
import com.solusinegeri.merchant3.core.utils.ApiErrorHandler
import retrofit2.Response

/**
 * Execute a Retrofit call safely and convert the outcome into a Kotlin [Result].
 *
 * All non-successful responses are converted into [ApiException] so the caller can surface
 * the user-friendly message via [ApiException.error].
 */
suspend fun <T> safeApiCall(
    context: Context? = null,
    call: suspend () -> Response<T>
): Result<T> {
    return runCatching { call() }
        .fold(
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Result.success(body)
                    } else {
                        val error = ApiErrorHandler.resolveHttpError(
                            statusCode = response.code(),
                            errorBody = null,
                            defaultMessage = "Response body kosong",
                            context = context
                        )
                        Result.failure(ApiException(error))
                    }
                } else {
                    val rawBody = runCatching { response.errorBody()?.string() }.getOrNull()
                    val error = ApiErrorHandler.resolveHttpError(
                        statusCode = response.code(),
                        errorBody = rawBody,
                        defaultMessage = response.message(),
                        context = context
                    )
                    Result.failure(ApiException(error))
                }
            },
            onFailure = { throwable ->
                val error = ApiErrorHandler.resolve(throwable, context)
                Result.failure(ApiException(error))
            }
        )
}
