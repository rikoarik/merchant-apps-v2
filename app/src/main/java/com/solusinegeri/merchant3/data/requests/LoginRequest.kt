package com.solusinegeri.merchant3.data.requests

/**
 * Request model untuk login
 */
data class LoginRequest(
    val companyId: String,
    val username: String,
    val password: String
)
