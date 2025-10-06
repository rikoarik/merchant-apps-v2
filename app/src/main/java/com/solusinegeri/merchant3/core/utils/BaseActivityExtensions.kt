package com.solusinegeri.merchant3.core.utils

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar

/**
 * Extension functions untuk BaseActivity dan komponen-komponennya
 */

/**
 * Hide keyboard dengan animasi smooth
 */
fun FragmentActivity.hideKeyboard() {
    val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}

/**
 * Show keyboard dengan animasi smooth
 */
fun FragmentActivity.showKeyboard(view: View) {
    val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

/**
 * Show snackbar dengan custom styling
 */
fun View.showSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionText: String? = null,
    action: (() -> Unit)? = null
) {
    val snackbar = Snackbar.make(this, message, duration)
    
    actionText?.let { text ->
        snackbar.setAction(text) {
            action?.invoke()
        }
    }
    
    snackbar.show()
}

/**
 * Show error snackbar dengan styling khusus
 */
fun View.showErrorSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    actionText: String = "Tutup",
    action: (() -> Unit)? = null
) {
    showSnackbar(message, duration, actionText, action)
}

/**
 * Show success snackbar dengan styling khusus
 */
fun View.showSuccessSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionText: String = "OK",
    action: (() -> Unit)? = null
) {
    showSnackbar(message, duration, actionText, action)
}

/**
 * Extension untuk Fragment untuk hide keyboard
 */
fun Fragment.hideKeyboard() {
    activity?.hideKeyboard()
}

/**
 * Extension untuk Fragment untuk show keyboard
 */
fun Fragment.showKeyboard(view: View) {
    activity?.showKeyboard(view)
}

/**
 * Extension untuk Fragment untuk show snackbar
 */
fun Fragment.showSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionText: String? = null,
    action: (() -> Unit)? = null
) {
    view?.showSnackbar(message, duration, actionText, action)
}

/**
 * Extension untuk Fragment untuk show error snackbar
 */
fun Fragment.showErrorSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    actionText: String = "Tutup",
    action: (() -> Unit)? = null
) {
    view?.showErrorSnackbar(message, duration, actionText, action)
}

/**
 * Extension untuk Fragment untuk show success snackbar
 */
fun Fragment.showSuccessSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionText: String = "OK",
    action: (() -> Unit)? = null
) {
    view?.showSuccessSnackbar(message, duration, actionText, action)
}
