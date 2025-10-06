package com.solusinegeri.merchant3.config

/**
 * Build configuration yang akan di-generate otomatis oleh Gradle
 * Berdasarkan flavor yang dipilih (dev/prod)
 */
object BuildConfig {
    
    val BASE_URL: String = com.solusinegeri.merchant3.BuildConfig.BASE_URL
    
    val IS_DEBUG: Boolean = com.solusinegeri.merchant3.BuildConfig.IS_DEBUG
    
    val ENABLE_LOGGING: Boolean = com.solusinegeri.merchant3.BuildConfig.ENABLE_LOGGING
    
    val FLAVOR: String = com.solusinegeri.merchant3.BuildConfig.FLAVOR
    
    val APP_NAME: String = "Solusi Negeri Merchant"
    
    val VERSION_NAME: String = "1.0"
    val VERSION_CODE: Int = 1
    
    /**
     * Check apakah ini development build
     */
    fun isDevelopment(): Boolean = FLAVOR == "dev"
    
    /**
     * Check apakah ini production build
     */
    fun isProduction(): Boolean = FLAVOR == "prod"
    
    /**
     * Get environment name untuk display
     */
    fun getEnvironmentName(): String = when (FLAVOR) {
        "dev" -> "Development"
        "prod" -> "Production"
        else -> "Unknown"
    }
}
