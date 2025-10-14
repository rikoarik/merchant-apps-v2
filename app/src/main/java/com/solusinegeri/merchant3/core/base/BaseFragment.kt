package com.solusinegeri.merchant3.core.base

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.solusinegeri.merchant3.core.base.BaseViewModel.UiEvent
import com.solusinegeri.merchant3.core.base.BaseViewModel.UiState
import com.solusinegeri.merchant3.core.utils.AutoFontApplier
import com.solusinegeri.merchant3.presentation.component.loading.LoadingPage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel> : Fragment() {

    // --- ViewBinding lifecycle-safe pattern ---
    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding
            ?: throw IllegalStateException("Accessing binding outside of view lifecycle")

    protected abstract val viewModel: VM
    protected abstract fun getViewBinding(view: View): VB

    private var loadingOverlay: View? = null
    private var observeJob: Job? = null

    // ----------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = getViewBinding(view)
        AutoFontApplier.applyAutoFontToRootView(view)

        setupUI()
        setupClickListeners()
        observeViewModel() // default observers (safe by repeatOnLifecycle di bawah)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // pastikan overlay dilepas agar tidak “nyantol”
        hideLoadingOverlay(immediate = true)
        loadingOverlay = null

        // hentikan koleksi flow yang mungkin masih aktif
        observeJob?.cancel()
        observeJob = null

        _binding = null
    }

    // ----------------------------------------------------
    // Hooks
    // ----------------------------------------------------
    protected open fun setupUI() {}
    protected open fun setupClickListeners() {}

    /**
     * Observe bawaan: loading | error | success
     * Dikoleksi mengikuti lifecycle view (STARTED..STOPPED)
     */
    open fun observeViewModel() {
        observeJob = viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            UiState.Idle -> showLoading(false)
                            UiState.Loading -> showLoading(true)
                            is UiState.Error -> Unit // handled via error flow
                            is UiState.Success -> Unit // handled via success flow
                        }
                        onUiStateChanged(state)
                    }
                }
                launch {
                    viewModel.errorMessage.collect { msg ->
                        msg?.let {
                            showError(it)
                            viewModel.clearError()
                        }
                    }
                }
                launch {
                    viewModel.successMessage.collect { msg ->
                        msg?.let {
                            showSuccess(it)
                            viewModel.clearSuccess()
                        }
                    }
                }
                launch {
                    viewModel.events.collect { handleEvent(it) }
                }
            }
        }
    }

    // ----------------------------------------------------
    // Loading Overlay
    // ----------------------------------------------------
    open fun showLoading(show: Boolean) {
        if (show) showLoadingOverlay() else hideLoadingOverlay()
    }

    private fun showLoadingOverlay() {
        val rootGroup = (binding.root as? ViewGroup)
            ?: (view as? ViewGroup)
            ?: return // tidak ada container yang cocok

        if (loadingOverlay != null) {
            // jika sudah ada, pastikan terlihat
            if (loadingOverlay?.parent == null) rootGroup.addView(loadingOverlay)
            loadingOverlay?.animate()?.alpha(1f)?.setDuration(180)?.start()
            return
        }

        val overlay = createLoadingOverlay(rootGroup)
        overlay.alpha = 0f
        rootGroup.addView(overlay)
        overlay.animate()
            .alpha(1f)
            .setDuration(180)
            .start()

        loadingOverlay = overlay
    }

    private fun hideLoadingOverlay(immediate: Boolean = false) {
        val overlay = loadingOverlay ?: return
        val parent = overlay.parent as? ViewGroup ?: return

        if (immediate) {
            parent.removeView(overlay)
            return
        }

        overlay.animate()
            .alpha(0f)
            .setDuration(180)
            .withEndAction {
                parent.removeView(overlay)
            }
            .start()
    }

    /**
     * Loading overlay sederhana: semi transparan + LoadingPage di tengah.
     */
    private fun createLoadingOverlay(container: ViewGroup): View {
        val sizeDp = 80
        val sizePx = (sizeDp * resources.displayMetrics.density).toInt()

        return FrameLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(0x80000000.toInt()) // ~50% hitam

            addView(LoadingPage(requireContext()).apply {
                layoutParams = FrameLayout.LayoutParams(sizePx, sizePx).apply {
                    gravity = Gravity.CENTER
                }
            })
            isClickable = true   // blok sentuhan ke bawah
            isFocusable = true
        }
    }

    // ----------------------------------------------------
    // Feedback helpers
    // ----------------------------------------------------
    protected open fun showError(message: String) {
        // gunakan viewLifecycleOwner agar aman terhadap lifecycle view
        _binding?.root?.let { root ->
            Snackbar.make(root, message, Snackbar.LENGTH_LONG)
                .setAction("Tutup") {}
                .show()
        } ?: run {
            // fallback jika view sudah hancur
            if (isAdded) Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    protected open fun showSuccess(message: String) {
        _binding?.root?.let { root ->
            Snackbar.make(root, message, Snackbar.LENGTH_SHORT)
                .setAction("OK") {}
                .show()
        } ?: run {
            if (isAdded) Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    protected fun showToast(message: String) {
        if (isAdded) Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    // ----------------------------------------------------
    // Keyboard utils
    // ----------------------------------------------------
    protected fun hideKeyboard() {
        val token = view?.windowToken ?: return
        val imm = requireContext()
            .getSystemService(android.content.Context.INPUT_METHOD_SERVICE)
                as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(token, 0)
    }

    protected fun showKeyboard(target: View) {
        val imm = requireContext()
            .getSystemService(android.content.Context.INPUT_METHOD_SERVICE)
                as android.view.inputmethod.InputMethodManager
        target.requestFocus()
        imm.showSoftInput(target, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
    }

    protected open fun onUiStateChanged(state: UiState) = Unit

    protected open fun handleEvent(event: UiEvent) {
        when (event) {
            is UiEvent.ShowSnackbar -> showSuccess(event.message)
            else -> Unit
        }
    }

    protected fun <T> collectFlow(
        flow: Flow<T>,
        minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
        collector: suspend (T) -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(minActiveState) {
                flow.collect(collector)
            }
        }
    }
}
