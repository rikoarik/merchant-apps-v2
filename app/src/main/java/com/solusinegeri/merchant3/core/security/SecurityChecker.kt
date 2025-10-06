package com.solusinegeri.merchant3.core.security

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*

class SecurityChecker(private val context: Context) {
    
    private val securityManager = SecurityManager(context)
    private var isChecking = false
    
    fun performSecurityCheck(
        onThreatsDetected: (List<SecurityThreat>) -> Unit,
        onNoThreats: () -> Unit = {}
    ) {
        if (isChecking) return
        
        isChecking = true
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val threats = securityManager.performSecurityCheck()
                
                withContext(Dispatchers.Main) {
                    if (threats.isNotEmpty()) {
                        onThreatsDetected(threats)
                    } else {
                        onNoThreats()
                    }
                }
            } catch (e: Exception) {
                // Log error but don't block the app
                android.util.Log.e("SecurityChecker", "Security check failed", e)
            } finally {
                isChecking = false
            }
        }
    }
    
    fun performQuickSecurityCheck(): Boolean {
        return try {
            val threats = securityManager.performSecurityCheck()
            threats.isEmpty()
        } catch (e: Exception) {
            // If security check fails, assume it's safe to continue
            true
        }
    }
}

@Composable
fun SecurityCheck(
    onThreatsDetected: (List<SecurityThreat>) -> Unit,
    onNoThreats: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    DisposableEffect(lifecycleOwner) {
        val securityChecker = SecurityChecker(context)
        
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                securityChecker.performSecurityCheck(
                    onThreatsDetected = onThreatsDetected,
                    onNoThreats = onNoThreats
                )
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

fun Activity.performSecurityCheck(
    onThreatsDetected: (List<SecurityThreat>) -> Unit,
    onNoThreats: () -> Unit = {}
) {
    val securityChecker = SecurityChecker(this)
    securityChecker.performSecurityCheck(onThreatsDetected, onNoThreats)
}
