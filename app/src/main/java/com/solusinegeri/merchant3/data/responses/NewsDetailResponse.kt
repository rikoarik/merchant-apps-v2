package com.solusinegeri.merchant3.data.responses

data class NewsDetailResponse (
    val _id: String,
    val companyId: String,
    val createdBy: String,
    val createdTime: String,
    val description: String,
    val imageUrl: List<String>,
    val isDelete: Boolean,
    val isHeadLine: Boolean,
    val isPublish: Boolean,
    val subTitle: String,
    val tags: List<Any>,
    val title: String,
    val updatedBy: Any,
    val updatedTime: String,
    val creatorName: String,
    val userType: List<String>
)