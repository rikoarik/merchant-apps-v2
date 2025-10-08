package com.solusinegeri.merchant3.data.model

data class ProfileMenuItem(
    val id: String,
    val title: String,
    val iconRes: Int,
    val onClick: () -> Unit
)

data class ProfileEditItem(
    val id      : String,
    val title   : String,
    var content : String,
    val editable: Boolean
)