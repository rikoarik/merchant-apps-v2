package com.solusinegeri.merchant3.data.repository

import com.solusinegeri.merchant3.core.network.safeApiCall
import com.solusinegeri.merchant3.data.network.NetworkClient
import com.solusinegeri.merchant3.data.network.NewsApi
import com.solusinegeri.merchant3.data.responses.NewsDetailResponse
import com.solusinegeri.merchant3.data.responses.NewsListResponse

class NewsInfoRepository(
    private val newsApi: NewsApi = NetworkClient.createService(NewsApi::class.java)
) {

    suspend fun getNewsList(
        page: Int? = null,
        size: Int? = null,
        sortBy: String? = null,
        dir: Int? = null
    ): Result<NewsListResponse> =
        safeApiCall(
            apiCall = {
                newsApi.getMerchantNews(
                    page = page,
                    size = size,
                    sortBy = sortBy,
                    dir = dir
                )
            },
            onEmptyBody = { IllegalStateException("Data berita tidak ditemukan") }
        )

    suspend fun getNewsDetail(id: String): Result<NewsDetailResponse> =
        safeApiCall(
            apiCall = { newsApi.getInfoMerchantById(id) },
            onEmptyBody = { IllegalStateException("Data berita tidak ditemukan") }
        )
}
