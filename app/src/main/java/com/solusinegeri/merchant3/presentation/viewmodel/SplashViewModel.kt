package com.solusinegeri.merchant3.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.solusinegeri.merchant3.core.base.BaseViewModel
import com.solusinegeri.merchant3.data.repository.AuthRepository
import com.solusinegeri.merchant3.core.security.TokenManager

class SplashViewModel : BaseViewModel() {
    
    private lateinit var authRepository: AuthRepository
    
    private val _splashUiState = MutableLiveData<OperationUiState>()
    val splashUiState: LiveData<OperationUiState> = _splashUiState
    
    private val _navigationState = MutableLiveData<SplashNavigationState>()
    val navigationState: LiveData<SplashNavigationState> = _navigationState
    
    fun initialize(authRepository: AuthRepository) {
        this.authRepository = authRepository
        TokenManager.initialize(authRepository.appContext)
    }
    
    fun checkAuthenticationAndNavigate() {
        _splashUiState.value = OperationUiState.Loading
        
        launchCoroutine(showLoading = false) {
            try {
                if (authRepository.isUserLoggedIn()) {
                    if (authRepository.needsTokenRefresh()) {
                        refreshTokenAndNavigate()
                    } else {
                        navigateToMain()
                    }
                } else {
                    navigateToLogin()
                }
            } catch (e: Exception) {
                _splashUiState.value = OperationUiState.Error("Error checking authentication: ${e.message}")
                navigateToLogin()
            }
        }
    }
    
    private suspend fun refreshTokenAndNavigate() {
        try {
            val refreshResult = authRepository.refreshTokenIfNeeded()
            
            refreshResult.fold(
                onSuccess = { refreshed ->
                    if (refreshed) {
                        _splashUiState.value = OperationUiState.Success("Token refreshed successfully")
                        navigateToMain()
                    } else {
                        _splashUiState.value = OperationUiState.Success("Token still valid")
                        navigateToMain()
                    }
                },
                onFailure = { error ->
                    _splashUiState.value = OperationUiState.Error("Failed to refresh token: ${error.message}")
                    authRepository.logout()
                    navigateToLogin()
                }
            )
        } catch (e: Exception) {
            _splashUiState.value = OperationUiState.Error("Error refreshing token: ${e.message}")
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
}
