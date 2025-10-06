package com.solusinegeri.merchant3.core.utils

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Switch
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.textfield.TextInputLayout

/**
 * Utility untuk apply auto font ke semua komponen text dalam layout
 * Tidak perlu inisialisasi manual
 */
object AutoFontApplier {
    
    /**
     * Apply auto font ke semua komponen text dalam ViewGroup
     */
    fun applyAutoFontToViewGroup(viewGroup: ViewGroup) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            applyAutoFontToView(child)
        }
    }
    
    /**
     * Apply auto font ke View dan semua child-nya
     */
    fun applyAutoFontToView(view: View) {
        when (view) {
            is TextView -> FontHelper.applyDefaultFont(view)
            is AppCompatTextView -> FontHelper.applyDefaultFont(view)
            is MaterialTextView -> FontHelper.applyDefaultFont(view)
            
            is EditText -> FontHelper.applyDefaultFont(view)
            is AppCompatEditText -> FontHelper.applyDefaultFont(view)
            
            is Button -> FontHelper.applyDefaultFont(view)
            is AppCompatButton -> FontHelper.applyDefaultFont(view)
            is MaterialButton -> FontHelper.applyDefaultFont(view)
            
            is CheckBox -> FontHelper.applyDefaultFont(view)
            is AppCompatCheckBox -> FontHelper.applyDefaultFont(view)
            
            is RadioButton -> FontHelper.applyDefaultFont(view)
            is AppCompatRadioButton -> FontHelper.applyDefaultFont(view)
            
            is Switch -> FontHelper.applyDefaultFont(view)
            is SwitchCompat -> FontHelper.applyDefaultFont(view)
            
            is ToggleButton -> FontHelper.applyDefaultFont(view)
            
            is TextInputLayout -> {
                view.editText?.let { editText ->
                    FontHelper.applyDefaultFont(editText)
                }
            }
            
            is ViewGroup -> applyAutoFontToViewGroup(view)
        }
    }
    
    /**
     * Apply auto font ke root view dan semua child-nya
     */
    fun applyAutoFontToRootView(rootView: View) {
        if (rootView is ViewGroup) {
            applyAutoFontToViewGroup(rootView)
        } else {
            applyAutoFontToView(rootView)
        }
    }
}
