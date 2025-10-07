package com.solusinegeri.merchant3.presentation.ui.main.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.widget.TextView
import com.solusinegeri.merchant3.data.responses.BalanceData
import java.text.NumberFormat
import java.util.Locale

object BalanceUtils {

    // Tambahkan spasi di depan "Rp"
    private const val HIDDEN_PLACEHOLDER = " Rp ••••••••"

    /** ===== Formatter ===== */
    fun formatBalance(balanceData: BalanceData): String {
        val balance = balanceData.balance ?: 0.0
        val currency = (balanceData.currency ?: "IDR").uppercase()

        return when (currency) {
            "IDR", "RUPIAH" -> {
                val raw = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(balance)
                val fixed = raw.replaceFirst(Regex("^Rp(?:\\s|\\u00A0)*"), " Rp ")
                if (fixed.startsWith(" Rp ")) fixed else " Rp $fixed"
            }
            "USD" -> {
                val raw = NumberFormat.getCurrencyInstance(Locale.US).format(balance)
                " $raw"
            }
            else -> {
                val num = NumberFormat.getNumberInstance(Locale.getDefault()).format(balance)
                " $currency $num"
            }
        }
    }


    fun formatBalanceHidden(): String = HIDDEN_PLACEHOLDER

    fun formatBalanceWithStatus(balanceData: BalanceData): String {
        val text = formatBalance(balanceData)
        return if (balanceData.isBlocked == true) "$text (Blocked)" else text
    }

    /** ===== State Management (per balanceCode) ===== */
    fun isBalanceCurrentlyVisible(balanceCode: String): Boolean {
        return BalanceCodeManager.getVisibility(balanceCode, true)
    }

    fun setBalanceVisibility(balanceCode: String, isVisible: Boolean) {
        BalanceCodeManager.setVisibility(balanceCode, isVisible)
    }

    /** ===== UI Helper ===== */
    fun toggleBalanceVisibility(
        balanceTextView: TextView,
        toggleTextView: TextView,
        balanceData: BalanceData?,
        balanceCode: String
    ) {
        val newVisible = !isBalanceCurrentlyVisible(balanceCode)
        setBalanceVisibility(balanceCode, newVisible)

        if (newVisible) {
            showBalance(balanceTextView, toggleTextView, balanceData)
        } else {
            hideBalance(balanceTextView, toggleTextView)
        }
    }

    fun applyInitialState(
        balanceTextView: TextView,
        toggleTextView: TextView,
        balanceData: BalanceData?,
        balanceCode: String
    ) {
        val visible = isBalanceCurrentlyVisible(balanceCode)
        if (visible) showBalance(balanceTextView, toggleTextView, balanceData)
        else hideBalance(balanceTextView, toggleTextView)
    }

    private fun showBalance(
        balanceTextView: TextView,
        toggleTextView: TextView,
        balanceData: BalanceData?
    ) {
        val text = balanceData?.let { formatBalanceWithStatus(it) } ?: " Rp 0"
        animateTextChange(balanceTextView, text)
        toggleTextView.text = "Hide"
    }

    private fun hideBalance(
        balanceTextView: TextView,
        toggleTextView: TextView
    ) {
        animateTextChange(balanceTextView, formatBalanceHidden())
        toggleTextView.text = "Show"
    }

    private fun animateTextChange(textView: TextView, newText: String) {
        val fadeOut = ObjectAnimator.ofFloat(textView, "alpha", 1f, 0f).apply { ObjectAnimator.setDuration = 150 }
        val fadeIn = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f).apply { ObjectAnimator.setDuration = 150 }

        fadeOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                textView.text = newText
                fadeIn.start()
            }
        })
        fadeOut.start()
    }
}
