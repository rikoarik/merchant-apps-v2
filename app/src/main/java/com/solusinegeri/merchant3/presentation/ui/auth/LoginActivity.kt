package com.solusinegeri.merchant3.presentation.ui.auth

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.text.Editable
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.solusinegeri.merchant3.presentation.ui.main.MainActivity
import com.solusinegeri.merchant3.R
import com.solusinegeri.merchant3.config.BuildConfig
import com.solusinegeri.merchant3.core.base.BaseActivity
import com.solusinegeri.merchant3.core.base.BaseViewModel
import com.solusinegeri.merchant3.core.security.InputValidator
import com.solusinegeri.merchant3.core.security.SecurityLogger
import com.solusinegeri.merchant3.core.security.SecureStorage
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.core.utils.UIThemeUpdater
import com.solusinegeri.merchant3.data.repository.AuthRepository
import com.solusinegeri.merchant3.databinding.ActivityLoginBinding
import com.solusinegeri.merchant3.presentation.ui.initial.InitialInputActivity
import com.solusinegeri.merchant3.presentation.viewmodel.AuthViewModel
import com.solusinegeri.merchant3.presentation.viewmodel.AuthViewModelFactory
import com.solusinegeri.merchant3.presentation.viewmodel.UiState
import com.solusinegeri.merchant3.core.security.SecurityThreat
import com.solusinegeri.merchant3.presentation.ui.compose.SecurityDialog
import kotlinx.coroutines.launch
import java.net.URLEncoder

/**
 * LoginActivity yang menggunakan BaseActivity untuk konsistensi
 */
class LoginActivity : BaseActivity<ActivityLoginBinding, AuthViewModel>() {

    private var companyId: String = ""

    override val viewModel: AuthViewModel by lazy {
        val authRepository = AuthRepository(this)
        val factory = AuthViewModelFactory(authRepository)
        ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    override fun getViewBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun setupUI() {
        super.setupUI()
        
        binding.tvVersion.text = BuildConfig.VERSION_NAME
        
        setupTextWatchers()
        
        binding.btnLogin.isEnabled = false
        updateUIWithDynamicColors()
        
        handleIntentData()
        performSecurityCheck()

    }

    
    private fun handleIntentData() {
        checkSavedInstansiData()
    }
    
    /**
     * Check saved instansi data dari SharedPreferences
     */
    private fun checkSavedInstansiData() {
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val savedInstansiId = sharedPref.getString("instansi_id", null)
        val savedInstansiCode = sharedPref.getString("instansi_code", null)
        val savedInstansiName = sharedPref.getString("instansi_name", null)
        
        if (!savedInstansiId.isNullOrEmpty()) {
            companyId = savedInstansiId
            
            if (!savedInstansiName.isNullOrEmpty()) {
                binding.tilUsername.hint = "Username/Email - $savedInstansiCode"
            }
            
            updateLoginButtonState()
        }
    }
    
    /**
     * Clear instansi data dari SharedPreferences
     */
    private fun clearInstansiData() {
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("instansi_id")
            remove("instansi_code")
            remove("instansi_name")
            remove("logo_url")
            remove("instansi_saved_time")
            apply()
        }
    }
    
    
    /**
     * Update UI colors dengan dynamic colors dari config initial
     */
    private fun updateUIWithDynamicColors() {
        val primaryColor = DynamicColors.getPrimaryColor(this)
        val secondaryColor = DynamicColors.getSecondaryColor(this)
        
        // Update title color
        UIThemeUpdater.updateTextColor(binding.tvLoginTitle, this, true)
        
        // Update help desk button color
        binding.btnHelpDesk.setTextColor(primaryColor)
        
        // Update version text color
        binding.tvVersion.setTextColor(secondaryColor)
        
        updateButtonColors(false)
    }
    
