package com.solusinegeri.merchant3.presentation.component.autofont

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textview.MaterialTextView
import com.solusinegeri.merchant3.core.utils.FontHelper

/**
 * MaterialTextView yang otomatis menggunakan font default sistem
 * Tidak perlu inisialisasi manual
 */
class AutoFontMaterialTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialTextView(context, attrs, defStyleAttr) {
    
    init {
        FontHelper.applyDefaultFont(this)
    }
}
