package com.solusinegeri.merchant3.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.solusinegeri.merchant3.core.base.BaseViewModel
import com.solusinegeri.merchant3.data.repository.AnalyticsRepository
import com.solusinegeri.merchant3.data.responses.DetailTransactionResponse
import com.solusinegeri.merchant3.data.responses.SummaryAnalyticsResponse
import com.solusinegeri.merchant3.data.responses.TransactionAnalyticsResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AnalyticsViewModel : BaseViewModel() {

    private val analyticsRepository: AnalyticsRepository by lazy { AnalyticsRepository() }

    private val _summaryUiState = MutableLiveData<DataUiState<SummaryAnalyticsResponse>>(DataUiState.Idle)
    val summaryUiState: LiveData<DataUiState<SummaryAnalyticsResponse>> get() = _summaryUiState

    private val _analyticsUiState = MutableLiveData<DataUiState<TransactionAnalyticsResponse>>(DataUiState.Idle)
    val analyticsUiState: LiveData<DataUiState<TransactionAnalyticsResponse>> get() = _analyticsUiState

    private val _detailTransactionsUiState = MutableLiveData<DataUiState<DetailTransactionResponse>>(DataUiState.Idle)
    val detailTransactionsUiState: LiveData<DataUiState<DetailTransactionResponse>> get() = _detailTransactionsUiState

    fun loadTransactionSummary(startDate: String, endDate: String, balanceCode: String) {
        _summaryUiState.value = DataUiState.Loading
        viewModelScope.launch {
            analyticsRepository.getTransactionSummary(startDate, endDate, balanceCode)
                .onSuccess { _summaryUiState.value = DataUiState.Success(it) }
                .onFailure { _summaryUiState.value = DataUiState.Error(it.message ?: "Data ringkasan tidak ditemukan") }
        }
    }

    fun loadTransactionAnalytics(startDate: String, endDate: String, balanceCode: String) {
        _analyticsUiState.value = DataUiState.Loading
        viewModelScope.launch {
            analyticsRepository.getTransactionAnalytics(startDate, endDate, balanceCode)
                .onSuccess { _analyticsUiState.value = DataUiState.Success(it) }
                .onFailure { _analyticsUiState.value = DataUiState.Error(it.message ?: "Data analitik tidak ditemukan") }
        }
    }

    fun loadDetailTransactions(startDate: String, endDate: String) {
        _detailTransactionsUiState.value = DataUiState.Loading
        viewModelScope.launch {
            analyticsRepository.getHistoryTransactionsAnalytics(startDate, endDate)
                .onSuccess { _detailTransactionsUiState.value = DataUiState.Success(it) }
                .onFailure { _detailTransactionsUiState.value = DataUiState.Error(it.message ?: "Data detail transaksi tidak ditemukan") }
        }
    }

    fun loadAllAnalytics(
        startDate: String, 
        endDate: String, 
        balanceCode: String,
        detailStartDate: String,
        detailEndDate: String
    ) {
        viewModelScope.launch {
            // Load summary, analytics, and detail transactions concurrently
            val summaryDeferred = async { 
                analyticsRepository.getTransactionSummary(startDate, endDate, balanceCode) 
            }
            val analyticsDeferred = async { 
                analyticsRepository.getTransactionAnalytics(startDate, endDate, balanceCode) 
            }
            val detailDeferred = async {
                analyticsRepository.getHistoryTransactionsAnalytics(detailStartDate, detailEndDate)
            }
            
            // Wait for all to complete
            val summaryResult = summaryDeferred.await()
            val analyticsResult = analyticsDeferred.await()
            val detailResult = detailDeferred.await()
            
            // Handle results
            summaryResult.fold(
                onSuccess = { _summaryUiState.value = DataUiState.Success(it) },
                onFailure = { _summaryUiState.value = DataUiState.Error(it.message ?: "Data ringkasan tidak ditemukan") }
            )
            
            analyticsResult.fold(
                onSuccess = { _analyticsUiState.value = DataUiState.Success(it) },
                onFailure = { _analyticsUiState.value = DataUiState.Error(it.message ?: "Data analitik tidak ditemukan") }
            )
            
            detailResult.fold(
                onSuccess = { _detailTransactionsUiState.value = DataUiState.Success(it) },
                onFailure = { _detailTransactionsUiState.value = DataUiState.Error(it.message ?: "Data detail transaksi tidak ditemukan") }
            )
        }
    }

}