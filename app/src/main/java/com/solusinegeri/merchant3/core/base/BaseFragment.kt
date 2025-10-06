package com.solusinegeri.merchant3.core.base

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.solusinegeri.merchant3.presentation.component.loading.LoadingPage
import com.solusinegeri.merchant3.core.utils.AutoFontApplier
import kotlinx.coroutines.launch

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel> : Fragment() {

    protected lateinit var binding: VB
    protected abstract val viewModel: VM

    private var loadingOverlay: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewBinding(view)

        AutoFontApplier.applyAutoFontToRootView(view)

        setupUI()
        observeViewModel()
        setupClickListeners()
    }

    protected abstract fun getViewBinding(view: View): VB

    protected open fun setupUI() {}
    protected open fun setupClickListeners() {}

    open fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                showLoading(isLoading)
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    showError(it)
                    viewModel.clearError()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.successMessage.collect { successMessage ->
                successMessage?.let {
                    showSuccess(it)
                    viewModel.clearSuccess()
                }
            }
        }
    }

    /**
     * 🌟 Show / hide loading overlay
     */
    fun showLoading(show: Boolean) {
        if (show) showLoadingOverlay() else hideLoadingOverlay()
    }

    private fun showLoadingOverlay() {
        if (loadingOverlay != null) return

        val overlay = createLoadingOverlay()
        (view as? ViewGroup)?.addView(overlay)

        overlay.alpha = 0f
        overlay.animate()
            .alpha(1f)
            .setDuration(300)
            .start()

        loadingOverlay = overlay
    }

    private fun hideLoadingOverlay() {
        loadingOverlay?.let { overlay ->
            overlay.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    (view as? ViewGroup)?.removeView(overlay)
                    loadingOverlay = null
                }
                .start()
        }
    }

    /**
     * 🌀 Custom loading overlay pakai LoadingPage di tengah
     */
    private fun createLoadingOverlay(): View {
        val sizeInDp = 80
        val sizeInPx = (sizeInDp * resources.displayMetrics.density).toInt()

        return FrameLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(android.graphics.Color.parseColor("#80000000"))

            addView(LoadingPage(requireContext()).apply {
                layoutParams = FrameLayout.LayoutParams(sizeInPx, sizeInPx).apply {
                    gravity = Gravity.CENTER
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
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    protected fun hideKeyboard() {
        val imm = requireContext()
            .getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    protected fun showKeyboard(view: View) {
        val imm = requireContext()
            .getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.showSoftInput(view, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
    }
}
