package com.solusinegeri.merchant3.presentation.ui.menu.profiles

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.solusinegeri.merchant3.R
import com.solusinegeri.merchant3.core.base.BaseActivity
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.databinding.ActivityProfileEditBinding
import com.solusinegeri.merchant3.presentation.viewmodel.ProfileViewModel

class ProfileEditActivity : BaseActivity<ActivityProfileEditBinding, ProfileViewModel>() {
    override val viewModel: ProfileViewModel by lazy { ProfileViewModel() }

    override fun getViewBinding(): ActivityProfileEditBinding {
        return ActivityProfileEditBinding.inflate(layoutInflater)
    }

    override fun setupUI() {
        super.setupUI()
        setupEdgeToEdge()
        setupToolBar()
        updateUIWithDynamicColors()
    }

    private fun setupToolBar(){
        binding.toolbar.tvTitle.text = "Edit Profile"
        binding.toolbar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    override fun setupStatusBar() {
        super.setupStatusBar()
        setStatusBarColor(getColor(R.color.white), true)
        setNavigationBarColor(getColor(R.color.white), true)
    }
    private fun updateUIWithDynamicColors() {
        val primaryColor = DynamicColors.getPrimaryColor(this.baseContext)

        // Update logout button
        binding.btnLogout.backgroundTintList = ColorStateList.valueOf(primaryColor)

        // Update profile placeholder icon
        binding.ADDIMAGE.setColorFilter(primaryColor)
    }
}