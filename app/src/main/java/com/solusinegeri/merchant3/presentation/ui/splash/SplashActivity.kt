package com.solusinegeri.merchant3.presentation.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.solusinegeri.merchant3.R
import com.solusinegeri.merchant3.core.base.BaseActivity
import com.solusinegeri.merchant3.data.repository.AuthRepository
import com.solusinegeri.merchant3.databinding.ActivitySplashBinding
import com.solusinegeri.merchant3.presentation.ui.auth.LoginActivity
import com.solusinegeri.merchant3.presentation.ui.main.MainActivity
import com.solusinegeri.merchant3.presentation.viewmodel.SplashViewModel
import com.solusinegeri.merchant3.presentation.viewmodel.SplashNavigationState
import com.solusinegeri.merchant3.presentation.viewmodel.OperationUiState
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {
    
    override val viewModel: SplashViewModel by viewModels()
    
    private lateinit var authRepository: AuthRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        authRepository = AuthRepository(applicationContext)
        
        viewModel.initialize(authRepository)
        
        setupUI()
        observeViewModel()
        
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.checkAuthenticationAndNavigate()
        }, 1500)
    }
    
    override fun getViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }
    
    override fun setupUI() {
        binding.apply {
            lottieAnimation.setAnimation(R.raw.splash_animation)
        }
    }
    
    override fun observeViewModel() {
        viewModel.splashUiState.observe(this) { state ->
            when (state) {
                is OperationUiState.Loading -> {
                }
                is OperationUiState.Success -> {
                    state.message?.let { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }
                is OperationUiState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    viewModel.clearSplashError()
                }
                is OperationUiState.Idle -> {
                }
            }
        }
        
        viewModel.navigationState.observe(this) { navigationState ->
            when (navigationState) {
                is SplashNavigationState.NavigateToMain -> {
                    navigateToMain()
                }
                is SplashNavigationState.NavigateToLogin -> {
                    navigateToLogin()
                }
                is SplashNavigationState.Idle -> {
                }
            }
        }
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}