package com.solusinegeri.merchant3.data.model

/**
 * Data model untuk user profile
 */
data class UserData(
    val _id         : String?,
    val nik         : String?,
    val noId        : String?,
    val name        : String?,
    val isWa        : Boolean?,
    val note        : String?,
    val lang        : String?,
    val city        : String?,
    val email       : String?,
    val phone       : String?,
    val gender      : String?,
    val village     : String?,
    val address     : String?,
    val tipeNik     : String?,
    val district    : String?,
    val username    : String?,
    val province    : String?,
    val companyId   : String?,
    val dateOfBirth : String?,
    val placeOfBirth: String?,
    val profileImage: String?
)