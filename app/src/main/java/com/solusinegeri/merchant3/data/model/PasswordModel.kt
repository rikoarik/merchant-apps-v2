package com.solusinegeri.merchant3.data.model

data class PasswordEditModel (
    val oldPassword: String,
    val password: String,
    val confirmPassword: String
)
data class StrengthPasswordResponse(
    val data: String,
    val message: String,
    val status_code: Int,
    val type: String
)