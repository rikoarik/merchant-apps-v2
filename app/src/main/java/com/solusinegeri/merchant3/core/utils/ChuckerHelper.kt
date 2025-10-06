package com.solusinegeri.merchant3.core.utils

import android.content.Context
import com.chuckerteam.chucker.api.Chucker

/**
 * Helper class untuk Chucker operations
 */
object ChuckerHelper {
    
    /**
     * Launch Chucker UI untuk melihat network requests
     */
    fun launchChucker(context: Context) {
        Chucker.getLaunchIntent(context)?.let { intent ->
            context.startActivity(intent)
        }
    }
    
    /**
     * Check apakah Chucker tersedia (hanya di debug build)
     */
    fun isChuckerAvailable(): Boolean {
        return try {
            Class.forName("com.chuckerteam.chucker.api.Chucker")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }
}
