package com.solusinegeri.merchant3.presentation.ui.menu.menupin

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.solusinegeri.merchant3.R.string
import com.solusinegeri.merchant3.core.base.BaseActivity
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.databinding.ActivityChangePinBinding
import com.solusinegeri.merchant3.presentation.viewmodel.ChangePinState
import com.solusinegeri.merchant3.presentation.viewmodel.ChangePinViewModel
import kotlinx.coroutines.launch

class ChangePinActivity : BaseActivity<ActivityChangePinBinding, ChangePinViewModel>() {

    override val viewModel: ChangePinViewModel by viewModels()

    override fun getViewBinding(): ActivityChangePinBinding {
        return ActivityChangePinBinding.inflate(layoutInflater)
    }

    override fun setupUI() {
        super.setupUI()
        setupEdgeToEdge()
        setupToolBar()
        observeViewModel()
        updateUIWithDynamicColors()

        // Set input type to password
        binding.etPinLama.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        binding.etPinBaru.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        binding.etKonfirmasiPinBaru.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
    }

    private fun updateUIWithDynamicColors() {
        val primaryColor = DynamicColors.getPrimaryColor(this.baseContext)
        val whiteColor = getColor(android.R.color.white)

        // Update logout button
        binding.btnKonfirmasi.backgroundTintList = ColorStateList.valueOf(primaryColor)
        binding.btnKonfirmasi.setTextColor(ColorStateList.valueOf(whiteColor))
    }
    private fun setupToolBar(){
        binding.toolbar.tvTitle.text = "Ganti Pin"
        binding.toolbar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    override fun observeViewModel() {
        super.observeViewModel()

        lifecycleScope.launch {
            viewModel.changePinState.collect { state ->
                when (state) {
                    is ChangePinState.Idle -> {
                        // Do nothing
                    }
                    is ChangePinState.Loading -> {
                        showLoading(true)
                    }
                    is ChangePinState.Success -> {
                        showLoading(false)
                        showSuccess(state.message)
                        // Kembali ke halaman sebelumnya setelah 1 detik
                        binding.root.postDelayed({
                            finish()
                        }, 1000)
                    }
                    is ChangePinState.Error -> {
                        showLoading(false)
                        showError(state.message)
                    }
                }
            }
        }
    }

    override fun setupClickListeners() {
        super.setupClickListeners()

        binding.btnKonfirmasi.setOnClickListener {
            val pinLama = binding.etPinLama.text.toString()
            val pinBaru = binding.etPinBaru.text.toString()
            val konfirmasiPinBaru = binding.etKonfirmasiPinBaru.text.toString()

            // Validasi input
            if (pinLama.isEmpty()) {
                showError("PIN lama harus diisi")
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

            if (pinBaru.length != 6) {
                showError("PIN harus 6 digit")
                return@setOnClickListener
            }

            if (pinBaru != konfirmasiPinBaru) {
                showError("PIN baru dan konfirmasi tidak cocok")
                return@setOnClickListener
            }

            if (pinLama == pinBaru) {
                showError("PIN baru tidak boleh sama dengan PIN lama")
                return@setOnClickListener
            }

            // Call ViewModel to change PIN
            viewModel.changePin(pinLama, pinBaru)
        }
    }
}