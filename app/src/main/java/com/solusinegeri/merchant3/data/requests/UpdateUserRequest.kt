package com.solusinegeri.merchant3.data.requests

data class UpdateUserRequest(
    var city        : String = "",
    var lang        : String = "",
    var name        : String = "",
    var isWa        : Boolean = false,
    var phone       : String = "",
    var email       : String = "",
    var gender      : String = "",
    var village     : String = "",
    var address     : String = "",
    var province    : String = "",
    var district    : String = "",
    var dateOfBirth : String = "",
    var placeOfBirth: String = ""
)
