package com.solusinegeri.merchant3.core.security

import android.content.Context
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 * Security logger untuk monitoring aktivitas keamanan
 * Mencatat event-event penting untuk audit dan monitoring
 */
object SecurityLogger {
    
    private const val TAG = "SecurityLogger"
    private const val SECURITY_LOG_PREFS = "security_logs"
    
    // Event types
    const val EVENT_LOGIN_SUCCESS = "LOGIN_SUCCESS"
    const val EVENT_LOGIN_FAILED = "LOGIN_FAILED"
    const val EVENT_LOGIN_BLOCKED = "LOGIN_BLOCKED"
    const val EVENT_SESSION_EXPIRED = "SESSION_EXPIRED"
    const val EVENT_INVALID_INPUT = "INVALID_INPUT"
    const val EVENT_SUSPICIOUS_ACTIVITY = "SUSPICIOUS_ACTIVITY"
    const val EVENT_TOKEN_REFRESH = "TOKEN_REFRESH"
    const val EVENT_LOGOUT = "LOGOUT"
    const val EVENT_DATA_ACCESS = "DATA_ACCESS"
    const val EVENT_SECURITY_VIOLATION = "SECURITY_VIOLATION"
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    
    /**
     * Log security event
     */
    fun logSecurityEvent(
        context: Context,
        eventType: String,
        details: String,
        userId: String? = null,
        severity: SecuritySeverity = SecuritySeverity.INFO
    ) {
        val timestamp = dateFormat.format(Date())
        val logEntry = SecurityLogEntry(
            timestamp = timestamp,
            eventType = eventType,
            details = details,
            userId = userId,
            severity = severity,
            deviceInfo = getDeviceInfo(context)
        )
        
        // Log ke console untuk debugging
        when (severity) {
            SecuritySeverity.CRITICAL -> Log.e(TAG, "CRITICAL: $eventType - $details")
            SecuritySeverity.HIGH -> Log.w(TAG, "HIGH: $eventType - $details")
            SecuritySeverity.MEDIUM -> Log.i(TAG, "MEDIUM: $eventType - $details")
            SecuritySeverity.LOW -> Log.d(TAG, "LOW: $eventType - $details")
            SecuritySeverity.INFO -> Log.i(TAG, "INFO: $eventType - $details")
        }
        
        // Simpan ke local storage untuk audit
        saveLogEntry(context, logEntry)
    }
    
    /**
     * Log login attempt
     */
    fun logLoginAttempt(
        context: Context,
        username: String,
        companyId: String,
        success: Boolean,
        errorMessage: String? = null
    ) {
        val eventType = if (success) EVENT_LOGIN_SUCCESS else EVENT_LOGIN_FAILED
        val details = buildString {
            append("Login attempt - Username: ${maskSensitiveData(username)}, ")
            append("CompanyID: ${maskSensitiveData(companyId)}, ")
            append("Success: $success")
            if (errorMessage != null) {
                append(", Error: $errorMessage")
            }
        }
        
        val severity = if (success) SecuritySeverity.INFO else SecuritySeverity.MEDIUM
        
        logSecurityEvent(context, eventType, details, severity = severity)
    }
    
    /**
     * Log blocked login attempt
     */
    fun logBlockedLoginAttempt(
        context: Context,
        username: String,
        companyId: String,
        reason: String
    ) {
        val details = buildString {
            append("Blocked login attempt - Username: ${maskSensitiveData(username)}, ")
            append("CompanyID: ${maskSensitiveData(companyId)}, ")
            append("Reason: $reason")
        }
        
        logSecurityEvent(
            context,
            EVENT_LOGIN_BLOCKED,
            details,
            severity = SecuritySeverity.HIGH
        )
    }
    
    /**
     * Log invalid input attempt
     */
    fun logInvalidInput(
        context: Context,
        inputType: String,
        input: String,
        validationError: String
    ) {
        val details = buildString {
            append("Invalid input - Type: $inputType, ")
            append("Input: ${maskSensitiveData(input)}, ")
            append("Error: $validationError")
        }
        
        logSecurityEvent(
            context,
            EVENT_INVALID_INPUT,
            details,
            severity = SecuritySeverity.LOW
        )
    }
    
