package com.solusinegeri.merchant3.core.utils

import com.solusinegeri.merchant3.core.network.ApiException

fun Throwable.toUserMessage(): String =
    when (this) {
        is ApiException -> this.error.message
        else -> this.message ?: "Terjadi kesalahan yang tidak diketahui"
    }