    /**
     * Setup text watchers untuk clear error ketika user mulai mengetik
     */
    private fun setupTextWatchers() {
        binding.etUsername.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.tilUsername.error != null) {
                    binding.tilUsername.error = null
                }
                updateLoginButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        
        binding.etPassword.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.tilPassword.error != null) {
                    binding.tilPassword.error = null
                }
                updateLoginButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    /**
     * Update login button state berdasarkan input fields
     */
    private fun updateLoginButtonState() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        val shouldEnable = username.isNotEmpty() && password.isNotEmpty() && !binding.btnLogin.isLoading
        binding.btnLogin.isEnabled = shouldEnable

        updateButtonColors(shouldEnable)

        binding.btnHelpDesk.setTextColor(DynamicColors.getPrimaryColor(this))
    }

    /**
     * Update button colors berdasarkan state
     */
    private fun updateButtonColors(isEnabled: Boolean) {
        val primaryColor = DynamicColors.getPrimaryColor(this)
        val whiteColor = getColor(android.R.color.white)

        if (isEnabled) {
            UIThemeUpdater.updateBackgroundColor(binding.btnLogin, this, true)
            binding.btnLogin.setTextColor(whiteColor)
        } else {
            binding.btnLogin.setBackgroundColor(primaryColor)
            binding.btnLogin.setTextColor(whiteColor)
        }
    }

    override fun setupClickListeners() {
        super.setupClickListeners()
        
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (!SecureStorage.canAttemptLogin(this)) {
                SecurityLogger.logBlockedLoginAttempt(
                    this,
                    username,
                    companyId,
                    "Rate limit exceeded"
                )
                showToast("Terlalu banyak percobaan login. Silakan tunggu 15 menit.")
                return@setOnClickListener
            }
            SecurityLogger.logLoginAttempt(this, username, companyId, false)
            
            viewModel.login(companyId, username, password)
        }
        
        binding.btnForgotPassword.setOnClickListener {
            showToast("Fitur lupa password akan segera tersedia")
        }
        
        binding.btnHelpDesk.setOnClickListener {
            val phone = "6289526643223"  // Misalnya nomor dalam format internasional tanpa "+"
            val message = "Permisi kak, Aku ada kendala nih, bisa bantu? \n\n" +
                    "Nama : \n" +
                    "Email : \n" +
                    "Company : \n" +
                    "Tanggal Lahir : \n" +
                    "Perihal : "
            val url = "https://api.whatsapp.com/send?phone=$phone&text=" + URLEncoder.encode(
                message,
                "UTF-8"
            )
            val sendIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(sendIntent)
        }
        
        binding.btnGoIntial.setOnClickListener {
            clearInstansiData()
            val intent = Intent(this, InitialInputActivity::class.java)
            startActivity(intent)
            finish()
        }
        
    }

    override fun observeViewModel() {
        super.observeViewModel()

        // ⛳️ ganti uiState -> uiStateLive
        viewModel.uiStateLive.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnLogin.setLoading(true)
                    updateButtonColors(false)
                }
                is UiState.Success -> {
                    binding.btnLogin.setSuccess(state.message)
                    showSuccess(state.message.toString())
                    SecurityLogger.logLoginAttempt(this, "", companyId, true)
                    SecureStorage.resetFailedLoginAttempts(this)
                    navigateToMain()
                }
                is UiState.Error -> {
                    binding.btnLogin.setError(state.message)
                    val errorMessage = mapLoginError(state.message)
                    showError(errorMessage)
                    SecurityLogger.logLoginAttempt(this, "", companyId, false, errorMessage)
                    SecureStorage.recordFailedLoginAttempt(this)
                    updateLoginButtonState()
                }
                is UiState.Idle -> {
                    binding.btnLogin.reset()
                    updateLoginButtonState()
                }
            }
        }

        viewModel.isLoggedIn.observe(this) { isLoggedIn ->
            if (isLoggedIn) navigateToMain()
        }
    }


    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Map login error message untuk user-friendly display
     */
    private fun mapLoginError(errorMessage: String?): String {
        return when {
            errorMessage?.contains("tidak ditemukan", ignoreCase = true) == true -> {
                "Username/Email atau password tidak ditemukan. Silakan periksa kembali."
            }
            errorMessage?.contains("password", ignoreCase = true) == true -> {
                "Password salah. Silakan periksa kembali password Anda."
            }
            errorMessage?.contains("username", ignoreCase = true) == true -> {
                "Username/Email tidak ditemukan. Silakan periksa kembali."
            }
            errorMessage?.contains("tidak valid", ignoreCase = true) == true -> {
                "Format Username/Email tidak valid. Silakan masukkan yang benar."
            }
            errorMessage?.contains("akses", ignoreCase = true) == true -> {
                "Anda tidak memiliki akses untuk login."
            }
            errorMessage?.contains("server", ignoreCase = true) == true -> {
                "Terjadi kesalahan pada server. Silakan coba lagi nanti."
            }
            errorMessage?.contains("jaringan", ignoreCase = true) == true -> {
                "Terjadi kesalahan jaringan. Periksa koneksi internet Anda."
            }
            errorMessage?.contains("timeout", ignoreCase = true) == true -> {
                "Permintaan timeout. Silakan coba lagi."
            }
            errorMessage?.contains("unauthorized", ignoreCase = true) == true -> {
                "Username/Email atau password salah."
            }
            errorMessage?.contains("forbidden", ignoreCase = true) == true -> {
                "Akses ditolak. Silakan hubungi administrator."
            }
            else -> errorMessage ?: "Terjadi kesalahan saat login. Silakan coba lagi."
        }
    }
    
    private fun performSecurityCheck() {
        val securityChecker = com.solusinegeri.merchant3.core.security.SecurityChecker(this)
        securityChecker.performSecurityCheck(
            onThreatsDetected = { threats ->
                showSecurityDialog(threats)
            },
            onNoThreats = {
                // No threats detected, continue normally
            }
        )
    }
    
    private fun showSecurityDialog(threats: List<SecurityThreat>) {
        // Create a simple dialog for login screen
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Security Alert")
            .setMessage("Security threats detected on this device. For security reasons, this app cannot run on compromised devices.")
            .setPositiveButton("Exit App") { _, _ ->
                finishAffinity()
            }
            .setNegativeButton("Continue Anyway") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

}