package com.solusinegeri.merchant3.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.solusinegeri.merchant3.core.base.BaseViewModel
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

    private val _changePinState = MutableStateFlow<ChangePinState>(ChangePinState.Idle)
    val changePinState: StateFlow<ChangePinState> = _changePinState.asStateFlow()

    fun changePin(oldPin: String, newPin: String) {
        launchCoroutine {
            try {
                _changePinState.value = ChangePinState.Loading

                // TODO: Implement API call untuk change PIN
                // Contoh:
                // val response = authRepository.changePin(oldPin, newPin)
                // if (response.isSuccess) {
                //     _changePinState.value = ChangePinState.Success("PIN berhasil diubah")
                // } else {
                //     _changePinState.value = ChangePinState.Error(response.message)
                // }

                // Simulasi delay API call
                kotlinx.coroutines.delay(1500)

                // Simulasi success response (ganti dengan real API call)
                _changePinState.value = ChangePinState.Success("PIN berhasil diubah")

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

                // TODO: Implement API call untuk reset PIN dengan OTP
                // Contoh:
                // val response = authRepository.resetPin(otp, newPin)
                // if (response.isSuccess) {
                //     _forgotPinState.value = ForgotPinState.Success("PIN berhasil direset")
                // } else {
                //     _forgotPinState.value = ForgotPinState.Error(response.message)
                // }

                // Simulasi delay API call
                kotlinx.coroutines.delay(1500)

                // Simulasi success response (ganti dengan real API call)
                _forgotPinState.value = ForgotPinState.Success("PIN berhasil direset")

            } catch (e: Exception) {
                _forgotPinState.value = ForgotPinState.Error(
                    e.message ?: "Terjadi kesalahan saat mereset PIN"
                )
            }
        }
    }
}