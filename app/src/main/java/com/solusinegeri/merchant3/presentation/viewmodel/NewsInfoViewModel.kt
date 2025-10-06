package com.solusinegeri.merchant3.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.solusinegeri.merchant3.core.base.BaseViewModel
import com.solusinegeri.merchant3.data.repository.NewsInfoRepository
import com.solusinegeri.merchant3.data.responses.NewsListResponse

class NewsInfoViewModel : BaseViewModel() {

    private val newsRepository = NewsInfoRepository()

    // ===== News state =====
    private val _newsUiState = MutableLiveData<DataUiState<NewsListResponse>>()
    val newsUiState: LiveData<DataUiState<NewsListResponse>> = _newsUiState

    fun loadNewsData(
        page: Int? = null,
        size: Int? = null,
        sortBy: String? = null,
        dir: Int? = null
    ) {
        _newsUiState.value = DataUiState.Loading
        launchCoroutine(showLoading = false) {
            newsRepository.getNewsList(
                page = page,
                size = size,
                sortBy = sortBy,
                dir = dir
            ).onSuccess { newsResponse ->
                _newsUiState.value = DataUiState.Success(newsResponse, "Berita berhasil dimuat")
            }.onFailure { error ->
                _newsUiState.value = DataUiState.Error(error.message ?: "Gagal memuat berita")
                setError(error.message ?: "Gagal memuat berita")
            }
        }
    }
}