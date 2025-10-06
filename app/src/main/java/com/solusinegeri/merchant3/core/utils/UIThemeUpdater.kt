package com.solusinegeri.merchant3.core.utils

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.solusinegeri.merchant3.R

/**
 * Utility untuk update UI theme berdasarkan dynamic colors dari server
 */
object UIThemeUpdater {
    
    /**
     * Update primary color untuk view
     */
    fun updatePrimaryColor(view: View, context: Context) {
        val primaryColor = DynamicColors.getPrimaryColor(context)
        
        when (view) {
            is Button -> {
                view.setBackgroundColor(primaryColor)
            }
            is TextView -> {
                view.setTextColor(primaryColor)
            }
        }
    }
    
    /**
     * Update secondary color untuk view
     */
    fun updateSecondaryColor(view: View, context: Context) {
        val secondaryColor = DynamicColors.getSecondaryColor(context)
        
        when (view) {
            is Button -> {
                view.setBackgroundColor(secondaryColor)
            }
            is TextView -> {
                view.setTextColor(secondaryColor)
            }
        }
    }
    
    /**
     * Update text color berdasarkan dynamic colors
     */
    fun updateTextColor(textView: TextView, context: Context, isPrimary: Boolean = true) {
        val color = if (isPrimary) {
            DynamicColors.getPrimaryColor(context)
        } else {
            DynamicColors.getSecondaryColor(context)
        }
        textView.setTextColor(color)
    }
    
    /**
     * Update background color berdasarkan dynamic colors
     */
    fun updateBackgroundColor(view: View, context: Context, isPrimary: Boolean = true) {
        val color = if (isPrimary) {
            DynamicColors.getPrimaryColor(context)
        } else {
            DynamicColors.getSecondaryColor(context)
        }
        view.setBackgroundColor(color)
    }
    
    /**
     * Check apakah color string valid
     */
    fun isValidColor(colorString: String?): Boolean {
        return try {
            colorString?.let { Color.parseColor(it) }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get color dengan fallback
     */
    fun getColorWithFallback(
        context: Context,
        colorString: String?,
        fallbackResId: Int
    ): Int {
        return if (isValidColor(colorString)) {
            Color.parseColor(colorString)
        } else {
            ContextCompat.getColor(context, fallbackResId)
        }
    }
}
