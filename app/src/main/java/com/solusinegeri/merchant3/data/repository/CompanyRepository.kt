package com.solusinegeri.merchant3.data.repository

import com.solusinegeri.merchant3.core.base.BaseRepository
import com.solusinegeri.merchant3.core.network.ApiError
import com.solusinegeri.merchant3.core.network.ApiException
import com.solusinegeri.merchant3.data.network.AuthService
import com.solusinegeri.merchant3.data.responses.CompanyData

/**
 * Repository untuk handle company data
 */
class CompanyRepository(private val authService: AuthService) : BaseRepository() {
    
    /**
     * Get company data berdasarkan name atau initial
     */
    suspend fun getCompanyData(nameOrInitial: String): Result<CompanyData> {
        return request { authService.getInitialCompany(nameOrInitial) }
            .mapCatching { response ->
                response.data ?: throw ApiException(
                    ApiError(
                        message = "Data company tidak ditemukan",
                        type = response.type
                    )
                )
            }
    }
    
    /**
     * Validate company initial
     */
    suspend fun validateCompanyInitial(initial: String): Result<CompanyData> {
        return getCompanyData(initial)
    }
}
