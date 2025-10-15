package com.solusinegeri.merchant3.core.utils

data class ErrorDisplay(
        val message: String,
        val title: String? = null,
        val isSnackBar: Boolean = false
    )