package com.solusinegeri.merchant3.data.interceptor

import android.content.Context
import com.solusinegeri.merchant3.core.security.SecureStorage
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor untuk menambahkan token autentikasi ke semua request API
 * Token akan otomatis ditambahkan ke header Authorization
 */
class AuthInterceptor(private val context: Context) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        if (shouldSkipAuth(originalRequest.url.toString())) {
            return chain.proceed(originalRequest)
        }
        
        val token = SecureStorage.getAuthToken(context)
        
        val newRequest = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        
        return chain.proceed(newRequest)
    }
    
    /**
     * Check if request should skip authentication
     */
    private fun shouldSkipAuth(url: String): Boolean {
        val skipEndpoints = listOf(
            "/authentication/merchant/login",
            "/user/info/company/get"
        )
        
        return skipEndpoints.any { endpoint ->
            url.contains(endpoint, ignoreCase = true)
        }
    }
}

/**
 * Interceptor untuk menangani response 401 (Unauthorized)
 * Otomatis logout jika token tidak valid
 */
class AuthResponseInterceptor(private val context: Context) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        
        if (response.code == 401) {
            SecureStorage.clearAllData(context)
        }
        
        return response
    }
}
