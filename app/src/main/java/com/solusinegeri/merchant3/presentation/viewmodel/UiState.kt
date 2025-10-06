package com.solusinegeri.merchant3.presentation.viewmodel

/**
 * Base sealed class untuk UI state management
 * Bisa digunakan di semua ViewModel untuk konsistensi
 */
sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val message: String? = null) : UiState()
    data class Error(val message: String) : UiState()
}

/**
 * Generic UiState untuk data loading
 * Bisa digunakan untuk loading data dengan generic type
 */
sealed class DataUiState<out T> {
    object Idle : DataUiState<Nothing>()
    object Loading : DataUiState<Nothing>()
    data class Success<out T>(val data: T, val message: String? = null) : DataUiState<T>()
    data class Error(val message: String) : DataUiState<Nothing>()
}

/**
 * UiState untuk specific operations
 */
sealed class OperationUiState {
    object Idle : OperationUiState()
    object Loading : OperationUiState()
    data class Success(val message: String? = null) : OperationUiState()
    data class Error(val message: String) : OperationUiState()
}
