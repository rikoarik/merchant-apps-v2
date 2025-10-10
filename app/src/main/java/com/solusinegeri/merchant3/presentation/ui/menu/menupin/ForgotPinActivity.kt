package com.solusinegeri.merchant3.presentation.ui.menu.menupin

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.solusinegeri.merchant3.core.base.BaseActivity
import com.solusinegeri.merchant3.core.security.SecureStorage
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.databinding.ActivityForgotPinBinding
import com.solusinegeri.merchant3.presentation.viewmodel.ForgotPinState
import com.solusinegeri.merchant3.presentation.viewmodel.ForgotPinViewModel
import kotlinx.coroutines.launch

class ForgotPinActivity : BaseActivity<ActivityForgotPinBinding, ForgotPinViewModel>() {

    override val viewModel: ForgotPinViewModel by viewModels()

    override fun getViewBinding(): ActivityForgotPinBinding {
        return ActivityForgotPinBinding.inflate(layoutInflater)
    }

    override fun setupUI() {
        super.setupUI()
        setupEdgeToEdge()
        setupToolBar()
        setupClickListeners()
        updateUIWithDynamicColors()

        // Set input type to password for PIN fields
        binding.etPinBaru.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        binding.etKonfirmasiPinBaru.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD

        // Show OTP dialog on create
        showOtpDialog()
    }
    private fun updateUIWithDynamicColors() {
        val primaryColor = DynamicColors.getPrimaryColor(this.baseContext)
        val whiteColor = getColor(android.R.color.white)

        // Update logout button
        binding.btnForgotkonfirmasi.backgroundTintList = ColorStateList.valueOf(primaryColor)
        binding.btnForgotkonfirmasi.setTextColor(ColorStateList.valueOf(whiteColor))
    }
    private fun setupToolBar(){
        binding.toolbar.tvTitle.text = "LUPA PIN"
        binding.toolbar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
    override fun observeViewModel() {
        super.observeViewModel()

        lifecycleScope.launch {
            viewModel.forgotPinState.collect { state ->
                when (state) {
                    is ForgotPinState.Idle -> {
                        // Do nothing
                    }
                    is ForgotPinState.Loading -> {
                        showLoading(true)
                    }
                    is ForgotPinState.OtpSent -> {
                        showLoading(false)
                        showSuccess("OTP telah dikirim ke email Anda")
                    }
                    is ForgotPinState.Success -> {
                        showLoading(false)
                        showSuccess(state.message)
                        // Kembali ke halaman sebelumnya setelah 1 detik
                        binding.root.postDelayed({
                            finish()
                        }, 1000)
                    }
                    is ForgotPinState.Error -> {
                        showLoading(false)
                        showError(state.message)
                    }
                }
            }
        }
    }

    override fun setupClickListeners() {
        super.setupClickListeners()

        binding.tvKirimUlangOtp.setOnClickListener {
            showOtpDialog()
        }

        binding.btnForgotkonfirmasi.setOnClickListener {
            val otp = binding.etKodeOtp.text.toString()
            val pinBaru = binding.etPinBaru.text.toString()
            val konfirmasiPinBaru = binding.etKonfirmasiPinBaru.text.toString()

            // Validasi input
            if (otp.isEmpty()) {
                showError("Kode OTP harus diisi")
                return@setOnClickListener
            }


            if (pinBaru.isEmpty()) {
                showError("PIN baru harus diisi")
                return@setOnClickListener
            }

            if (konfirmasiPinBaru.isEmpty()) {
                showError("Konfirmasi PIN baru harus diisi")
                return@setOnClickListener
            }


            if (pinBaru != konfirmasiPinBaru) {
                showError("PIN baru dan konfirmasi tidak cocok")
                return@setOnClickListener
            }

            // Call ViewModel to reset PIN
            viewModel.resetPin(otp, pinBaru)
        }
    }

    private fun showOtpDialog() {
        val userData = SecureStorage.getUserData(this)
        val email = userData["user_email"] ?: "email@example.com"

        val dialog = AlertDialog.Builder(this)
            .setTitle("Kirim OTP")
            .setMessage("OTP akan dikirim ke email:\n$email")
            .setPositiveButton("Kirim") { dialogInterface, _ ->
                viewModel.requestOtp(email)
                dialogInterface.dismiss()
            }
            .setNegativeButton("Batal") { dialogInterface, _ ->
                dialogInterface.dismiss()
                finish()
            }
            .setCancelable(false)
            .create()

        dialog.show()
        val primaryColor = DynamicColors.getPrimaryColor(this.baseContext)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
            setTextColor(primaryColor)
        }
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
            setTextColor(primaryColor)
        }
    }

}