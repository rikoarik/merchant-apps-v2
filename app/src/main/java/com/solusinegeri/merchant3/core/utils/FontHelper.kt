package com.solusinegeri.merchant3.core.utils

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView

/**
 * Helper untuk mengelola font secara otomatis
 * Menggunakan font default sistem tanpa perlu inisialisasi manual
 */
object FontHelper {
    
    /**
     * Apply default system font ke TextView
     * Font akan otomatis sesuai dengan sistem
     */
    fun applyDefaultFont(textView: TextView) {
        textView.typeface = null
    }
    
    /**
     * Apply default system font ke TextView dengan size tertentu
     */
    fun applyDefaultFont(textView: TextView, textSize: Float) {
        textView.typeface = null
        textView.textSize = textSize
    }
    
    /**
     * Get default system font
     */
    fun getDefaultFont(context: Context): Typeface? {
        return null
    }
    
    /**
     * Check apakah font tersedia di sistem
     */
    fun isFontAvailable(context: Context, fontName: String): Boolean {
        return try {
            Typeface.createFromAsset(context.assets, "fonts/$fontName.ttf")
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Apply font dari assets jika tersedia, fallback ke default
     */
    fun applyFontFromAssets(textView: TextView, fontName: String) {
        val context = textView.context
        if (isFontAvailable(context, fontName)) {
            try {
                val typeface = Typeface.createFromAsset(context.assets, "fonts/$fontName.ttf")
                textView.typeface = typeface
            } catch (e: Exception) {
                textView.typeface = null
            }
        } else {
            textView.typeface = null
        }
    }
}
