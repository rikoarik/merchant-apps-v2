package com.solusinegeri.merchant3.presentation.ui.main.utils

import android.content.Context
import com.solusinegeri.merchant3.core.utils.PreferenceManager

/**
 * Manager untuk mengelola balance code & state visibility saldo (per code).
 */
object BalanceCodeManager {

    private const val DEFAULT_BALANCE_CODE = "CLOSEPAY"
    private const val PREF_BALANCE_VISIBLE_PREFIX = "balance_visible_"

    fun initialize(context: Context) {
        PreferenceManager.initialize(context)
        if (!PreferenceManager.hasBalanceCode()) {
            setDefaultBalanceCode()
        }
    }

    private fun setDefaultBalanceCode() {
        PreferenceManager.saveBalanceCode(DEFAULT_BALANCE_CODE)
    }

    fun getCurrentBalanceCode(): String {
        return PreferenceManager.getBalanceCode() ?: DEFAULT_BALANCE_CODE
    }

    fun updateBalanceCode(newBalanceCode: String) {
        PreferenceManager.saveBalanceCode(newBalanceCode)
    }

    fun clearBalanceCode() {
        PreferenceManager.clearBalanceCode()
    }

    fun hasBalanceCode(): Boolean = PreferenceManager.hasBalanceCode()

    fun getMaskedBalanceCode(): String {
        val code = getCurrentBalanceCode()
        return if (code.length > 4) {
            "${code.substring(0, 2)}${"*".repeat(code.length - 4)}${code.substring(code.length - 2)}"
        } else {
            "*".repeat(code.length)
        }
    }

    fun isValidBalanceCode(balanceCode: String): Boolean {
        return balanceCode.isNotBlank() &&
                balanceCode.length in 3..20 &&
                balanceCode.matches(Regex("[A-Z0-9_]+"))
    }

    /* ===== Visibility state per balance code ===== */
    fun getVisibility(balanceCode: String, defaultValue: Boolean = true): Boolean {
        return PreferenceManager.getBoolean(PREF_BALANCE_VISIBLE_PREFIX + balanceCode, defaultValue)
    }

    fun setVisibility(balanceCode: String, visible: Boolean) {
        PreferenceManager.saveBoolean(PREF_BALANCE_VISIBLE_PREFIX + balanceCode, visible)
    }
}
