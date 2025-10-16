package com.solusinegeri.merchant3.data.responses

import com.solusinegeri.merchant3.data.model.UserData

/**
 * response model untuk user profile
 */
data class UserProfileResponse(
    val data       : UserData?,
    val message    : String?,
    val status_code: Int?,
    val type       : String?
)

/**
 * response model untuk user profile picture
 */
data class ProfileImageResponse(
    val data       : UserData?,
    val message    : String?,
    val status_code: Int?,
    val type       : String?
)
