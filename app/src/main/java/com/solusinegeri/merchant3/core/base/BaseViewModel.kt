package com.solusinegeri.merchant3.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel yang menyediakan fungsi-fungsi umum untuk semua ViewModel
 * Termasuk handling loading state, error handling, dan coroutine management
 */
abstract class BaseViewModel : ViewModel() {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }
    
    /**
     * Menjalankan coroutine dengan exception handling otomatis
     */
    protected fun launchCoroutine(
        showLoading: Boolean = true,
        block: suspend () -> Unit
    ) {
        viewModelScope.launch(exceptionHandler) {
            try {
                if (showLoading) {
                    setLoading(true)
                }
                block()
            } catch (e: Exception) {
                handleError(e)
            } finally {
                if (showLoading) {
                    setLoading(false)
                }
            }
        }
    }
    
    /**
     * Menjalankan coroutine tanpa loading state
     */
    protected fun launchCoroutineSilent(block: suspend () -> Unit) {
        launchCoroutine(showLoading = false, block = block)
    }
    
    /**
     * Set loading state
     */
    protected fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }
    
    /**
     * Set error message
     */
    protected fun setError(message: String?) {
        _errorMessage.value = message
    }
    
    /**
     * Set success message
     */
    protected fun setSuccess(message: String?) {
        _successMessage.value = message
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Clear success message
     */
    fun clearSuccess() {
        _successMessage.value = null
    }
    
    /**
     * Handle error secara otomatis
     */
    private fun handleError(throwable: Throwable) {
        setLoading(false)
        val errorMsg = throwable.message ?: "Terjadi kesalahan yang tidak diketahui"
        setError(errorMsg)
    }
    
    /**
     * Override untuk custom error handling
     */
    protected open fun onError(throwable: Throwable) {
        handleError(throwable)
    }
}
