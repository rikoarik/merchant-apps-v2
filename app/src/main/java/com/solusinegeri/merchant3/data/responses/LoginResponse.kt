package com.solusinegeri.merchant3.data.responses

/**
 * Response model untuk login
 */
data class LoginResponse(
    val data: LoginData?,
    val message: String?,
    val status_code: String?,
    val type: String?
)

/**
 * Data model untuk login response
 */
data class LoginData(
    val authToken: String?,
    val companyId: String?,
    val email: String?,
    val name: String?,
    val userId: String?
)
