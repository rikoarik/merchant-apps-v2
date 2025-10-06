package com.solusinegeri.merchant3.presentation.ui.initial

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.solusinegeri.merchant3.R
import com.solusinegeri.merchant3.core.base.BaseActivity
import com.solusinegeri.merchant3.core.utils.BackPressedHandler
import com.solusinegeri.merchant3.core.utils.ChuckerHelper
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.databinding.ActivityInitialInputBinding
import com.solusinegeri.merchant3.presentation.ui.auth.LoginActivity
import com.solusinegeri.merchant3.presentation.component.button.SmartButton
import kotlinx.coroutines.launch

/**
 * InitialInputActivity untuk input kode instansi
 */
class InitialInputActivity : BaseActivity<ActivityInitialInputBinding, InitialInputViewModel>() {
    
    override val viewModel: InitialInputViewModel by lazy { InitialInputViewModel() }
    
    override fun getViewBinding(): ActivityInitialInputBinding {
        return ActivityInitialInputBinding.inflate(layoutInflater)
    }
    
    override fun setupUI() {
        super.setupUI()
        setupInputField()
        setupCardView()
        setupSmartButton()
        setupChuckerButton()
        setupBackPressedHandler()
        checkSavedInstansiData()
    }

    
    private fun setupSmartButton() {
        binding.btnCheck.setOnClickListener {
            val instansiCode = binding.edInitial.text.toString().trim()
            if (instansiCode.isNotEmpty()) {
                binding.textInputLayout.error = null
                viewModel.checkInstansi(this@InitialInputActivity, instansiCode)
            }
        }
        
        // Initial state - disable button
        updateButtonState()
    }
    
    private fun updateButtonState() {
        val instansiCode = binding.edInitial.text.toString().trim()
        val shouldEnable = instansiCode.isNotEmpty() && !binding.btnCheck.isLoading
        binding.btnCheck.isEnabled = shouldEnable
    }
    
    override fun setupStatusBar() {
        super.setupStatusBar()
        setStatusBarColor(getColor(R.color.white), true)
        setNavigationBarColor(getColor(R.color.white), true)
    }
    
    override fun setupClickListeners() {
        super.setupClickListeners()
        
        binding.cardViewInitial.setOnClickListener {
            val instansiCode = getCurrentInstansiCode()
            if (instansiCode.isNotEmpty()) {
                navigateToLogin()
            } else {
                showError("Kode instansi tidak boleh kosong")
            }
        }
        
        binding.fabChucker.setOnClickListener {
            if (ChuckerHelper.isChuckerAvailable()) {
                ChuckerHelper.launchChucker(this)
            } else {
                showError(getString(R.string.chucker_not_available))
            }
        }
    }
    
    private fun setupInputField() {
        binding.edInitial.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonState()
            }
            
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }


    
    private fun setupCardView() {
        binding.cardViewInitial.visibility = android.view.View.GONE
        binding.imgCompanyLogo.visibility = android.view.View.GONE
    }
    
    
    override fun observeViewModel() {
        super.observeViewModel()
        
        lifecycleScope.launch {
            viewModel.instansiResult.collect { result ->
                when (result) {
                    is InitialInputViewModel.InstansiResult.Loading -> {
                        binding.btnCheck.setLoading(true)
                        updateButtonState()
                    }
                    is InitialInputViewModel.InstansiResult.Success -> {
                        binding.btnCheck.setSuccess("Berhasil!")
                        showInstansiInfo(result.instansiName, result.instansiCode, result.logoUrl)
                        updateButtonState()
                        binding.textInputLayout.error = null
                    }
                    is InitialInputViewModel.InstansiResult.Error -> {
                        binding.btnCheck.setError("Gagal!")
                        showError(result.message)
                        updateButtonState()
                        binding.textInputLayout.error = result.message
                    }
                    is InitialInputViewModel.InstansiResult.Idle -> {
                        binding.btnCheck.reset()
                        updateButtonState()
                    }
                }
            }
        }
    }
    
    private fun showInstansiInfo(instansiName: String, instansiCode: String, logoUrl: String) {
        binding.txtInstansi.text = instansiName
        binding.txtInitial.text = instansiCode
        
        binding.imgCompanyLogo.visibility = View.VISIBLE
        
        Glide.with(this)
            .load(logoUrl)
            .apply(
                com.bumptech.glide.request.RequestOptions()
                    .placeholder(R.drawable.ic_closepay_logo)
                    .error(R.drawable.ic_closepay_logo)
                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                    .centerInside()
                    .timeout(10000)
            )
            .into(binding.imgCompanyLogo)
        
        updateUIWithDynamicColors()
        
        binding.cardViewInitial.visibility = android.view.View.VISIBLE
        binding.cardViewInitial.alpha = 0f
        binding.cardViewInitial.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }
    
    private fun updateUIWithDynamicColors() {
        val titleView = findViewById<android.widget.TextView>(android.R.id.title)
        if (titleView != null) {
            com.solusinegeri.merchant3.core.utils.UIThemeUpdater.updateTextColor(titleView, this, true)
        }

        binding.txtInitialTitle.let { title ->
            com.solusinegeri.merchant3.core.utils.UIThemeUpdater.updateTextColor(title, this, true)
        }
    }
    
    private fun setupChuckerButton() {
        if (ChuckerHelper.isChuckerAvailable()) {
            binding.fabChucker.visibility = View.VISIBLE
        } else {
            binding.fabChucker.visibility = View.GONE
        }
    }
    
    private fun setupBackPressedHandler() {
        BackPressedHandler.addInputClearingCallback(
            activity = this,
            inputFields = listOf(binding.edInitial)
        ) {
            finishAffinity()
        }
    }
    
    private fun navigateToLogin() {
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val instansiName = binding.txtInstansi.text.toString()

        with(sharedPref.edit()) {
            putString("instansi_id", getCurrentInstansiId())
            putString("instansi_code", getCurrentInstansiCode())
            putString("instansi_name", instansiName)
            putString("logo_url", getCurrentLogoUrl())
            putLong("instansi_saved_time", System.currentTimeMillis())
            apply()
        }

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
    
    private fun getCurrentInstansiId(): String {
        return viewModel.instansiResult.value.let { result ->
            if (result is InitialInputViewModel.InstansiResult.Success) {
                result.instansiId
            } else {
                ""
            }
        }
    }

    private fun getCurrentInstansiCode(): String {
        return viewModel.instansiResult.value.let { result ->
            if (result is InitialInputViewModel.InstansiResult.Success) {
                result.instansiCode
            } else {
                ""
            }
        }
    }

    private fun getCurrentLogoUrl(): String {
        return viewModel.instansiResult.value.let { result ->
            if (result is InitialInputViewModel.InstansiResult.Success) {
                result.logoUrl
            } else {
                ""
            }
        }
    }


    private fun checkSavedInstansiData() {
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val savedInstansiId = sharedPref.getString("instansi_id", null)
        val savedInstansiCode = sharedPref.getString("instansi_code", null)
        val savedInstansiName = sharedPref.getString("instansi_name", null)
        val savedLogoUrl = sharedPref.getString("logo_url", null)

        if (!savedInstansiId.isNullOrEmpty() && !savedInstansiCode.isNullOrEmpty() && !savedInstansiName.isNullOrEmpty() && !savedLogoUrl.isNullOrEmpty()) {
            val successResult = InitialInputViewModel.InstansiResult.Success(
                instansiId = savedInstansiId,
                instansiName = savedInstansiName,
                instansiCode = savedInstansiCode,
                logoUrl = savedLogoUrl
            )
            viewModel.setInstansiResult(successResult)
        }
    }
}

