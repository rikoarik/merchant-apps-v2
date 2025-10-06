package com.solusinegeri.merchant3.data.responses

/**
 * Response model untuk daftar berita merchant (sesuai JSON payload)
 */
data class NewsListResponse(
    val data: List<NewsData>?,
    val total: Int? = null,
    val page: Int? = null,
    val size: Int? = null,
    val sortBy: String? = null,
    val dir: Int? = null,
    val message: String? = null,
    val status_code: String? = null,
    val type: String? = null
)

/**
 * Data model untuk setiap item berita
 */
data class NewsData(
    val _id: String? = null,
    val title: String? = null,
    val subTitle: String? = null,
    val description: String? = null,
    val imageUrl: List<String>? = null,
    val createdTime: String? = null,
    val updatedTime: String? = null,
    val isPublish: Boolean? = null,
    val isHeadLine: Boolean? = null,
    val isDelete: Boolean? = null,
    val tags: List<String>? = null,
    val userType: List<String>? = null,
    val companyId: String? = null,
    val subCompanyId: String? = null,
    val companyName: String? = null,
    val createdBy: String? = null,
    val updatedBy: String? = null,
    val creatorName: String? = null
)
