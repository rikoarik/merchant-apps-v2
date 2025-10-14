package com.solusinegeri.merchant3.core.base

import android.content.Context
import com.solusinegeri.merchant3.core.network.safeApiCall
import retrofit2.Response

/**
 * Simplified base repository that exposes a single `request` helper wrapping Retrofit calls
 * with the shared `safeApiCall` logic.
 */
abstract class BaseRepository {

    /**
     * Execute a Retrofit request safely and return the result wrapped in Kotlin `Result`.
     *
     * Provide a `Context` when you need automatic logging via `ApiErrorHandler`.
     */
    protected suspend fun <T> request(
        context: Context? = null,
        block: suspend () -> Response<T>
    ): Result<T> = safeApiCall(context, block)
}
