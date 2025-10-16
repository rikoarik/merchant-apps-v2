package com.solusinegeri.merchant3.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.solusinegeri.merchant3.core.base.BaseViewModel
import com.solusinegeri.merchant3.core.security.SecureStorage
import com.solusinegeri.merchant3.data.repository.PinCodeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ========================== PinMenuViewModel ==========================
class PinMenuViewModel : BaseViewModel() {
    // Simple ViewModel, no special logic needed
}

// ========================== ChangePinViewModel ==========================
sealed class ChangePinState {
    object Idle : ChangePinState()
    object Loading : ChangePinState()
    data class Success(val message: String) : ChangePinState()
    data class Error(val message: String) : ChangePinState()
}

class ChangePinViewModel : BaseViewModel() {

    private val repository = PinCodeRepository()

    private val _changePinState = MutableStateFlow<ChangePinState>(ChangePinState.Idle)
    val changePinState: StateFlow<ChangePinState> = _changePinState.asStateFlow()

    fun changePin(oldPin: String, newPin: String) {
        viewModelScope.launch {
            try {
                _changePinState.value = ChangePinState.Loading

                // Call API
                val result = repository.changePin(oldPin, newPin)

                result.onSuccess { message ->
                    _changePinState.value = ChangePinState.Success(message)
                }.onFailure { error ->
                    _changePinState.value = ChangePinState.Error(
                        error.message ?: "Terjadi kesalahan saat mengubah PIN"
                    )
                }

            } catch (e: Exception) {
                _changePinState.value = ChangePinState.Error(
                    e.message ?: "Terjadi kesalahan saat mengubah PIN"
                )
            }
        }
    }
}

// ========================== ForgotPinViewModel ==========================
sealed class ForgotPinState {
    object Idle : ForgotPinState()
    object Loading : ForgotPinState()
    object OtpSent : ForgotPinState()
    data class Success(val message: String) : ForgotPinState()
    data class Error(val message: String) : ForgotPinState()
}

class ForgotPinViewModel : BaseViewModel() {

    private val _forgotPinState = MutableStateFlow<ForgotPinState>(ForgotPinState.Idle)
    val forgotPinState: StateFlow<ForgotPinState> = _forgotPinState.asStateFlow()

    fun requestOtp(email: String) {
        launchCoroutine {
            try {
                _forgotPinState.value = ForgotPinState.Loading

                kotlinx.coroutines.delay(1500)
                _forgotPinState.value = ForgotPinState.OtpSent

            } catch (e: Exception) {
                _forgotPinState.value = ForgotPinState.Error(
                    e.message ?: "Terjadi kesalahan saat mengirim OTP"
                )
            }
        }
    }


    fun resetPin(otp: String, newPin: String) {
        launchCoroutine {
            try {
                _forgotPinState.value = ForgotPinState.Loading

                kotlinx.coroutines.delay(1500)

                _forgotPinState.value = ForgotPinState.Success("PIN berhasil direset")

            } catch (e: Exception) {
                _forgotPinState.value = ForgotPinState.Error(
                    e.message ?: "Terjadi kesalahan saat mereset PIN"
                )
            }
        }
    }

}