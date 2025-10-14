package com.solusinegeri.merchant3.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.solusinegeri.merchant3.core.base.BaseViewModel
import com.solusinegeri.merchant3.core.utils.toUserMessage
import com.solusinegeri.merchant3.data.repository.NewsInfoRepository
import com.solusinegeri.merchant3.data.responses.NewsDetailResponse
import com.solusinegeri.merchant3.data.responses.NewsListResponse

class NewsInfoViewModel : BaseViewModel() {

    private val newsRepository = NewsInfoRepository()

    // ===== News state =====
    private val _newsUiState = MutableLiveData<DataUiState<NewsListResponse>>()
    val newsUiState: LiveData<DataUiState<NewsListResponse>> = _newsUiState

    private val _newsDetailUiState = MutableLiveData<DataUiState<NewsDetailResponse>>()
    val newsDetailUiState: LiveData<DataUiState<NewsDetailResponse>> = _newsDetailUiState

    fun loadNewsData(
        page: Int? = null,
        size: Int? = null,
        sortBy: String? = null,
        dir: Int? = null
    ) {
        _newsUiState.value = DataUiState.Loading
        launchIO(showLoading = false) {
            newsRepository.getNewsList(
                page = page,
                size = size,
                sortBy = sortBy,
                dir = dir
            ).onSuccess { newsResponse ->
                _newsUiState.value = DataUiState.Success(newsResponse, "Berita berhasil dimuat")
            }.onFailure { error ->
                val message = error.toUserMessage()
                _newsUiState.value = DataUiState.Error(message)
                setError(message)
            }
        }
    }

    fun loadNewsDetail(id: String) {
        _newsDetailUiState.value = DataUiState.Loading
        launchIO(showLoading = false) {
            newsRepository.getNewsDetail(id)
                .onSuccess { newsDetailResponse ->
                _newsDetailUiState.value = DataUiState.Success(newsDetailResponse, "Berita berhasil dimuat")
            }.onFailure { error ->
                val message = error.toUserMessage()
                _newsDetailUiState.value = DataUiState.Error(message)
                setError(message)
            }
        }
    }
}
