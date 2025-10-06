package com.solusinegeri.merchant3.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.solusinegeri.merchant3.core.base.BaseViewModel
import com.solusinegeri.merchant3.data.repository.AuthRepository

/**
 * AuthViewModel yang menggunakan BaseViewModel untuk konsistensi
 */
class AuthViewModel(private val authRepository: AuthRepository) : BaseViewModel() {
    
    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState
    
    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn
    
    init {
        checkLoginStatus()
    }
    
    fun login(companyId: String, username: String, password: String) {
        _uiState.value = UiState.Loading
        
        launchCoroutine(showLoading = false) {
            authRepository.login(companyId, username, password)
                .onSuccess { response ->
                    _uiState.value = UiState.Success("Login berhasil!")
                    _isLoggedIn.value = true
                    setSuccess("Login berhasil!")
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: "Login gagal")
                    setError(error.message ?: "Login gagal")
                }
        }
    }
    
    fun logout() {
        launchCoroutine(showLoading = false) {
            authRepository.logout()
            _isLoggedIn.value = false
            _uiState.value = UiState.Success("Logged out")
            setSuccess("Logout berhasil!")
        }
    }
    
    private fun checkLoginStatus() {
        launchCoroutineSilent {
            _isLoggedIn.value = authRepository.isLoggedIn()
        }
    }
    
    fun clearUiError() {
        _uiState.value = UiState.Idle
        clearError()
    }
}

