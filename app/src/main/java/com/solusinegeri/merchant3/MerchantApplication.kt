package com.solusinegeri.merchant3

import android.app.Application
import com.solusinegeri.merchant3.data.network.NetworkClient

/**
 * Application class untuk inisialisasi global
 */
class MerchantApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        NetworkClient.initialize(this)
    }
}