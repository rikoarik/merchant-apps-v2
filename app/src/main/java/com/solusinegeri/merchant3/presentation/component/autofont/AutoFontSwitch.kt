package com.solusinegeri.merchant3.presentation.component.autofont

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.SwitchCompat
import com.solusinegeri.merchant3.core.utils.FontHelper

/**
 * Switch yang otomatis menggunakan font default sistem
 * Tidak perlu inisialisasi manual
 */
class AutoFontSwitch @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SwitchCompat(context, attrs, defStyleAttr) {
    
    init {
        FontHelper.applyDefaultFont(this)
    }
}
