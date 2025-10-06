package com.solusinegeri.merchant3.data.model

data class ProfileMenuItem(
    val id: String,
    val title: String,
    val iconRes: Int,
    val onClick: () -> Unit
)