package com.solusinegeri.merchant3.core.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.solusinegeri.merchant3.R
import com.solusinegeri.merchant3.core.utils.AutoFontApplier
import com.solusinegeri.merchant3.presentation.component.loading.LoadingPage
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel> : FragmentActivity() {

    protected lateinit var binding: VB
    protected abstract val viewModel: VM

    private var loadingOverlay: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = getViewBinding()

        setContentView(binding.root)

        AutoFontApplier.applyAutoFontToRootView(binding.root)
        setupStatusBar()

        setupUI()
        observeViewModel()
        setupClickListeners()
    }

    protected abstract fun getViewBinding(): VB

    protected open fun setupUI() {}
    protected open fun setupClickListeners() {}

    open fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading -> showLoading(isLoading) }
        }
        lifecycleScope.launch {
            viewModel.errorMessage.collect { msg ->
                msg?.let { showError(it); viewModel.clearError() }
            }
        }
        lifecycleScope.launch {
            viewModel.successMessage.collect { msg ->
                msg?.let { showSuccess(it); viewModel.clearSuccess() }
            }
        }
    }

    fun showLoading(show: Boolean) {
        if (show) showLoadingOverlay() else hideLoadingOverlay()
    }

    private fun showLoadingOverlay() {
        if (loadingOverlay != null) return
        val overlay = createLoadingOverlay()
        (binding.root as ViewGroup).addView(overlay)
        overlay.alpha = 0f
        overlay.animate().alpha(1f).setDuration(300).start()
        loadingOverlay = overlay
    }

    private fun hideLoadingOverlay() {
        loadingOverlay?.let { overlay ->
            overlay.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    (binding.root as ViewGroup).removeView(overlay)
                    loadingOverlay = null
                }
                .start()
        }
    }

    private fun createLoadingOverlay(): View {
        val sizeInDp = 80
        val sizeInPx = (sizeInDp * resources.displayMetrics.density).toInt()

        return FrameLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(android.graphics.Color.parseColor("#80000000"))

            addView(LoadingPage(this@BaseActivity).apply {
                layoutParams = FrameLayout.LayoutParams(sizeInPx, sizeInPx).apply {
                    gravity = android.view.Gravity.CENTER
                }
            })
        }
    }

    protected open fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Tutup") {}
            .show()
    }

    protected open fun showSuccess(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setAction("OK") {}
            .show()
    }

    protected fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    protected fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    protected fun showKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.showSoftInput(view, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
    }

    protected open fun setupStatusBar() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setStatusBarColor(getColor(R.color.white), true)
    }

    @SuppressLint("DeprecatedApi")
    protected fun setStatusBarColor(color: Int, lightStatusBar: Boolean) {
        window.statusBarColor = color
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = lightStatusBar
        }
    }

    @SuppressLint("DeprecatedApi")
    protected fun setNavigationBarColor(color: Int, lightNavigationBar: Boolean) {
        window.navigationBarColor = color
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightNavigationBars = lightNavigationBar
        }
    }

    protected fun setSystemBarsColor(statusBarColor: Int, navigationBarColor: Int, lightBars: Boolean) {
        setStatusBarColor(statusBarColor, lightBars)
        setNavigationBarColor(navigationBarColor, lightBars)
    }

    protected fun makeStatusBarTransparent() {
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
    }

    fun setupEdgeToEdge() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    protected fun makeNavigationBarTransparent() {
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightNavigationBars = true
        }
    }

    protected fun makeSystemBarsTransparent() {
        makeStatusBarTransparent()
        makeNavigationBarTransparent()
    }
}
