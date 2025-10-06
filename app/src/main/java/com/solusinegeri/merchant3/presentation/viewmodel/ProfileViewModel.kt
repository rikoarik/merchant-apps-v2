package com.solusinegeri.merchant3.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.solusinegeri.merchant3.core.base.BaseViewModel
import com.solusinegeri.merchant3.data.model.UserData

/**
 * Contoh implementasi ViewModel dengan UiState pattern
 * Bisa digunakan sebagai template untuk ViewModel lainnya
 */
class ProfileViewModel : BaseViewModel() {
    
    // Data LiveData
    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> = _userData
    
    // UI State LiveData
    private val _profileUiState = MutableLiveData<DataUiState<UserData>>()
    val profileUiState: LiveData<DataUiState<UserData>> = _profileUiState
    
    private val _updateProfileUiState = MutableLiveData<OperationUiState>()
    val updateProfileUiState: LiveData<OperationUiState> = _updateProfileUiState
    
    fun loadProfileData() {
        _profileUiState.value = DataUiState.Loading
        launchCoroutine(showLoading = false) {
            // Simulasi API call
            try {
                // Repository call here
                // val result = profileRepository.getProfile()
                // result.onSuccess { userData ->
                //     _userData.value = userData
                //     _profileUiState.value = DataUiState.Success(userData, "Profile berhasil dimuat")
                // }
                // .onFailure { error ->
                //     _profileUiState.value = DataUiState.Error(error.message ?: "Gagal memuat profile")
                //     setError(error.message ?: "Gagal memuat profile")
                // }
            } catch (e: Exception) {
                _profileUiState.value = DataUiState.Error("Error: ${e.message}")
                setError("Error: ${e.message}")
            }
        }
    }
    
    fun updateProfile(userData: UserData) {
        _updateProfileUiState.value = OperationUiState.Loading
        launchCoroutine(showLoading = false) {
            try {
                // Repository call here
                // val result = profileRepository.updateProfile(userData)
                // result.onSuccess {
                //     _userData.value = userData
                //     _updateProfileUiState.value = OperationUiState.Success("Profile berhasil diupdate")
                // }
                // .onFailure { error ->
                //     _updateProfileUiState.value = OperationUiState.Error(error.message ?: "Gagal update profile")
                //     setError(error.message ?: "Gagal update profile")
                // }
            } catch (e: Exception) {
                _updateProfileUiState.value = OperationUiState.Error("Error: ${e.message}")
                setError("Error: ${e.message}")
            }
        }
    }
    
    fun clearProfileError() {
        _profileUiState.value = DataUiState.Idle
    }
    
    fun clearUpdateProfileError() {
        _updateProfileUiState.value = OperationUiState.Idle
    }
}