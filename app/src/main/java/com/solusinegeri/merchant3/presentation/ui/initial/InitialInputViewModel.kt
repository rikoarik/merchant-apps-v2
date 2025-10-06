package com.solusinegeri.merchant3.presentation.ui.initial

import android.content.Context
import com.solusinegeri.merchant3.core.base.BaseViewModel
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.data.repository.CompanyRepository
import com.solusinegeri.merchant3.data.network.NetworkClient
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel untuk InitialInputActivity dengan API integration
 */
class InitialInputViewModel : BaseViewModel() {
    
    sealed class InstansiResult {
        object Idle : InstansiResult()
        object Loading : InstansiResult()
        data class Success(val instansiId: String, val instansiName: String, val instansiCode: String, val logoUrl: String) : InstansiResult()
        data class Error(val message: String) : InstansiResult()
    }
    
    private val _instansiResult = kotlinx.coroutines.flow.MutableStateFlow<InstansiResult>(InstansiResult.Idle)
    val instansiResult: kotlinx.coroutines.flow.StateFlow<InstansiResult> = _instansiResult.asStateFlow()
    
    fun setInstansiResult(result: InstansiResult) {
        _instansiResult.value = result
    }
    
    private val companyRepository = CompanyRepository(NetworkClient.authService)
    
    fun checkInstansi(context: Context, instansiCode: String) {
        launchCoroutine(showLoading = false) {
            try {
                _instansiResult.value = InstansiResult.Loading
                
                val validationResult = validateInput(instansiCode)
                if (!validationResult.isValid) {
                    _instansiResult.value = InstansiResult.Error(validationResult.errorMessage ?: "Validation error")
                    return@launchCoroutine
                }
                
                val result = companyRepository.validateCompanyInitial(instansiCode)
                
                result.fold(
                    onSuccess = { companyData ->
                        companyData.color?.let { colors ->
                            DynamicColors.setCompanyColors(context, colors)
                        }
                        
                        _instansiResult.value = InstansiResult.Success(
                            instansiId = companyData.id ?: "",
                            instansiName = companyData.name ?: "Unknown Company",
                            instansiCode = companyData.initial ?: instansiCode,
                            logoUrl = companyData.companyLogo ?: ""
                        )
                    },
                    onFailure = { error ->
                        val errorMessage = when {
                            error.message?.contains("tidak ditemukan", ignoreCase = true) == true -> {
                                "Kode instansi tidak ditemukan. Silakan periksa kembali kode yang Anda masukkan."
                            }
                            error.message?.contains("tidak valid", ignoreCase = true) == true -> {
                                "Format kode instansi tidak valid. Silakan masukkan kode yang benar."
                            }
                            error.message?.contains("akses", ignoreCase = true) == true -> {
                                "Anda tidak memiliki akses untuk mengakses data ini."
                            }
                            error.message?.contains("server", ignoreCase = true) == true -> {
                                "Terjadi kesalahan pada server. Silakan coba lagi nanti."
                            }
                            error.message?.contains("jaringan", ignoreCase = true) == true -> {
                                "Terjadi kesalahan jaringan. Periksa koneksi internet Anda."
                            }
                            else -> error.message ?: "Gagal mengambil data company"
                        }
                        
                        _instansiResult.value = InstansiResult.Error(errorMessage)
                    }
                )
                
            } catch (e: Exception) {
                val errorMessage = handleException(e)
                _instansiResult.value = InstansiResult.Error(errorMessage)
            }
        }
    }
    
    /**
     * Handle different types of exceptions and return user-friendly messages
     */
    private fun handleException(exception: Exception): String {
        return when (exception) {
            is java.net.UnknownHostException -> "Tidak ada koneksi internet. Periksa koneksi Anda dan coba lagi."
            is java.net.SocketTimeoutException -> "Permintaan timeout. Silakan coba lagi."
            is java.io.IOException -> "Terjadi kesalahan jaringan. Periksa koneksi internet Anda."
            is retrofit2.HttpException -> {
                when (exception.code()) {
                    404 -> "Kode instansi tidak ditemukan. Silakan periksa kembali kode yang Anda masukkan."
                    400 -> "Format kode instansi tidak valid. Silakan masukkan kode yang benar."
                    401 -> "Anda tidak memiliki akses untuk mengakses data ini."
                    403 -> "Akses ditolak. Silakan hubungi administrator."
                    500 -> "Terjadi kesalahan pada server. Silakan coba lagi nanti."
                    else -> "Terjadi kesalahan server: ${exception.message()}"
                }
            }
            else -> "Terjadi kesalahan yang tidak diketahui: ${exception.message}"
        }
    }
    
    private fun validateInput(code: String): InstansiValidationResult {
        return when {
            code.isBlank() -> InstansiValidationResult(
                isValid = false,
                errorMessage = "Kode instansi tidak boleh kosong"
            )
            code.length < 2 -> InstansiValidationResult(
                isValid = false,
                errorMessage = "Kode instansi minimal 2 karakter"
            )
            code.length > 20 -> InstansiValidationResult(
                isValid = false,
                errorMessage = "Kode instansi maksimal 20 karakter"
            )
            else -> InstansiValidationResult(
                isValid = true,
                errorMessage = null
            )
        }
    }
    
    private data class InstansiValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )
}
