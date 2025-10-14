package com.solusinegeri.merchant3.data.repository

import com.solusinegeri.merchant3.core.base.BaseRepository
import com.solusinegeri.merchant3.data.network.BalanceApi
import com.solusinegeri.merchant3.data.network.NetworkClient
import com.solusinegeri.merchant3.data.responses.BalanceData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BalanceRepository : BaseRepository() {
    
    private val balanceApi: BalanceApi by lazy {
        NetworkClient.createService(BalanceApi::class.java)
    }
    
    suspend fun getBalance(balanceCode: String): Result<BalanceData> = withContext(Dispatchers.IO) {
        request { balanceApi.getMerchantBalance(balanceCode) }
            .mapCatching { balanceResponse ->
                BalanceData(
                    balance = balanceResponse.amount,
                    balanceCode = balanceResponse.balanceCode,
                    currency = "IDR",
                    isBlocked = balanceResponse.isBlocked,
                    vaNumbers = balanceResponse.vaNumbers,
                    dailyLimit = balanceResponse.dailyLimit,
                    monthlyLimit = balanceResponse.monthlyLimit
                )
            }
    }
}
