package com.solusinegeri.merchant3.presentation.viewmodel

import android.R
import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.solusinegeri.merchant3.core.base.BaseViewModel
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.data.model.UpdateUserModel
import com.solusinegeri.merchant3.data.model.UserData
import com.solusinegeri.merchant3.data.repository.ProfileRepository

/**
 * Contoh implementasi ViewModel dengan UiState pattern
 * Bisa digunakan sebagai template untuk ViewModel lainnya
 */
class ProfileViewModel(private var repository: ProfileRepository) : BaseViewModel() {
    // Data LiveData
    private val _userData: MutableLiveData<UserData>  = MutableLiveData<UserData>()
    val userData: LiveData<UserData> = _userData
    
    // UI State LiveData
    private val _profileUiState = MutableLiveData<DataUiState<UserData>>()
    val profileUiState: LiveData<DataUiState<UserData>> = _profileUiState

    private val _passwordChangeState = MutableLiveData<DataUiState<String>>()
    val passwordChangeState: LiveData<DataUiState<String>> = _passwordChangeState
    
    private val _updateProfileUiState = MutableLiveData<OperationUiState>()
    val updateProfileUiState: LiveData<OperationUiState> = _updateProfileUiState
    
    fun loadProfileData() {
        _profileUiState.value = DataUiState.Loading
        launchCoroutine(showLoading = false) {
            // Simulasi API call
            try{
                repository.getProfile()
                    .onSuccess { userData ->
                        _userData.value = userData
                        _profileUiState.value = DataUiState.Success(userData, "Berhasil memuat profil")
                    }
                    .onFailure { err ->
                        val message = err.message ?: "Gagal memuat profil"
                        _profileUiState.value = DataUiState.Error(message)
                        setError(message)
                    }
            }catch (e: Exception){
                val message = e.message ?: "Gagal memuat profil"
                _profileUiState.value = DataUiState.Error(message)
                setError(message)
            }
        }
    }
    
    fun updateProfile(updateModel: UpdateUserModel) {
        _updateProfileUiState.value = OperationUiState.Loading
        launchCoroutine(showLoading = false) {
            try {
                repository.updateProfile(updateModel)
                    .onSuccess { responseBody ->
                        _updateProfileUiState.value = OperationUiState.Success()
                    }
                    .onFailure { err ->
                        val message = err.message ?: "Ubah Password Gagal"
                        _updateProfileUiState.value = OperationUiState.Error(message)
                        setError(message)
                    }
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

    fun changePass(
        oldPassword    : String,
        newPassword    : String,
        confirmPassword: String
    ){
        _passwordChangeState.value = DataUiState.Loading

        launchCoroutine(showLoading = true){
            repository.changePassword(
                oldPassword,
                newPassword,
                confirmPassword
            )
                .onSuccess { responseBody ->
                    _passwordChangeState.value = DataUiState.Success("Berhasil Mengubah Password")
                }
                .onFailure { error ->
                    val message = error.message ?: "Ubah Password Gagal"
                    _passwordChangeState.value = DataUiState.Error(message)
                    setError(message)
                }
        }
    }

    //TODO(Replace with custom pup up at BaseViewModel)
    fun showDialogue(context: Context, message: String) {
        val dialog = AlertDialog.Builder(context)
            .setTitle("Berhasil")
            .setMessage(message)
            .setPositiveButton("Lanjut") { dialogInterface, _ ->
                dialogInterface.dismiss()
                _passwordChangeState.value = DataUiState.Idle
            }
            .setCancelable(false)
            .create()

        dialog.show()
        val primaryColor = DynamicColors.getPrimaryColor(context)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
            setBackgroundColor(primaryColor)
            setTextColor(resources.getColor(R.color.white))
        }
    }
}