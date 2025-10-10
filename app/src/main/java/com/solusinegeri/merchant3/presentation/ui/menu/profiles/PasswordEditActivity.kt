package com.solusinegeri.merchant3.presentation.ui.menu.profiles

import android.R
import android.content.res.ColorStateList
import android.graphics.Typeface
import com.solusinegeri.merchant3.R.string
import com.solusinegeri.merchant3.core.base.BaseActivity
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.data.model.PasswordEditModel
import com.solusinegeri.merchant3.data.repository.PasswordRepository
import com.solusinegeri.merchant3.databinding.ActivityPasswordEditBinding
import com.solusinegeri.merchant3.presentation.viewmodel.PasswordEditState
import com.solusinegeri.merchant3.presentation.viewmodel.PasswordViewModel

class PasswordEditActivity : BaseActivity<ActivityPasswordEditBinding, PasswordViewModel>() {
    override val viewModel: PasswordViewModel by lazy {
        PasswordViewModel(
            PasswordRepository(
                this.baseContext
            )
        )
    }

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
        observeViewModel()
    }

    override fun getViewBinding(): ActivityPasswordEditBinding {
        return ActivityPasswordEditBinding.inflate(layoutInflater)
    }

    //region UI Initialisation

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
            viewModel.changePass(
                oldPassword     = textBoxTexts.oldPassword,
                newPassword     = textBoxTexts.password,
                confirmPassword = textBoxTexts.confirmPassword
            )
        }
    }
    //endregion

    override fun observeViewModel() {
        super.observeViewModel()

        viewModel.changePasswordState.observe(this){ state ->
            when(state){
                is PasswordEditState.Error ->{
                    binding.btnEditPassword.backgroundTintList = ColorStateList.valueOf(getColor(R.color.black))
                }
                is PasswordEditState.Loading -> {
                    binding.btnEditPassword.backgroundTintList = ColorStateList.valueOf(getColor(R.color.black))
                }
                is PasswordEditState.Success -> {
                    val colorPrimary = DynamicColors.getPrimaryColor(this.baseContext)
                    binding.btnEditPassword.backgroundTintList = ColorStateList.valueOf(colorPrimary)
                }

                is PasswordEditState.Idle -> {

                }
            }
        }
    }

    private fun getTextData() : PasswordEditModel{
        return PasswordEditModel(
            oldPassword     = binding.edOld.text.toString().trim(),
            password        = binding.edNew.text.toString().trim(),
            confirmPassword = binding.edConfirm.text.toString().trim()
        )
    }
}