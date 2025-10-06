package com.solusinegeri.merchant3.data.network

import com.solusinegeri.merchant3.data.responses.MenuListResponse
import retrofit2.Response
import retrofit2.http.GET

/**
 * Menu API interface untuk data menu merchant
 */
interface MenuApi {

    @GET("/user/account/merchant/menu/merchant")
    suspend fun getMerchantMenuList(): Response<MenuListResponse>
}
