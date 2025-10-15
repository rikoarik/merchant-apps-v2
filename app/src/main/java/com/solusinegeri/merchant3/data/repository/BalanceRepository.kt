package com.solusinegeri.merchant3.data.repository

import com.solusinegeri.merchant3.core.network.safeApiCall
import com.solusinegeri.merchant3.data.network.BalanceApi
import com.solusinegeri.merchant3.data.network.NetworkClient
import com.solusinegeri.merchant3.data.responses.BalanceData

class BalanceRepository(
    private val balanceApi: BalanceApi = NetworkClient.createService(BalanceApi::class.java)
) {

    suspend fun getBalance(balanceCode: String): Result<BalanceData> =
        safeApiCall(
            apiCall = { balanceApi.getMerchantBalance(balanceCode) },
            onEmptyBody = { IllegalStateException("Data balance tidak ditemukan") }
        ).mapCatching { balanceResponse ->
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
