package com.solusinegeri.merchant3.core.domain

/**
 * Simple Result wrapper
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val exception: Throwable) : Result<Nothing>()
}
