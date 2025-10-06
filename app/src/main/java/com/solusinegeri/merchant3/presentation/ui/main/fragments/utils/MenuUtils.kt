package com.solusinegeri.merchant3.presentation.ui.main.fragments.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import com.solusinegeri.merchant3.R
import com.solusinegeri.merchant3.data.responses.MenuData

object MenuUtils {


    private val DEFAULT_HIDDEN_MENUS = setOf(
        "qris",
        "generate_barcode",
        "scan_barcode"
    )

    fun filterVisibleMenus(
        menuList: List<MenuData>,
        extraHidden: Set<String> = emptySet()
    ): List<MenuData> {
        val hidden = (DEFAULT_HIDDEN_MENUS + extraHidden).map { it.lowercase() }.toSet()
        return menuList.filter { it.isActive && (it.name?.lowercase() !in hidden) }
    }

    /** (Opsional) tetap sediakan versi lama jika masih dipakai */
    fun filterActiveMenus(menuList: List<MenuData>): List<MenuData> {
        return menuList.filter { it.isActive }
    }

    fun formatMenuName(menuData: MenuData): String {
        return menuData.display ?: menuData.displayEn ?: formatMenuNameFromSnakeCase(menuData.name ?: "Menu")
    }
    
    private fun formatMenuNameFromSnakeCase(name: String): String {
        return name.split("_")
            .joinToString(" ") { word ->
                word.lowercase().replaceFirstChar { 
                    if (it.isLowerCase()) it.titlecase() else it.toString() 
                }
            }
    }
    
    fun animateMenuSection(view: View) {
        view.apply {
            alpha = 0f
            translationY = 50f
            
            val fadeIn = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f)
            val slideUp = ObjectAnimator.ofFloat(this, "translationY", 50f, 0f)
            
            val animatorSet = AnimatorSet()
            animatorSet.playTogether(fadeIn, slideUp)
            animatorSet.duration = 400
            animatorSet.start()
        }
    }
    
    fun animateRecyclerView(view: View) {
        view.apply {
            alpha = 0f
            
            val fadeIn = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f)
            fadeIn.duration = 300
            fadeIn.start()
        }
    }
    
    fun animateMenuItem(view: View, position: Int) {
        view.apply {
            alpha = 0f
            scaleX = 0.8f
            scaleY = 0.8f
            
            val delay = position * 100L
            
            val fadeIn = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f)
            val scaleX = ObjectAnimator.ofFloat(this, "scaleX", 0.8f, 1f)
            val scaleY = ObjectAnimator.ofFloat(this, "scaleY", 0.8f, 1f)
            
            val animatorSet = AnimatorSet()
            animatorSet.playTogether(fadeIn, scaleX, scaleY)
            animatorSet.duration = 300
            animatorSet.startDelay = delay
            animatorSet.start()
        }
    }
    
    fun animateClick(view: View) {
        view.apply {
            val scaleDown = ObjectAnimator.ofFloat(this, "scaleX", 1f, 0.95f)
            val scaleDownY = ObjectAnimator.ofFloat(this, "scaleY", 1f, 0.95f)
            val scaleUp = ObjectAnimator.ofFloat(this, "scaleX", 0.95f, 1f)
            val scaleUpY = ObjectAnimator.ofFloat(this, "scaleY", 0.95f, 1f)
            
            val scaleDownSet = AnimatorSet()
            scaleDownSet.playTogether(scaleDown, scaleDownY)
            scaleDownSet.duration = 100
            
            val scaleUpSet = AnimatorSet()
            scaleUpSet.playTogether(scaleUp, scaleUpY)
            scaleUpSet.duration = 100
            
            val animatorSet = AnimatorSet()
            animatorSet.playSequentially(scaleDownSet, scaleUpSet)
            animatorSet.start()
        }
    }
    
    fun getIconResource(context: Context, iconName: String): Int {
        return when (iconName.lowercase()) {
            "mutation" -> R.drawable.ic_mutation_circle
            "scan_barcode" -> R.drawable.ic_qr_cash_out
            "generate_barcode" -> R.drawable.ic_generate
            "topup_member" -> R.drawable.ic_topup
            "marketplace" -> R.drawable.ic_marketplace
            "rent" -> R.drawable.ic_rent
            "pos" -> R.drawable.ic_pos
            "fnb" -> R.drawable.ic_fnb
            "check_balance" -> R.drawable.ic_check_balance_circle
            "invoice" -> R.drawable.ic_menu_invoice_circle
            "card_transaction" -> R.drawable.ic_transaction_card
            "withdraw_balance" -> R.drawable.ic_withdraw
            "fast_trx" -> R.drawable.check_balance_1
            "top_up_va" -> R.drawable.ic_top_up_va
            "finance_management" -> R.drawable.ic_finance_management_circle
            "data_transaksi" -> R.drawable.ic_data_transaksi
            "transfer_antar_merchant" -> R.drawable.ic_transfer_merchant_to_merchant
            "bank_sampah" -> R.drawable.ic_waste_bank
            "parkir_helper" -> R.drawable.ic_parkir_helper
            "distribusi_bantuan" -> R.drawable.ic_disbursement_of_funds_via_admin
            "trip_tour" -> R.drawable.ic_trip
            "qris" -> R.drawable.ic_scan_qr_cpm
            "offline_transaction" -> R.drawable.ic_offline_mode
            "retribution" -> R.drawable.ic_retribution_circle
            else -> R.drawable.ic_category_new
        }
    }

}
