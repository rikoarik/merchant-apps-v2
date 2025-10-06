package com.solusinegeri.merchant3.core.config

import android.content.Context
import android.content.SharedPreferences
import com.solusinegeri.merchant3.R

/**
 * Konfigurasi untuk BaseActivity dan komponen-komponennya
 * Memungkinkan kustomisasi tanpa mengubah kode utama
 */
object BaseActivityConfig {
    
    const val LOADING_ANIMATION_DURATION = 200L
    const val LOADING_FADE_DURATION = 200L
    
    const val SMART_BUTTON_SUCCESS_DURATION = 2000L
    const val SMART_BUTTON_ERROR_DURATION = 3000L
    const val SMART_BUTTON_SPINNER_DURATION = 1000L
    const val SMART_BUTTON_SWEEP_DURATION = 1500L
    
    const val SNACKBAR_ERROR_DURATION = 3000L
    const val SNACKBAR_SUCCESS_DURATION = 1500L
    const val SNACKBAR_INFO_DURATION = 2000L
    
    const val LOADING_PAGE_ROTATION_DURATION = 2000L
    const val LOADING_PAGE_SWEEP_DURATION = 1500L
    const val LOADING_PAGE_SCALE_DURATION = 1000L
    
    val PRIMARY_COLOR = R.color.primary_color
    val SUCCESS_COLOR = R.color.success
    val ERROR_COLOR = R.color.error
    val WARNING_COLOR = R.color.warning
    val INFO_COLOR = R.color.info
    
    const val DEFAULT_LOADING_MESSAGE = "Loading..."
    const val DEFAULT_SUCCESS_MESSAGE = "Operasi berhasil!"
    const val DEFAULT_ERROR_MESSAGE = "Terjadi kesalahan!"
    const val DEFAULT_ERROR_TITLE = "Error"
    const val DEFAULT_SUCCESS_TITLE = "Success"
    
    const val ENABLE_ANIMATIONS = true
    const val ENABLE_LOADING_OVERLAY = true
    const val ENABLE_SMART_BUTTON_AUTO_DISABLE = true
    const val ENABLE_SNACKBAR_AUTO_DISMISS = true
    
    const val ENABLE_DEBUG_LOGS = false
    const val ENABLE_PERFORMANCE_LOGS = false
}

/**
 * SharedPreferences untuk menyimpan konfigurasi BaseActivity
 */
class BaseActivityPreferences(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "base_activity_config",
        Context.MODE_PRIVATE
    )
    
    var enableLoadingOverlay: Boolean
        get() = prefs.getBoolean("enable_loading_overlay", BaseActivityConfig.ENABLE_LOADING_OVERLAY)
        set(value) = prefs.edit().putBoolean("enable_loading_overlay", value).apply()
    
    var enableAnimations: Boolean
        get() = prefs.getBoolean("enable_animations", BaseActivityConfig.ENABLE_ANIMATIONS)
        set(value) = prefs.edit().putBoolean("enable_animations", value).apply()
    
    var enableSmartButtonAutoDisable: Boolean
        get() = prefs.getBoolean("enable_smart_button_auto_disable", BaseActivityConfig.ENABLE_SMART_BUTTON_AUTO_DISABLE)
        set(value) = prefs.edit().putBoolean("enable_smart_button_auto_disable", value).apply()
    
    var enableSnackbarAutoDismiss: Boolean
        get() = prefs.getBoolean("enable_snackbar_auto_dismiss", BaseActivityConfig.ENABLE_SNACKBAR_AUTO_DISMISS)
        set(value) = prefs.edit().putBoolean("enable_snackbar_auto_dismiss", value).apply()
    
    var enableDebugLogs: Boolean
        get() = prefs.getBoolean("enable_debug_logs", BaseActivityConfig.ENABLE_DEBUG_LOGS)
        set(value) = prefs.edit().putBoolean("enable_debug_logs", value).apply()
    
    var enablePerformanceLogs: Boolean
        get() = prefs.getBoolean("enable_performance_logs", BaseActivityConfig.ENABLE_PERFORMANCE_LOGS)
        set(value) = prefs.edit().putBoolean("enable_performance_logs", value).apply()
    
    var loadingAnimationDuration: Long
        get() = prefs.getLong("loading_animation_duration", BaseActivityConfig.LOADING_ANIMATION_DURATION)
        set(value) = prefs.edit().putLong("loading_animation_duration", value).apply()
    
    var smartButtonSuccessDuration: Long
        get() = prefs.getLong("smart_button_success_duration", BaseActivityConfig.SMART_BUTTON_SUCCESS_DURATION)
        set(value) = prefs.edit().putLong("smart_button_success_duration", value).apply()
    
    var smartButtonErrorDuration: Long
        get() = prefs.getLong("smart_button_error_duration", BaseActivityConfig.SMART_BUTTON_ERROR_DURATION)
        set(value) = prefs.edit().putLong("smart_button_error_duration", value).apply()
    
    fun resetToDefault() {
        prefs.edit().clear().apply()
    }
}
