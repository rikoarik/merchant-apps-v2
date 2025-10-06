package com.solusinegeri.merchant3.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.solusinegeri.merchant3.core.base.BaseViewModel
import com.solusinegeri.merchant3.data.repository.BalanceRepository
import com.solusinegeri.merchant3.data.repository.MenuRepository
import com.solusinegeri.merchant3.data.repository.NewsInfoRepository
import com.solusinegeri.merchant3.data.responses.BalanceData
import com.solusinegeri.merchant3.data.responses.MenuData
import com.solusinegeri.merchant3.data.responses.NewsListResponse

class HomeViewModel : BaseViewModel() {

    private val menuRepository = MenuRepository()
    private val balanceRepository = BalanceRepository()
    private val newsRepository = NewsInfoRepository()

    private val _menuList = MutableLiveData<List<MenuData>>()
    val menuList: LiveData<List<MenuData>> = _menuList

    private val _balanceData = MutableLiveData<BalanceData?>()
    val balanceData: LiveData<BalanceData?> = _balanceData

    private val _menuUiState = MutableLiveData<DataUiState<List<MenuData>>>()
    val menuUiState: LiveData<DataUiState<List<MenuData>>> = _menuUiState

    private val _balanceUiState = MutableLiveData<DataUiState<BalanceData>>()
    val balanceUiState: LiveData<DataUiState<BalanceData>> = _balanceUiState

    // ===== News state =====
    private val _newsUiState = MutableLiveData<DataUiState<NewsListResponse>>()
    val newsUiState: LiveData<DataUiState<NewsListResponse>> = _newsUiState

    fun loadMenuData() {
        _menuUiState.value = DataUiState.Loading
        launchCoroutine(showLoading = false) {
            menuRepository.getActiveMenus()
                .onSuccess { activeMenus ->
                    _menuList.value = activeMenus
                    _menuUiState.value = DataUiState.Success(activeMenus, "Menu berhasil dimuat")
                }
                .onFailure { error ->
                    _menuUiState.value = DataUiState.Error(error.message ?: "Gagal memuat menu")
                    setError(error.message ?: "Gagal memuat menu")
                }
        }
    }

    fun loadBalanceData(balanceCode: String) {
        _balanceUiState.value = DataUiState.Loading
        launchCoroutine(showLoading = false) {
            balanceRepository.getBalance(balanceCode)
                .onSuccess { balanceData ->
                    _balanceData.value = balanceData
                    _balanceUiState.value = DataUiState.Success(balanceData, "Balance berhasil dimuat")
                }
                .onFailure { error ->
                    _balanceUiState.value = DataUiState.Error(error.message ?: "Gagal memuat balance")
                    _balanceData.value = null
                    setError(error.message ?: "Gagal memuat balance")
                }
        }
    }

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

    fun refreshData(balanceCode: String) {
        loadMenuData()
        loadBalanceData(balanceCode)
        loadNewsData(page = 1, size = 10, sortBy = "createdAt", dir = -1)
    }

    fun clearMenuError() {
        _menuUiState.value = DataUiState.Idle
    }

    fun clearBalanceError() {
        _balanceUiState.value = DataUiState.Idle
    }

    fun clearNewsError() {
        _newsUiState.value = DataUiState.Idle
    }
}
