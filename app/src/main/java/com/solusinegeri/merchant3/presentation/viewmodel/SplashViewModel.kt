package com.solusinegeri.merchant3.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.solusinegeri.merchant3.core.auth.TokenProvider
import com.solusinegeri.merchant3.core.base.BaseViewModel
import com.solusinegeri.merchant3.core.utils.ApiErrorHandler
import com.solusinegeri.merchant3.core.utils.toUserMessage
import com.solusinegeri.merchant3.data.repository.AuthRepository
import com.solusinegeri.merchant3.core.security.TokenManager

class SplashViewModel : BaseViewModel() {
    
    private lateinit var authRepository: AuthRepository
    private lateinit var tokenProvider: TokenProvider
    
    private val _splashUiState = MutableLiveData<OperationUiState>()
    val splashUiState: LiveData<OperationUiState> = _splashUiState
    
    private val _navigationState = MutableLiveData<SplashNavigationState>()
    val navigationState: LiveData<SplashNavigationState> = _navigationState
    
    fun initialize(authRepository: AuthRepository) {
        this.authRepository = authRepository
        this.tokenProvider = TokenProvider(authRepository.appContext)
        TokenManager.initialize(authRepository.appContext)
    }
    
    fun checkAuthenticationAndNavigate() {
        _splashUiState.value = OperationUiState.Loading
        
        launchIO(showLoading = false) {
            try {
                if (tokenProvider.isTokenValid()) {
                    if (tokenProvider.needsRefresh()) {
                        refreshTokenAndNavigate()
                    } else {
                        navigateToMain()
                    }
                } else {
                    // Token expired, coba auto-refresh dulu jika ada credentials
                    if (tokenProvider.hasCredentialsForRefresh()) {
                        refreshTokenAndNavigate()
                    } else {
                        navigateToInitial()
                    }
                }
            } catch (e: Exception) {
                // Check if error requires logout
                if (ApiErrorHandler.requiresLogout(e)) {
                    _splashUiState.value = OperationUiState.Error("Sesi Anda telah berakhir. Silakan login ulang.")
                    authRepository.logout()
                    navigateToLogin()
                } else {
                    _splashUiState.value = OperationUiState.Error("Error checking authentication: ${e.toUserMessage()}")
                    navigateToInitial()
                }
            }
        }
    }
    
    private suspend fun refreshTokenAndNavigate() {
        try {
            val refreshResult = authRepository.refreshTokenIfNeeded()
            
            refreshResult.fold(
                onSuccess = { refreshed ->
                    navigateToMain()
                },
                onFailure = { error ->
                    // Check if error requires logout
                    if (ApiErrorHandler.requiresLogout(error)) {
                        _splashUiState.value = OperationUiState.Error("Sesi Anda telah berakhir. Silakan login ulang.")
                    } else {
                        _splashUiState.value = OperationUiState.Error("Failed to refresh token: ${error.toUserMessage()}")
                    }
                    authRepository.logout()
                    navigateToLogin()
                }
            )
        } catch (e: Exception) {
            // Check if error requires logout
            if (ApiErrorHandler.requiresLogout(e)) {
                _splashUiState.value = OperationUiState.Error("Sesi Anda telah berakhir. Silakan login ulang.")
            } else {
                _splashUiState.value = OperationUiState.Error("Error refreshing token: ${e.toUserMessage()}")
            }
            authRepository.logout()
            navigateToLogin()
        }
    }
    
    private fun navigateToMain() {
        _navigationState.value = SplashNavigationState.NavigateToMain
    }
    
    private fun navigateToLogin() {
        _navigationState.value = SplashNavigationState.NavigateToLogin
    }

    private fun navigateToInitial() {
        _navigationState.value = SplashNavigationState.NavigateToInitial
    }
    
    fun clearNavigationState() {
        _navigationState.value = SplashNavigationState.Idle
    }
    
    fun clearSplashError() {
        _splashUiState.value = OperationUiState.Idle
    }
}

/**
 * Navigation state untuk splash screen
 */
sealed class SplashNavigationState {
    object Idle : SplashNavigationState()
    object NavigateToMain : SplashNavigationState()
    object NavigateToLogin : SplashNavigationState()
    object NavigateToInitial : SplashNavigationState()
}
