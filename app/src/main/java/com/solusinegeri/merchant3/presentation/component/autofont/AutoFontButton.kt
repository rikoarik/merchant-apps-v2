package com.solusinegeri.merchant3.presentation.component.autofont

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton
import com.solusinegeri.merchant3.core.utils.FontHelper

/**
 * MaterialButton yang otomatis menggunakan font default sistem
 * Tidak perlu inisialisasi manual
 */
class AutoFontButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialButton(context, attrs, defStyleAttr) {
    
    init {
        FontHelper.applyDefaultFont(this)
    }
}
