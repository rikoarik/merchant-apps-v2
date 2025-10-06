package com.solusinegeri.merchant3.presentation.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.solusinegeri.merchant3.core.base.BaseViewModel
import com.solusinegeri.merchant3.data.repository.AuthRepository
import com.solusinegeri.merchant3.data.network.NetworkClient
import kotlinx.coroutines.delay

/**
 * ViewModel untuk Splash Screen
 * Menangani logika untuk mengecek status login dan navigasi
 */
class SplashViewModel : BaseViewModel() {
    
    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn
    
    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin
    
    private val _navigateToMain = MutableLiveData<Boolean>()
    val navigateToMain: LiveData<Boolean> = _navigateToMain
    
    private val _navigateToInitial = MutableLiveData<Boolean>()
    val navigateToInitial: LiveData<Boolean> = _navigateToInitial
    
    /**
     * Cek status login user dan instansi tersimpan
     */
    fun checkLoginStatus(context: android.content.Context) {
        launchCoroutine(showLoading = false) {
            try {
                delay(2000)
                
                val isLoggedIn = checkUserLoginStatus(context)
                val hasInstansiData = checkInstansiData(context)
                
                if (isLoggedIn) {
                    _isLoggedIn.value = true
                    _navigateToMain.value = true
                } else if (hasInstansiData) {
                    _isLoggedIn.value = false
                    _navigateToLogin.value = true
                } else {
                    _isLoggedIn.value = false
                    _navigateToInitial.value = true
                }
                
            } catch (e: Exception) {
                setError("Error checking login status: ${e.message}")
                _navigateToInitial.value = true
            }
        }
    }
    
    /**
     * Cek apakah user sudah login
     * Validasi token berdasarkan local storage dan expiry time
     */
    private suspend fun checkUserLoginStatus(context: android.content.Context): Boolean {
        return try {
            val authRepository = AuthRepository(context)
            
            val isLoggedIn = authRepository.isLoggedIn()
            if (!isLoggedIn) {
                return false
            }
            
            val token = authRepository.getToken()
            if (token.isNullOrEmpty()) {
                authRepository.logout()
                return false
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Cek apakah ada data instansi tersimpan
     */
    private fun checkInstansiData(context: android.content.Context): Boolean {
        val sharedPref = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        val instansiId = sharedPref.getString("instansi_id", null)
        val instansiCode = sharedPref.getString("instansi_code", null)
        val instansiName = sharedPref.getString("instansi_name", null)
        
        return !instansiId.isNullOrEmpty() && !instansiCode.isNullOrEmpty() && !instansiName.isNullOrEmpty()
    }
    
    /**
     * Reset navigation flags
     */
    fun resetNavigationFlags() {
        _navigateToLogin.value = false
        _navigateToMain.value = false
        _navigateToInitial.value = false
    }
    
    /**
     * Clear login status
     */
    fun clearLoginStatus() {
        _isLoggedIn.value = false
    }
}