    /**
     * Log suspicious activity
     */
    fun logSuspiciousActivity(
        context: Context,
        activity: String,
        details: String,
        userId: String? = null
    ) {
        val logDetails = buildString {
            append("Suspicious activity - Activity: $activity, ")
            append("Details: $details")
        }
        
        logSecurityEvent(
            context,
            EVENT_SUSPICIOUS_ACTIVITY,
            logDetails,
            userId,
            SecuritySeverity.HIGH
        )
    }
    
    /**
     * Log session events
     */
    fun logSessionEvent(
        context: Context,
        eventType: String,
        userId: String? = null,
        details: String = ""
    ) {
        logSecurityEvent(
            context,
            eventType,
            details,
            userId,
            SecuritySeverity.INFO
        )
    }
    
    /**
     * Mask sensitive data untuk logging
     */
    private fun maskSensitiveData(data: String): String {
        return when {
            data.length <= 2 -> "*".repeat(data.length)
            data.length <= 4 -> "${data.first()}${"*".repeat(data.length - 2)}${data.last()}"
            else -> "${data.take(2)}${"*".repeat(data.length - 4)}${data.takeLast(2)}"
        }
    }
    
    /**
     * Get device info untuk logging
     */
    private fun getDeviceInfo(context: Context): String {
        return buildString {
            append("Android ${android.os.Build.VERSION.RELEASE}, ")
            append("Model: ${android.os.Build.MODEL}, ")
            append("Brand: ${android.os.Build.BRAND}")
        }
    }
    
    /**
     * Save log entry ke local storage
     */
    private fun saveLogEntry(context: Context, logEntry: SecurityLogEntry) {
        val prefs = context.getSharedPreferences(SECURITY_LOG_PREFS, Context.MODE_PRIVATE)
        val logs = prefs.getStringSet("security_logs", mutableSetOf()) ?: mutableSetOf()
        
        // Tambahkan log entry baru
        logs.add(logEntry.toString())
        
        // Batasi jumlah log (max 1000 entries)
        if (logs.size > 1000) {
            val sortedLogs = logs.sorted()
            logs.clear()
            logs.addAll(sortedLogs.takeLast(1000))
        }
        
        prefs.edit()
            .putStringSet("security_logs", logs)
            .apply()
    }
    
    /**
     * Get security logs untuk audit
     */
    fun getSecurityLogs(context: Context): List<SecurityLogEntry> {
        val prefs = context.getSharedPreferences(SECURITY_LOG_PREFS, Context.MODE_PRIVATE)
        val logs = prefs.getStringSet("security_logs", emptySet()) ?: emptySet()
        
        return logs.mapNotNull { logString ->
            try {
                SecurityLogEntry.fromString(logString)
            } catch (e: Exception) {
                null
            }
        }.sortedByDescending { it.timestamp }
    }
    
    /**
     * Clear old security logs
     */
    fun clearOldLogs(context: Context, daysToKeep: Int = 30) {
        val prefs = context.getSharedPreferences(SECURITY_LOG_PREFS, Context.MODE_PRIVATE)
        val logs = prefs.getStringSet("security_logs", emptySet()) ?: emptySet()
        
        val cutoffDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -daysToKeep)
        }.time
        
        val filteredLogs = logs.filter { logString ->
            try {
                val logEntry = SecurityLogEntry.fromString(logString)
                val logDate = dateFormat.parse(logEntry.timestamp)
                logDate?.after(cutoffDate) ?: false
            } catch (e: Exception) {
                false
            }
        }.toSet()
        
        prefs.edit()
            .putStringSet("security_logs", filteredLogs)
            .apply()
    }
    
    /**
     * Data class untuk security log entry
     */
    data class SecurityLogEntry(
        val timestamp: String,
        val eventType: String,
        val details: String,
        val userId: String?,
        val severity: SecuritySeverity,
        val deviceInfo: String
    ) {
        override fun toString(): String {
            return "$timestamp|$eventType|$details|${userId ?: "N/A"}|$severity|$deviceInfo"
        }
        
        companion object {
            fun fromString(logString: String): SecurityLogEntry {
                val parts = logString.split("|")
                return SecurityLogEntry(
                    timestamp = parts[0],
                    eventType = parts[1],
                    details = parts[2],
                    userId = if (parts[3] == "N/A") null else parts[3],
                    severity = SecuritySeverity.valueOf(parts[4]),
                    deviceInfo = parts[5]
                )
            }
        }
    }
    
    /**
     * Enum untuk severity level
     */
    enum class SecuritySeverity {
        CRITICAL, HIGH, MEDIUM, LOW, INFO
    }
}
