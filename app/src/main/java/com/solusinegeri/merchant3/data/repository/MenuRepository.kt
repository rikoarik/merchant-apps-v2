package com.solusinegeri.merchant3.data.repository

import com.solusinegeri.merchant3.core.network.safeApiCall
import com.solusinegeri.merchant3.data.network.MenuApi
import com.solusinegeri.merchant3.data.network.NetworkClient
import com.solusinegeri.merchant3.data.responses.MenuData

class MenuRepository(
    private val menuApi: MenuApi = NetworkClient.createService(MenuApi::class.java)
) {

    suspend fun getActiveMenus(): Result<List<MenuData>> =
        safeApiCall(
            apiCall = { menuApi.getMerchantMenuList() },
            onEmptyBody = { IllegalStateException("Data menu tidak ditemukan") }
        ).mapCatching { response ->
            val menuList = response.data ?: throw IllegalStateException("Data menu tidak ditemukan")
            menuList.filter { it.isActive }
        }
}
