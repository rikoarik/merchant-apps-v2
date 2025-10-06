package com.solusinegeri.merchant3.presentation.ui.main.fragments.config

import com.solusinegeri.merchant3.R

object MenuConfig {
    
    data class MenuAction(
        val activityClass: Class<*>? = null,
        val requiresData: Boolean = false,
        val dataType: String? = null,
        val extraParams: Map<String, Any>? = null
    )
    
    private val menuActions = mapOf(
        "mutation" to MenuAction(),
        "topup_member" to MenuAction(requiresData = true, dataType = "MerchantData"),
        "card_transaction" to MenuAction(requiresData = true, dataType = "MenuData"),
        "QR_CASH_OUT" to MenuAction(),
        "withdraw_balance" to MenuAction(),
        "trip_tour" to MenuAction(),
        "fnb" to MenuAction(),
        "marketplace" to MenuAction(requiresData = true, dataType = "MarketplaceData"),
        "pos" to MenuAction(),
        "finance_management" to MenuAction(),
        "VIRTUAL_CARD" to MenuAction(),
        "BALANCE_REFUND" to MenuAction(),
        "check_balance" to MenuAction(),
        "invoice" to MenuAction(requiresData = true, dataType = "RetributionData"),
        "retribution" to MenuAction(requiresData = true, dataType = "RetributionData"),
        "fast_trx" to MenuAction(requiresData = true, dataType = "RetributionData"),
        "top_up_va" to MenuAction(extraParams = mapOf("vaType" to "OPEN")),
        "parkir_helper" to MenuAction(),
        "distribusi_bantuan" to MenuAction(),
        "data_transaksi" to MenuAction(),
        "rent" to MenuAction(requiresData = true, dataType = "MerchantData"),
        "transfer_antar_merchant" to MenuAction(),
        "bank_sampah" to MenuAction()
    )
    
    fun getMenuAction(menuId: String): MenuAction? {
        return menuActions.entries.find { (key, _) ->
            menuId.contains(key, ignoreCase = true)
        }?.value
    }
    
    fun getMenuCategory(menuId: String): MenuCategory {
        return when {
            menuId.contains("transfer") || menuId.contains("topup") || menuId.contains("withdraw") -> MenuCategory.FINANCIAL
            menuId.contains("transaction") || menuId.contains("card") || menuId.contains("qr") -> MenuCategory.TRANSACTION
            menuId.contains("marketplace") || menuId.contains("pos") || menuId.contains("fnb") -> MenuCategory.BUSINESS
            menuId.contains("invoice") || menuId.contains("retribution") || menuId.contains("fast") -> MenuCategory.PAYMENT
            menuId.contains("virtual") || menuId.contains("balance") || menuId.contains("check") -> MenuCategory.SPECIAL
            else -> MenuCategory.OTHER
        }
    }
    
    enum class MenuCategory {
        FINANCIAL,
        TRANSACTION,
        BUSINESS,
        PAYMENT,
        SPECIAL,
        OTHER
    }
}
