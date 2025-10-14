package com.solusinegeri.merchant3.presentation.ui.initial

import android.content.Context
import com.solusinegeri.merchant3.core.base.BaseViewModel
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.core.utils.toUserMessage
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
        launchIO(showLoading = false) {
            try {
                _instansiResult.value = InstansiResult.Loading
                
                val validationResult = validateInput(instansiCode)
                if (!validationResult.isValid) {
                    _instansiResult.value = InstansiResult.Error(validationResult.errorMessage ?: "Validation error")
                    return@launchIO
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
                            error.toUserMessage().contains("tidak ditemukan", ignoreCase = true) -> {
                                "Kode instansi tidak ditemukan. Silakan periksa kembali kode yang Anda masukkan."
                            }
                            error.toUserMessage().contains("tidak valid", ignoreCase = true) -> {
                                "Format kode instansi tidak valid. Silakan masukkan kode yang benar."
                            }
                            error.toUserMessage().contains("akses", ignoreCase = true) -> {
                                "Anda tidak memiliki akses untuk mengakses data ini."
                            }
                            error.toUserMessage().contains("server", ignoreCase = true) -> {
                                "Terjadi kesalahan pada server. Silakan coba lagi nanti."
                            }
                            error.toUserMessage().contains("jaringan", ignoreCase = true) -> {
                                "Terjadi kesalahan jaringan. Periksa koneksi internet Anda."
                            }
                            else -> error.toUserMessage()
                        }
                        
                        _instansiResult.value = InstansiResult.Error(errorMessage)
                    }
                )
                
            } catch (e: Exception) {
                _instansiResult.value = InstansiResult.Error(handleException(e))
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
            else -> "Terjadi kesalahan yang tidak diketahui: ${exception.toUserMessage()}"
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
