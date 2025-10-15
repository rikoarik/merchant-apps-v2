package com.solusinegeri.merchant3.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.solusinegeri.merchant3.core.base.BaseViewModel
import com.solusinegeri.merchant3.data.repository.AuthRepository

/**
 * AuthViewModel yang menggunakan BaseViewModel untuk konsistensi
 */
class AuthViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    // Gunakan uiState milik BaseViewModel (StateFlow<UiState>)
    // Jika butuh LiveData untuk UI lama:
    val uiStateLive: LiveData<UiState> = uiState.asLiveData()

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    init {
        checkLoginStatus()
    }

    fun login(companyId: String, username: String, password: String) {
        // Set loading via BaseViewModel
        setLoading(true)
        launchCoroutine(showLoading = false) {
            authRepository.login(companyId, username, password)
                .onSuccess {
                    setSuccess("Login berhasil!")
                    _isLoggedIn.value = true
                }
                .onFailure { error ->
                    setError(error.message ?: "Login gagal")
                }
        }
    }

    fun logout() {
        launchCoroutine(showLoading = true) {
            authRepository.logout()
            _isLoggedIn.value = false
            setSuccess("Logout berhasil!")
        }
    }

    private fun checkLoginStatus() {
        launchCoroutineSilent {
            _isLoggedIn.value = authRepository.isLoggedIn()
        }
    }

    fun clearUiError() {
        clearError()
    }
}
