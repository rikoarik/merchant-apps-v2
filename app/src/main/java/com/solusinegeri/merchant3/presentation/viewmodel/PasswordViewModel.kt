package com.solusinegeri.merchant3.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.solusinegeri.merchant3.core.base.BaseViewModel
import com.solusinegeri.merchant3.data.repository.PasswordRepository

sealed class PasswordEditState{
    object Idle    : PasswordEditState()
    object Loading : PasswordEditState()
    data class Success(val message: String): PasswordEditState()
    data class Error  (val message: String): PasswordEditState()
}

class PasswordViewModel(private var repository: PasswordRepository) : BaseViewModel() {
    private val _changePasswordState: MutableLiveData<PasswordEditState> = MutableLiveData()
    val changePasswordState: LiveData<PasswordEditState> get() = _changePasswordState

    var passwordStrength = ""
    fun changePass(
        oldPassword    : String,
        newPassword    : String,
        confirmPassword: String
    ){
        _changePasswordState.value = PasswordEditState.Loading

        launchCoroutine(showLoading = true){
            repository.changePassword(
                oldPassword,
                newPassword,
                confirmPassword
            ).onSuccess { responseBody ->
                _changePasswordState.value = PasswordEditState.Success("Berhasil Mengubah Password")
            }.onFailure { error ->
                val message = error.message ?: "Ubah Password Gagal"
                _changePasswordState.value = PasswordEditState.Error(message)
                setError(message)
            }
        }
    }
}