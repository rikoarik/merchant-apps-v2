package com.solusinegeri.merchant3.data.network

import com.solusinegeri.merchant3.data.responses.NewsListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * News API interface untuk data berita merchant
 */
interface NewsApi {

    @GET("/info/merchant/news/get_news")
    suspend fun getMerchantNews(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("dir") dir: Int? = null
    ): Response<NewsListResponse>
}
