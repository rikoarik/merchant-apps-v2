package com.solusinegeri.merchant3.presentation.component.autofont

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton
import com.solusinegeri.merchant3.core.utils.FontHelper

/**
 * RadioButton yang otomatis menggunakan font default sistem
 * Tidak perlu inisialisasi manual
 */
class AutoFontRadioButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatRadioButton(context, attrs, defStyleAttr) {
    
    init {
        FontHelper.applyDefaultFont(this)
    }
}
