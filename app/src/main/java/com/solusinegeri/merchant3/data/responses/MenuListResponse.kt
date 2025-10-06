package com.solusinegeri.merchant3.data.responses

/**
 * Response model untuk daftar menu merchant
 */
data class MenuListResponse(
    val data: List<MenuData>?,
    val message: String?,
    val status_code: String?,
    val type: String?
)

/**
 * Data model untuk menu item
 */
data class MenuData(
    val name: String?,
    val display: String?,
    val displayEn: String?,
    val imageUrl: String?
) {
    // Computed properties untuk backward compatibility
    val id: String? get() = name
    val icon: String? get() = name
    val isActive: Boolean get() = true // Semua menu dari response dianggap aktif
}
