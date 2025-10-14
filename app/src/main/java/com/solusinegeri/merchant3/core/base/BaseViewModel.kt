package com.solusinegeri.merchant3.core.base

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel dengan utilitas umum:
 * - Manajemen loading / error / success lewat StateFlow
 * - Helper `launchCoroutine` & `launchIO`
 * - Penyaluran event satu-kali lewat SharedFlow
 */
abstract class BaseViewModel : ViewModel() {

    /**
     * Representasi state generik agar UI bisa cukup observe satu flow saja jika mau.
     */
    sealed interface UiState {
        object Idle : UiState
        object Loading : UiState
        data class Error(val message: String) : UiState
        data class Success(val message: String) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        emitError(throwable)
    }

    /**
     * Jalankan coroutine dengan error & loading handling otomatis.
     */
    protected fun launchCoroutine(
        showLoading: Boolean = true,
        dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
        block: suspend CoroutineScope.() -> Unit
    ): Job = viewModelScope.launch(dispatcher + exceptionHandler) {
        try {
            if (showLoading) setLoading(true)
            block()
        } catch (throwable: Throwable) {
            emitError(throwable)
        } finally {
            if (showLoading) setLoading(false)
        }
    }

    /**
     * Shortcut untuk pekerjaan IO-heavy.
     */
    protected fun launchIO(
        showLoading: Boolean = true,
        block: suspend CoroutineScope.() -> Unit
    ): Job = launchCoroutine(showLoading = showLoading, dispatcher = Dispatchers.IO, block = block)

    /**
     * Versi lama (tanpa loading). Tetap dipertahankan untuk kompatibilitas.
     */
    protected fun launchCoroutineSilent(block: suspend () -> Unit): Job =
        launchCoroutine(showLoading = false) { block() }

    /**
     * Emit event satu kali (misal navigasi, snackbar khusus).
     */
    protected fun sendEvent(event: UiEvent) {
        viewModelScope.launch { _events.emit(event) }
    }

    protected fun setLoading(loading: Boolean) {
        _isLoading.value = loading
        _uiState.value = if (loading) UiState.Loading else UiState.Idle
    }

    protected fun setError(message: String?) {
        _errorMessage.value = message
        message?.let { _uiState.value = UiState.Error(it) }
    }

    protected fun setSuccess(message: String?) {
        _successMessage.value = message
        message?.let { _uiState.value = UiState.Success(it) }
    }

    fun clearError() {
        _errorMessage.value = null
        if (!_isLoading.value) _uiState.value = UiState.Idle
    }

    fun clearSuccess() {
        _successMessage.value = null
        if (!_isLoading.value && _errorMessage.value == null) {
            _uiState.value = UiState.Idle
        }
    }

    private fun emitError(throwable: Throwable) {
        val message = formatError(throwable)
        setLoading(false)
        setError(message)
        onError(throwable, message)
    }

    /**
     * Override untuk menyesuaikan pesan error global.
     */
    protected open fun formatError(throwable: Throwable): String =
        throwable.message ?: "Terjadi kesalahan yang tidak diketahui"

    /**
     * Dipanggil setiap ada error setelah [formatError].
     */
    protected open fun onError(throwable: Throwable, message: String) = Unit

    /**
     * Representasi event satu-kali.
     */
    sealed interface UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent
        data class Navigate(val destination: String, val args: Bundle? = null) : UiEvent
        data class ShowDialog(val tag: String, val args: Bundle? = null) : UiEvent
        object DismissDialog : UiEvent
    }
}
