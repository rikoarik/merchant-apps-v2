package com.solusinegeri.merchant3.core.security

import android.util.Patterns
import java.util.regex.Pattern

/**
 * Utility class untuk validasi dan sanitization input
 * Meningkatkan keamanan dengan validasi yang ketat
 */
object InputValidator {
    
    // Constants untuk validasi
    private const val MIN_PASSWORD_LENGTH = 8
    private const val MAX_PASSWORD_LENGTH = 128
    private const val MIN_USERNAME_LENGTH = 3
    private const val MAX_USERNAME_LENGTH = 50
    private const val MIN_COMPANY_ID_LENGTH = 3
    private const val MAX_COMPANY_ID_LENGTH = 20
    
    // Pattern untuk validasi username (alphanumeric + underscore + dot)
    private val USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+$")
    
    // Pattern untuk validasi company ID (alphanumeric + underscore + dash)
    private val COMPANY_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$")
    
    // Pattern untuk mendeteksi SQL injection attempts
    private val SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute|script|javascript|vbscript|onload|onerror|onclick)"
    )
    
    // Pattern untuk mendeteksi XSS attempts
    private val XSS_PATTERN = Pattern.compile(
        "(?i)(<script|</script|javascript:|vbscript:|onload=|onerror=|onclick=|<iframe|<object|<embed|<link|<meta|<style)"
    )
    
    /**
     * Validasi dan sanitize username
     */
    fun validateUsername(username: String): ValidationResult {
        val trimmedUsername = username.trim()
        
        // Check empty
        if (trimmedUsername.isEmpty()) {
            return ValidationResult(false, "Username tidak boleh kosong")
        }
        
        // Check length
        if (trimmedUsername.length < MIN_USERNAME_LENGTH) {
            return ValidationResult(false, "Username minimal $MIN_USERNAME_LENGTH karakter")
        }
        
        if (trimmedUsername.length > MAX_USERNAME_LENGTH) {
            return ValidationResult(false, "Username maksimal $MAX_USERNAME_LENGTH karakter")
        }
        
        // Check pattern
        if (!USERNAME_PATTERN.matcher(trimmedUsername).matches()) {
            return ValidationResult(false, "Username hanya boleh mengandung huruf, angka, titik, underscore, dan dash")
        }
        
        // Check for malicious patterns
        if (containsMaliciousPattern(trimmedUsername)) {
            return ValidationResult(false, "Username mengandung karakter yang tidak diizinkan")
        }
        
        return ValidationResult(true, trimmedUsername)
    }
    
    /**
     * Validasi dan sanitize password
     */
    fun validatePassword(password: String): ValidationResult {
        // Check empty
        if (password.isEmpty()) {
            return ValidationResult(false, "Password tidak boleh kosong")
        }
        
        // Check length
        if (password.length < MIN_PASSWORD_LENGTH) {
            return ValidationResult(false, "Password minimal $MIN_PASSWORD_LENGTH karakter")
        }
        
        if (password.length > MAX_PASSWORD_LENGTH) {
            return ValidationResult(false, "Password maksimal $MAX_PASSWORD_LENGTH karakter")
        }
        
        // Check for malicious patterns
        if (containsMaliciousPattern(password)) {
            return ValidationResult(false, "Password mengandung karakter yang tidak diizinkan")
        }
        
        return ValidationResult(true, password)
    }
    
    /**
     * Validasi dan sanitize company ID
     */
    fun validateCompanyId(companyId: String): ValidationResult {
        val trimmedCompanyId = companyId.trim()
        
        // Check empty
        if (trimmedCompanyId.isEmpty()) {
            return ValidationResult(false, "Kode instansi tidak boleh kosong")
        }
        
        // Check length
        if (trimmedCompanyId.length < MIN_COMPANY_ID_LENGTH) {
            return ValidationResult(false, "Kode instansi minimal $MIN_COMPANY_ID_LENGTH karakter")
        }
        
        if (trimmedCompanyId.length > MAX_COMPANY_ID_LENGTH) {
            return ValidationResult(false, "Kode instansi maksimal $MAX_COMPANY_ID_LENGTH karakter")
        }
        
        // Check pattern
        if (!COMPANY_ID_PATTERN.matcher(trimmedCompanyId).matches()) {
            return ValidationResult(false, "Kode instansi hanya boleh mengandung huruf, angka, underscore, dan dash")
        }
        
        // Check for malicious patterns
        if (containsMaliciousPattern(trimmedCompanyId)) {
            return ValidationResult(false, "Kode instansi mengandung karakter yang tidak diizinkan")
        }
        
        return ValidationResult(true, trimmedCompanyId)
    }
    
    /**
     * Validasi email format
     */
    fun validateEmail(email: String): ValidationResult {
        val trimmedEmail = email.trim()
        
        if (trimmedEmail.isEmpty()) {
            return ValidationResult(false, "Email tidak boleh kosong")
        }
        
        if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return ValidationResult(false, "Format email tidak valid")
        }
        
        if (containsMaliciousPattern(trimmedEmail)) {
            return ValidationResult(false, "Email mengandung karakter yang tidak diizinkan")
        }
        
        return ValidationResult(true, trimmedEmail)
    }
    
    /**
     * Cek apakah input mengandung pattern yang berbahaya
     */
    private fun containsMaliciousPattern(input: String): Boolean {
        val lowerInput = input.lowercase()
        
        return SQL_INJECTION_PATTERN.matcher(lowerInput).find() ||
               XSS_PATTERN.matcher(lowerInput).find() ||
               lowerInput.contains("'") ||
               lowerInput.contains("\"") ||
               lowerInput.contains(";") ||
               lowerInput.contains("--") ||
               lowerInput.contains("/*") ||
               lowerInput.contains("*/")
    }
    
    /**
     * Sanitize input dengan menghapus karakter berbahaya
     */
    fun sanitizeInput(input: String): String {
        return input.trim()
            .replace(Regex("[<>\"'%;()&+]"), "")
            .replace(Regex("\\s+"), " ")
    }
    
    /**
     * Cek apakah input mengandung karakter khusus yang berbahaya
     */
    fun containsSpecialCharacters(input: String): Boolean {
        val specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?"
        return input.any { it in specialChars }
    }
    
    /**
     * Data class untuk hasil validasi
     */
    data class ValidationResult(
        val isValid: Boolean,
        val message: String
    )
}
