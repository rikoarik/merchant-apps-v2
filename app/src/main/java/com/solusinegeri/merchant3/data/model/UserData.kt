package com.solusinegeri.merchant3.data.model

/**
 * Data model untuk user profile
 */
data class UserData(
    val id: String?,
    val name: String?,
    val email: String?,
    val phone: String?,
    val avatar: String?,
    val address: String?,
    val city: String?,
    val province: String?,
    val postalCode: String?,
    val dateOfBirth: String?,
    val gender: String?,
    val isVerified: Boolean?,
    val createdAt: String?,
    val updatedAt: String?
)