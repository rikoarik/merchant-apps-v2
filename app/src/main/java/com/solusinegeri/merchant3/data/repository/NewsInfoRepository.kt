package com.solusinegeri.merchant3.data.repository

import com.solusinegeri.merchant3.data.network.NetworkClient
import com.solusinegeri.merchant3.data.network.NewsApi
import com.solusinegeri.merchant3.data.responses.NewsDetailResponse
import com.solusinegeri.merchant3.data.responses.NewsListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewsInfoRepository {

    private val newsApi: NewsApi by lazy {
        NetworkClient.createService(NewsApi::class.java)
    }

    /**
     * Ambil daftar news merchant dengan optional paging/sort.
     * @param page   Halaman (0-based) – opsional
     * @param size   Jumlah item per halaman – opsional
     * @param sortBy Field untuk sorting, contoh: "createdAt", "title" – opsional
     * @param dir    Arah sort: 1 (ASC) atau -1 (DESC) – opsional
     */
    suspend fun getNewsList(
        page: Int? = null,
        size: Int? = null,
        sortBy: String? = null,
        dir: Int? = null
    ): Result<NewsListResponse> = withContext(Dispatchers.IO) {
        try {
            val response = newsApi.getMerchantNews(
                page = page,
                size = size,
                sortBy = sortBy,
                dir = dir
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Data berita tidak ditemukan"))
                }
            } else {
                Result.failure(Exception("Gagal memuat berita: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error memuat berita: ${e.message}"))
        }
    }

    suspend fun getNewsDetail(id: String): Result<NewsDetailResponse> = withContext(Dispatchers.IO) {
        try {
            val response = newsApi.getInfoMerchantById(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Data berita tidak ditemukan"))
                }
            } else {
                Result.failure(Exception("Gagal memuat berita: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error memuat berita: ${e.message}"))
        }
    }
}
