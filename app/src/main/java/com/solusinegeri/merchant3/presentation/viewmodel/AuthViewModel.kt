package com.solusinegeri.merchant3.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.solusinegeri.merchant3.core.base.BaseViewModel
import com.solusinegeri.merchant3.core.base.BaseViewModel.UiEvent
import com.solusinegeri.merchant3.core.utils.toUserMessage
import com.solusinegeri.merchant3.data.repository.AuthRepository

/**
 * AuthViewModel yang menggunakan BaseViewModel untuk konsistensi
 */
class AuthViewModel(private val authRepository: AuthRepository) : BaseViewModel() {

    private val _legacyUiState = MutableLiveData<UiState>()
    val legacyUiState: LiveData<UiState> = _legacyUiState

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    init {
        checkLoginStatus()
    }

    fun login(companyId: String, username: String, password: String) {
        _legacyUiState.value = UiState.Loading

        launchIO(showLoading = false) {
            authRepository.login(companyId, username, password)
                .onSuccess {
                    _legacyUiState.postValue(UiState.Success("Login berhasil!"))
                    _isLoggedIn.postValue(true)
                    setSuccess("Login berhasil!")
                    sendEvent(UiEvent.ShowSnackbar("Login berhasil!"))
                }
                .onFailure { error ->
                    val message = error.toUserMessage()
                    _legacyUiState.postValue(UiState.Error(message))
                    setError(message)
                }
        }
    }

    fun logout() {
        launchIO(showLoading = false) {
            authRepository.logout()
            _isLoggedIn.postValue(false)
            _legacyUiState.postValue(UiState.Success("Logged out"))
            setSuccess("Logout berhasil!")
        }
    }

    private fun checkLoginStatus() {
        launchCoroutineSilent {
            _isLoggedIn.postValue(authRepository.isLoggedIn())
        }
    }

    fun clearUiError() {
        _legacyUiState.value = UiState.Idle
        clearError()
    }
}

