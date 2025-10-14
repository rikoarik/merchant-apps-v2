package com.solusinegeri.merchant3.data.repository

import com.solusinegeri.merchant3.core.base.BaseRepository
import com.solusinegeri.merchant3.core.network.ApiError
import com.solusinegeri.merchant3.core.network.ApiException
import com.solusinegeri.merchant3.data.network.MenuApi
import com.solusinegeri.merchant3.data.network.NetworkClient
import com.solusinegeri.merchant3.data.responses.MenuData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MenuRepository : BaseRepository() {
    
    private val menuApi: MenuApi by lazy {
        NetworkClient.createService(MenuApi::class.java)
    }
    
    suspend fun getActiveMenus(): Result<List<MenuData>> = withContext(Dispatchers.IO) {
        request { menuApi.getMerchantMenuList() }
            .mapCatching { response ->
                val menuList = response.data
                    ?: throw ApiException(ApiError(message = "Data menu tidak ditemukan"))
                menuList.filter { it.isActive }
            }
    }
}
