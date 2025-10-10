package com.solusinegeri.merchant3.presentation.ui.menu.menupin

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.activity.viewModels
import com.solusinegeri.merchant3.R.string
import com.solusinegeri.merchant3.core.base.BaseActivity
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.databinding.ActivityPinMenuBinding
import com.solusinegeri.merchant3.presentation.ui.menu.menupin.ChangePinActivity
import com.solusinegeri.merchant3.presentation.viewmodel.PinMenuViewModel

class PinMenuActivity : BaseActivity<ActivityPinMenuBinding, PinMenuViewModel>() {

    override val viewModel: PinMenuViewModel by viewModels()

    override fun getViewBinding(): ActivityPinMenuBinding {
        return ActivityPinMenuBinding.inflate(layoutInflater)
    }

    override fun setupUI() {
        super.setupUI()
        setupEdgeToEdge()
        setupToolBar()
        updateUIWithDynamicColors()
        setupClickListeners()
        // Setup toolbar

    }
    private fun setupToolBar(){
        binding.toolbar.tvTitle.text = getString(string.Pin_name)
        binding.toolbar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun updateUIWithDynamicColors() {
        val primaryColor = DynamicColors.getPrimaryColor(this.baseContext)
        val whiteColor = getColor(android.R.color.white)

        // Update logout button
        binding.btnGantiPin.backgroundTintList = ColorStateList.valueOf(primaryColor)
        binding.btnLupaPin.backgroundTintList=ColorStateList.valueOf(primaryColor)
    }

    override fun setupClickListeners() {
        super.setupClickListeners()

        binding.btnGantiPin.setOnClickListener {
            val intent = Intent(this, ChangePinActivity::class.java)
            startActivity(intent)
        }

        binding.btnLupaPin.setOnClickListener {
            val intent = Intent(this, ForgotPinActivity::class.java)
            startActivity(intent)
        }
    }
}