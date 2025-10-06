package com.solusinegeri.merchant3.presentation.component.autofont

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.solusinegeri.merchant3.core.utils.FontHelper

/**
 * EditText yang otomatis menggunakan font default sistem
 * Tidak perlu inisialisasi manual
 */
class AutoFontEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {
    
    init {
        FontHelper.applyDefaultFont(this)
    }
}
