package com.solusinegeri.merchant3.data.repository

import com.solusinegeri.merchant3.data.network.MenuApi
import com.solusinegeri.merchant3.data.network.NetworkClient
import com.solusinegeri.merchant3.data.responses.MenuData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MenuRepository {
    
    private val menuApi: MenuApi by lazy {
        NetworkClient.createService(MenuApi::class.java)
    }
    
    suspend fun getActiveMenus(): Result<List<MenuData>> = withContext(Dispatchers.IO) {
        try {
            val response = menuApi.getMerchantMenuList()
            
            if (response.isSuccessful) {
                val menuResponse = response.body()
                val menuList = menuResponse?.data
                
                if (menuList != null) {
                    val activeMenus = menuList.filter { it.isActive }
                    Result.success(activeMenus)
                } else {
                    Result.failure(Exception("Data menu tidak ditemukan"))
                }
            } else {
                Result.failure(Exception("Gagal memuat menu: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error memuat menu: ${e.message}"))
        }
    }
}
