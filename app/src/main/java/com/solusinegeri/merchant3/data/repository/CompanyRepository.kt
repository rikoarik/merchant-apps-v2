package com.solusinegeri.merchant3.data.repository

import com.solusinegeri.merchant3.core.utils.ErrorParser
import com.solusinegeri.merchant3.data.network.AuthService
import com.solusinegeri.merchant3.data.responses.CompanyData
import com.solusinegeri.merchant3.data.responses.InitialCompanyResponse
import retrofit2.Response

/**
 * Repository untuk handle company data
 */
class CompanyRepository(private val authService: AuthService) {
    
    /**
     * Get company data berdasarkan name atau initial
     */
    suspend fun getCompanyData(nameOrInitial: String): Result<CompanyData> {
        return try {
            val response: Response<InitialCompanyResponse> = authService.getInitialCompany(nameOrInitial)
            
            if (response.isSuccessful) {
                val companyResponse = response.body()
                if (companyResponse?.data != null) {
                    Result.success(companyResponse.data)
                } else {
                    Result.failure(Exception("Data company tidak ditemukan"))
                }
            } else {
                val errorMessage = ErrorParser.parseCompanyError(
                    response.errorBody()?.string() ?: "",
                    response.code()
                )
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    
    /**
     * Validate company initial
     */
    suspend fun validateCompanyInitial(initial: String): Result<CompanyData> {
        return getCompanyData(initial)
    }
}
