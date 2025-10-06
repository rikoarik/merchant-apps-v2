package com.solusinegeri.merchant3.core.utils

import retrofit2.Response

/**
 * Contoh penggunaan ErrorParser untuk berbagai skenario
 * File ini bisa dihapus setelah memahami cara penggunaan
 */
object ErrorParserUsage {
    
    /**
     * Contoh penggunaan untuk repository umum
     */
    fun <T> handleApiResponse(response: Response<T>): Result<T> {
        return if (response.isSuccessful) {
            val data = response.body()
            if (data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception("Empty response"))
            }
        } else {
            val errorMessage = ErrorParser.parseErrorResponse(response)
            Result.failure(Exception(errorMessage))
        }
    }
    
    /**
     * Contoh penggunaan untuk login API
     */
    fun handleLoginResponse(response: Response<Any>): Result<Any> {
        return if (response.isSuccessful) {
            val data = response.body()
            if (data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception("Empty response"))
            }
        } else {
            val errorMessage = ErrorParser.parseLoginError(
                response.errorBody()?.string() ?: "",
                response.code()
            )
            Result.failure(Exception(errorMessage))
        }
    }
    
    /**
     * Contoh penggunaan untuk company API
     */
    fun handleCompanyResponse(response: Response<Any>): Result<Any> {
        return if (response.isSuccessful) {
            val data = response.body()
            if (data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception("Empty response"))
            }
        } else {
            val errorMessage = ErrorParser.parseCompanyError(
                response.errorBody()?.string() ?: "",
                response.code()
            )
            Result.failure(Exception(errorMessage))
        }
    }
    
    /**
     * Contoh penggunaan untuk custom error parsing
     */
    fun handleCustomResponse(response: Response<Any>): Result<Any> {
        return if (response.isSuccessful) {
            val data = response.body()
            if (data != null) {
                Result.success(data)
            } else {
                Result.failure(Exception("Empty response"))
            }
        } else {
            val errorMessage = ErrorParser.parseErrorBody(
                response.errorBody()?.string() ?: "",
                response.code()
            )
            Result.failure(Exception(errorMessage))
        }
    }
}
