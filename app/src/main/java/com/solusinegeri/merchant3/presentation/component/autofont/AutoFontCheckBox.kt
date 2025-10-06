package com.solusinegeri.merchant3.presentation.component.autofont

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import com.solusinegeri.merchant3.core.utils.FontHelper

/**
 * CheckBox yang otomatis menggunakan font default sistem
 * Tidak perlu inisialisasi manual
 */
class AutoFontCheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatCheckBox(context, attrs, defStyleAttr) {
    
    init {
        FontHelper.applyDefaultFont(this)
    }
}
