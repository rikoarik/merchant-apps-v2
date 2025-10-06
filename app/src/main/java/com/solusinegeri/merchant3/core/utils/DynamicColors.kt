package com.solusinegeri.merchant3.core.utils

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.solusinegeri.merchant3.R
import com.solusinegeri.merchant3.data.responses.CompanyColorData
import androidx.core.graphics.toColorInt

/**
 * Utility untuk handle dynamic colors dari server
 */
object DynamicColors {
    
    private const val PREFS_NAME = "company_colors_prefs"
    private const val KEY_COLOR1 = "company_color1"
    private const val KEY_COLOR2 = "company_color2"
    
    private var companyColors: CompanyColorData? = null
    
    /**
     * Set company colors dari server response
     */
    fun setCompanyColors(context: Context, colors: CompanyColorData?) {
        companyColors = colors
        
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            if (colors != null) {
                colors.color1?.let { putString(KEY_COLOR1, it) }
                colors.color2?.let { putString(KEY_COLOR2, it) }
            } else {
                remove(KEY_COLOR1)
                remove(KEY_COLOR2)
            }
        }
    }
    
    /**
     * Get primary color dari server atau fallback ke default
     */
    fun getPrimaryColor(context: Context): Int {
        return try {
            val colorString = companyColors?.color1 ?: getSavedColor1(context)
            if (!colorString.isNullOrEmpty()) {
                colorString.toColorInt()
            } else {
                ContextCompat.getColor(context, R.color.primary_color)
            }
        } catch (e: Exception) {
            ContextCompat.getColor(context, R.color.primary_color)
        }
    }
    
    /**
     * Get secondary color dari server atau fallback ke default
     */
    fun getSecondaryColor(context: Context): Int {
        return try {
            val colorString = companyColors?.color2 ?: getSavedColor2(context)
            if (!colorString.isNullOrEmpty()) {
                colorString.toColorInt()
            } else {
                ContextCompat.getColor(context, R.color.accent_color)
            }
        } catch (e: Exception) {
            ContextCompat.getColor(context, R.color.accent_color)
        }
    }
    
    /**
     * Get primary color string dari server atau fallback ke default
     */
    fun getPrimaryColorString(context: Context): String {
        return companyColors?.color1 ?: getSavedColor1(context) ?: "#FF2196F3"
    }
    
    /**
     * Get secondary color string dari server atau fallback ke default
     */
    fun getSecondaryColorString(context: Context): String {
        return companyColors?.color2 ?: getSavedColor2(context) ?: "#FFFF9800"
    }
    
    /**
     * Check apakah colors sudah di-set dari server
     */
    fun hasCompanyColors(): Boolean {
        return companyColors != null && 
               !companyColors?.color1.isNullOrEmpty() && 
               !companyColors?.color2.isNullOrEmpty()
    }
    
    /**
     * Clear company colors (untuk logout atau reset)
     */
    fun clearCompanyColors(context: Context) {
        companyColors = null
        
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            remove(KEY_COLOR1)
            remove(KEY_COLOR2)
        }
    }
    
    /**
     * Get saved color1 from SharedPreferences
     */
    private fun getSavedColor1(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_COLOR1, null)
    }
    
    /**
     * Get saved color2 from SharedPreferences
     */
    private fun getSavedColor2(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_COLOR2, null)
    }
}
