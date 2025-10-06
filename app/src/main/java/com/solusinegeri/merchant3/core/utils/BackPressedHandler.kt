package com.solusinegeri.merchant3.core.utils

import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher

/**
 * Modern back pressed handler utility
 * Compatible with Android 13+ Predictive Back Gesture
 * 
 * This utility replaces the deprecated onBackPressed() method with modern
 * OnBackPressedCallback approach that supports Predictive Back Gesture.
 * 
 * Usage Examples:
 * 
 * 1. Simple finish activity:
 * ```kotlin
 * BackPressedHandler.addFinishCallback(this)
 * ```
 * 
 * 2. Clear input fields first:
 * ```kotlin
 * BackPressedHandler.addInputClearingCallback(
 *     activity = this,
 *     inputFields = listOf(editText1, editText2)
 * ) {
 *     finishAffinity()
 * }
 * ```
 * 
 * 3. Show confirmation dialog:
 * ```kotlin
 * BackPressedHandler.addConfirmationCallback(
 *     activity = this,
 *     message = "Are you sure you want to exit?"
 * ) {
 *     finishAffinity()
 * }
 * ```
 * 
 * 4. Custom logic:
 * ```kotlin
 * BackPressedHandler.addCallback(this) {
 *     // Your custom logic here
 *     if (shouldExit) {
 *         finishAffinity()
 *     }
 * }
 * ```
 */
object BackPressedHandler {
    
    /**
     * Add a back pressed callback with custom logic
     * @param activity The activity to register the callback
     * @param enabled Whether the callback is enabled
     * @param onBackPressed The custom logic to execute
     * @return The callback instance for potential removal
     */
    fun addCallback(
        activity: ComponentActivity,
        enabled: Boolean = true,
        onBackPressed: () -> Unit
    ): OnBackPressedCallback {
        val callback = object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
        
        activity.onBackPressedDispatcher.addCallback(activity, callback)
        return callback
    }
    
    /**
     * Add a back pressed callback that shows confirmation dialog
     * @param activity The activity to register the callback
     * @param message The confirmation message
     * @param onConfirm The action to execute when confirmed
     * @return The callback instance for potential removal
     */
    fun addConfirmationCallback(
        activity: ComponentActivity,
        message: String = "Apakah Anda yakin ingin keluar?",
        onConfirm: () -> Unit
    ): OnBackPressedCallback {
        return addCallback(activity) {
            androidx.appcompat.app.AlertDialog.Builder(activity)
                .setTitle("Konfirmasi")
                .setMessage(message)
                .setPositiveButton("Ya") { _, _ ->
                    onConfirm()
                }
                .setNegativeButton("Tidak", null)
                .show()
        }
    }
    
    /**
     * Add a back pressed callback that clears input field first
     * @param activity The activity to register the callback
     * @param inputFields List of input fields to clear
     * @param onFinalBack The action to execute when all fields are cleared
     * @return The callback instance for potential removal
     */
    fun addInputClearingCallback(
        activity: ComponentActivity,
        inputFields: List<android.widget.EditText>,
        onFinalBack: () -> Unit
    ): OnBackPressedCallback {
        return addCallback(activity) {
            val hasText = inputFields.any { it.text?.isNotEmpty() == true }
            
            if (hasText) {
                inputFields.forEach { it.text?.clear() }
            } else {
                onFinalBack()
            }
        }
    }
    
    /**
     * Add a back pressed callback that finishes the activity
     * @param activity The activity to register the callback
     * @return The callback instance for potential removal
     */
    fun addFinishCallback(activity: ComponentActivity): OnBackPressedCallback {
        return addCallback(activity) {
            activity.finish()
        }
    }
    
    /**
     * Add a back pressed callback that finishes all activities in the task
     * @param activity The activity to register the callback
     * @return The callback instance for potential removal
     */
    fun addFinishAffinityCallback(activity: ComponentActivity): OnBackPressedCallback {
        return addCallback(activity) {
            activity.finishAffinity()
        }
    }
}