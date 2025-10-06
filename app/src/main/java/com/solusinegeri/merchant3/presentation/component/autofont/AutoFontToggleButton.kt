package com.solusinegeri.merchant3.presentation.component.autofont

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatToggleButton
import com.solusinegeri.merchant3.core.utils.FontHelper

/**
 * ToggleButton yang otomatis menggunakan font default sistem
 * Tidak perlu inisialisasi manual
 */
class AutoFontToggleButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatToggleButton(context, attrs, defStyleAttr) {
    
    init {
        FontHelper.applyDefaultFont(this)
    }
}
