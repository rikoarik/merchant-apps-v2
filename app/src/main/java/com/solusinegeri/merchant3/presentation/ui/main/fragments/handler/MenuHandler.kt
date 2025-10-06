package com.solusinegeri.merchant3.presentation.ui.main.fragments.handler

import android.content.Context
import android.widget.Toast
import com.solusinegeri.merchant3.data.responses.MenuData
import com.solusinegeri.merchant3.presentation.ui.main.fragments.utils.MenuUtils

class MenuHandler(private val context: Context) {

    fun handleMenuClick(menuData: MenuData) {
        val menuName = MenuUtils.formatMenuName(menuData)

        when {
            menuData.name?.contains("mutation") == true -> {
                navigateToMutation()
            }
            menuData.name?.contains("transfer_antar_merchant") == true -> {
                navigateToTransferAntarMerchant()
            }
            menuData.name?.contains("transfer_ke_bank") == true -> {
                navigateToTransferKeBank()
            }
            menuData.name?.contains("top_up") == true -> {
                navigateToTopUp()
            }
            menuData.name?.contains("withdraw") == true -> {
                navigateToWithdraw()
            }
            menuData.name?.contains("payment") == true -> {
                navigateToPayment()
            }
            menuData.name?.contains("history") == true -> {
                navigateToHistory()
            }
            menuData.name?.contains("profile") == true -> {
                navigateToProfile()
            }
            menuData.name?.contains("settings") == true -> {
                navigateToSettings()
            }
            else -> {
                showComingSoon(menuName)
            }
        }
    }

    private fun navigateToMutation() {
        Toast.makeText(context, "Navigating to Mutation", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToTransferAntarMerchant() {
        Toast.makeText(context, "Navigating to Transfer Antar Merchant", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToTransferKeBank() {
        Toast.makeText(context, "Navigating to Transfer ke Bank", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToTopUp() {
        Toast.makeText(context, "Navigating to Top Up", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToWithdraw() {
        Toast.makeText(context, "Navigating to Withdraw", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToPayment() {
        Toast.makeText(context, "Navigating to Payment", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToHistory() {
        Toast.makeText(context, "Navigating to History", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToProfile() {
        Toast.makeText(context, "Navigating to Profile", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToSettings() {
        Toast.makeText(context, "Navigating to Settings", Toast.LENGTH_SHORT).show()
    }

    private fun showComingSoon(menuName: String) {
        Toast.makeText(context, "$menuName - Coming Soon", Toast.LENGTH_SHORT).show()
    }
}