package com.solusinegeri.merchant3.presentation.ui.menu.profiles

import android.R
import android.content.res.ColorStateList
import android.graphics.Typeface
import com.solusinegeri.merchant3.R.string
import com.solusinegeri.merchant3.core.base.BaseActivity
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.data.model.PasswordEditModel
import com.solusinegeri.merchant3.data.repository.ProfileRepository
import com.solusinegeri.merchant3.databinding.ActivityPasswordEditBinding
import com.solusinegeri.merchant3.presentation.viewmodel.DataUiState
import com.solusinegeri.merchant3.presentation.viewmodel.ProfileViewModel

class PasswordEditActivity : BaseActivity<ActivityPasswordEditBinding, ProfileViewModel>() {
    override val viewModel: ProfileViewModel by lazy {
        ProfileViewModel(
            ProfileRepository(
                this.baseContext
            )
        )
    }

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

        viewModel.passwordChangeState.observe(this){ state ->
            when(state){
                is DataUiState.Error   -> {
                    binding.btnEditPassword.setError(state.message)
                    binding.btnEditPassword.backgroundTintList = ColorStateList.valueOf(getColor(R.color.black))
                }
                is DataUiState.Loading -> {
                    binding.btnEditPassword.backgroundTintList = ColorStateList.valueOf(getColor(R.color.black))
                    binding.btnEditPassword.setLoading(true)
                }
                is DataUiState.Success -> {
                    binding.btnEditPassword.setSuccess(state.message)
                    viewModel.showDialogue(this, "Berhasil Mengubah Password")
                }
                is DataUiState.Idle    -> {
                    binding.btnEditPassword.reset()
                    onBackPressedDispatcher.onBackPressed()
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