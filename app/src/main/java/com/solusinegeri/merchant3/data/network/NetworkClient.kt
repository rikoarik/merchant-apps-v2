package com.solusinegeri.merchant3.data.network

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.solusinegeri.merchant3.BuildConfig
import com.solusinegeri.merchant3.config.AppConfig
import com.solusinegeri.merchant3.core.auth.TokenAuthenticator
import com.solusinegeri.merchant3.data.interceptor.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Enhanced NetworkClient dengan auto-refresh token mechanism
 * 
 * SECURITY FEATURES:
 * - Automatic token refresh on 401 responses
 * - Encrypted credential storage
 * - Rate limiting for refresh attempts
 * - Comprehensive security logging
 * - Thread-safe operations
 * 
 * Call NetworkClient.initialize(appContext) once (mis. di Application.onCreate()).
 */
object NetworkClient {

    @Volatile
    private var appContext: Context? = null
    private var authenticator: TokenAuthenticator? = null

    fun initialize(context: Context) {
        if (appContext == null) {
            synchronized(this) {
                if (appContext == null) {
                    appContext = context.applicationContext
                    authenticator = TokenAuthenticator(context.applicationContext)
                }
            }
        }
    }

    private fun requireContext(): Context =
        appContext ?: error("NetworkClient not initialized. Call initialize(context) first.")

    private fun requireAuthenticator(): TokenAuthenticator =
        authenticator ?: error("NetworkClient not initialized. Call initialize(context) first.")

    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            // SECURITY: Never log request/response body in production
            // to prevent token exposure in logs
            level = if (BuildConfig.IS_DEBUG) {
                HttpLoggingInterceptor.Level.HEADERS // Only headers in debug
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    private fun buildOkHttp(ctx: Context): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)

        // Add authentication interceptor for automatic token injection
        builder.addInterceptor(AuthInterceptor(ctx))
        
        // Add authenticator for automatic token refresh on 401 responses
        builder.authenticator(requireAuthenticator())

        if (BuildConfig.IS_DEBUG) {
            val chuckerCollector = ChuckerCollector(
                context = ctx,
                showNotification = true,
                retentionPeriod = RetentionManager.Period.ONE_HOUR
            )

            val chuckerInterceptor = ChuckerInterceptor.Builder(ctx)
                .collector(chuckerCollector)
                .maxContentLength(250_000L)
                // SECURITY: Redact Authorization header to prevent token exposure
                .redactHeaders("Authorization", "X-Auth-Token")
                .alwaysReadResponseBody(false)
                .build()

            builder.addInterceptor(chuckerInterceptor)
        }

        builder.addNetworkInterceptor(loggingInterceptor)

        return builder.build()
    }

    private val retrofit: Retrofit by lazy {
        val ctx = requireContext()
        Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL)
            .client(buildOkHttp(ctx))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy { retrofit.create(AuthService::class.java) }

    fun <T> createService(service: Class<T>): T = retrofit.create(service)

    @Deprecated("Use authService instead")
    fun getApiService(): AuthService = authService
    
    /**
     * Get authentication status for debugging
     * 
     * SECURITY: Returns status information without exposing sensitive data
     */
    fun getAuthStatus(): Map<String, Any> {
        return requireAuthenticator().getStatus()
    }
    
    /**
     * Check if auto-refresh is ready
     * 
     * SECURITY: Validates that credentials are available for refresh
     */
    fun isAutoRefreshReady(): Boolean {
        return requireAuthenticator().isReady()
    }
}
