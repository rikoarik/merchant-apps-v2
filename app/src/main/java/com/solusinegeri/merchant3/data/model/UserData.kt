package com.solusinegeri.merchant3.data.model

/**
 * Data model untuk user profile
 */
//data class UserData(
//    val _id         : String?,
//    val name        : String?,
//    val email       : String?,
//    val phone       : String?,
//    val avatar      : String?,
//    val address     : String?,
//    val city        : String?,
//    val province    : String?,
//    val postalCode  : String?,
//    val dateOfBirth : String?,
//    val gender      : String?,
//    val isVerified  : Boolean?,
//    val createdAt   : String?,
//    val updatedAt   : String?,
//    val placeOfBirth: String?,
//)

data class UpdateUserModel(
    var city: String = "",
    var isWa: Boolean = false,
    var lang: String = "",
    var name: String = "",
    var phone: String = "",
    var gender: String = "",
    var village: String = "",
    var address: String = "",
    var province: String = "",
    var district: String = "",
    var dateOfBirth: String = "",
    var placeOfBirth: String = ""
)

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

/**
 * Data model untuk user response
 */
data class UserResponse(
    val data       : UserData?,
    val message    : String?,
    val status_code: Int?,
    val type       : String?
)