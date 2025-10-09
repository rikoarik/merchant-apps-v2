package com.solusinegeri.merchant3.presentation.ui.menu.profiles

import android.content.res.ColorStateList
import android.graphics.Typeface
import com.solusinegeri.merchant3.R.string
import com.solusinegeri.merchant3.core.base.BaseActivity
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.databinding.ActivityPasswordEditBinding
import com.solusinegeri.merchant3.presentation.viewmodel.ProfileViewModel

class PasswordEditActivity : BaseActivity<ActivityPasswordEditBinding, ProfileViewModel>() {
    override val viewModel: ProfileViewModel by lazy {ProfileViewModel()}

    val PASS_OLD_ITEM     = "pass_old"
    val PASS_NEW_ITEM     = "pass_new"
    val PASS_CONFRIM_ITEM = "pass_confirm"

    override fun setupUI() {
        super.setupUI()
        setupToolbar()
        setupEdgeToEdge()
        setupTypefaces()
        setupOnClickListeners()
        updateUIWithDynamicColors()
    }

    override fun getViewBinding(): ActivityPasswordEditBinding {
        return ActivityPasswordEditBinding.inflate(layoutInflater)
    }

    private fun setupToolbar(){
        binding.toolbar.apply {
            ivBack .setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            tvTitle.text = getString(string.edit_password )
        }
    }

    private fun updateUIWithDynamicColors() {
        val primaryColor = DynamicColors.getPrimaryColor(this.baseContext)

        // Update logout button
        binding.btnEditPassword.backgroundTintList = ColorStateList.valueOf(primaryColor)

        // Update textview border
        binding.edbxOld    .boxStrokeColor = primaryColor
        binding.edbxNew    .boxStrokeColor = primaryColor
        binding.edbxConfirm.boxStrokeColor = primaryColor
    }

    private fun setupTypefaces(){
        val bold = Typeface.create("", Typeface.BOLD)

        //Setup title typeface
        binding.tvTitle.typeface = bold

        //Setup toolbar title typeface
        binding.toolbar.tvTitle.typeface = bold
    }

    private fun setupOnClickListeners(){
        binding.btnEditPassword.setOnClickListener {
            val textBoxTexts = getTextData()
            println(textBoxTexts)
        }
    }

    private fun getTextData() : Map<String, String>{
        return mapOf(
            PASS_OLD_ITEM     to binding.edOld.text.toString(),
            PASS_NEW_ITEM     to binding.edNew.text.toString(),
            PASS_CONFRIM_ITEM to binding.edConfirm.text.toString()
        )
    }
}