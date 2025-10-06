package com.solusinegeri.merchant3.data.responses

/**
 * Response untuk API getInitialCompany
 */
data class InitialCompanyResponse(
    val `data`: CompanyData? = null,
    val message: String? = null,
    val status_code: Int? = null,
    val type: String? = null
)

/**
 * Data company dari response
 */
data class CompanyData(
    val id: String? = null,
    val initial: String? = null,
    val name: String? = null,
    val companyLogo: String? = null,
    val enableMemberSelfRegister: Boolean? = null,
    val layout: String? = null,
    val color: CompanyColorData? = null,
)

/**
 * Data warna company
 * color1 = Primary Color
 * color2 = Secondary Color
 */
data class CompanyColorData(
    val color1: String? = null, // Primary Color
    val color2: String? = null // Secondary Color
)
