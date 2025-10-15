package com.solusinegeri.merchant3.data.repository

import com.solusinegeri.merchant3.core.network.safeApiCall
import com.solusinegeri.merchant3.core.utils.ErrorParser
import com.solusinegeri.merchant3.data.network.AuthService
import com.solusinegeri.merchant3.data.network.NetworkClient
import com.solusinegeri.merchant3.data.responses.CompanyData

/**
 * Repository untuk handle company data
 */
class CompanyRepository(
    private val authService: AuthService = NetworkClient.createService(AuthService::class.java)
) {

    /**
     * Get company data berdasarkan name atau initial
     */
    suspend fun getCompanyData(nameOrInitial: String): Result<CompanyData> =
        safeApiCall(
            apiCall = { authService.getInitialCompany(nameOrInitial) },
            onEmptyBody = { IllegalStateException("Data company tidak ditemukan") },
            errorParser = { ErrorParser.parseCompanyError(it) }
        ).mapCatching { response ->
            response.data ?: throw IllegalStateException("Data company tidak ditemukan")
        }

    /**
     * Validate company initial
     */
    suspend fun validateCompanyInitial(initial: String): Result<CompanyData> =
        getCompanyData(initial)
}
