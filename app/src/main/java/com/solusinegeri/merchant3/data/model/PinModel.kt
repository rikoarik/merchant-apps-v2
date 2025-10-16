package com.solusinegeri.merchant3.data.model


/**
 * Model untuk request ganti PIN
 */
data class ChangePinRequest(
    val newSecurityCode: String,
    val oldSecurityCode: String
)

/**
 * Model untuk request OTP lupa PIN
 */
data class ForgotPinOtpRequest(
    val companyId: String,
    val destinationOtp: String,
    val name: String,
    val otpType: String,
    val userType: String
)

/**
 * Model untuk request reset PIN dengan OTP
 */
data class ResetPinRequest(
    val otp: String,
    val securityCode: String
)

/**
 * Model untuk response OTP
 */
data class OtpResponse(
    val status: Boolean,
    val message: String,
    val data: OtpData?
)

data class OtpData(
    val otpSent: Boolean,
    val expiresAt: String?
)

/**
 * Model untuk response umum
 */
data class PinResponse(
    val status: Boolean,
    val message: String
)