package com.solusinegeri.merchant3.data.repository

import com.solusinegeri.merchant3.data.network.BalanceApi
import com.solusinegeri.merchant3.data.network.NetworkClient
import com.solusinegeri.merchant3.data.responses.BalanceData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BalanceRepository {
    
    private val balanceApi: BalanceApi by lazy {
        NetworkClient.createService(BalanceApi::class.java)
    }
    
    suspend fun getBalance(balanceCode: String): Result<BalanceData> = withContext(Dispatchers.IO) {
        try {
            val response = balanceApi.getMerchantBalance(balanceCode)
            
            if (response.isSuccessful) {
                val balanceResponse = response.body()
                
                if (balanceResponse != null) {
                    // Convert API response to UI model
                    val balanceData = BalanceData(
                        balance = balanceResponse.amount,
                        balanceCode = balanceResponse.balanceCode,
                        currency = "IDR", // Default currency
                        isBlocked = balanceResponse.isBlocked,
                        vaNumbers = balanceResponse.vaNumbers,
                        dailyLimit = balanceResponse.dailyLimit,
                        monthlyLimit = balanceResponse.monthlyLimit
                    )
                    Result.success(balanceData)
                } else {
                    Result.failure(Exception("Data balance tidak ditemukan"))
                }
            } else {
                Result.failure(Exception("Gagal memuat balance: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error memuat balance: ${e.message}"))
        }
    }
}
