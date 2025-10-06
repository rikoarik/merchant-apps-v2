package com.solusinegeri.merchant3

import com.google.gson.Gson
import com.solusinegeri.merchant3.core.utils.ErrorParser
import com.solusinegeri.merchant3.data.responses.ValidationErrorResponse
import com.solusinegeri.merchant3.data.responses.getUserFriendlyMessage
import org.junit.Test
import org.junit.Assert.*

/**
 * Test untuk memverifikasi error parsing functionality
 */
class ErrorParsingTest {
    
    private val gson = Gson()
    
    @Test
    fun testValidationErrorParsing() {
        // Test case: {"errorCodes":[],"errors":{"companyId":["MustNotBlank"]}}
        val json = """{"errorCodes":[],"errors":{"companyId":["MustNotBlank"]}}"""
        val validationError = gson.fromJson(json, ValidationErrorResponse::class.java)
        
        val result = validationError.getUserFriendlyMessage()
        assertEquals("companyId: MustNotBlank", result)
    }
    
    @Test
    fun testMultipleValidationErrors() {
        // Test case dengan multiple errors
        val json = """{"errorCodes":[],"errors":{"companyId":["MustNotBlank"],"username":["MustNotBlank"],"password":["TooShort"]}}"""
        val validationError = gson.fromJson(json, ValidationErrorResponse::class.java)
        
        val result = validationError.getUserFriendlyMessage()
        assertTrue(result.contains("companyId: MustNotBlank"))
        assertTrue(result.contains("username: MustNotBlank"))
        assertTrue(result.contains("password: TooShort"))
    }
    
    @Test
    fun testEmptyValidationErrors() {
        // Test case dengan empty errors
        val json = """{"errorCodes":[],"errors":{}}"""
        val validationError = gson.fromJson(json, ValidationErrorResponse::class.java)
        
        val result = validationError.getUserFriendlyMessage()
        assertEquals("Terjadi kesalahan validasi data", result)
    }
    
    @Test
    fun testUnknownValidationError() {
        // Test case dengan unknown error message
        val json = """{"errorCodes":[],"errors":{"companyId":["UnknownError"]}}"""
        val validationError = gson.fromJson(json, ValidationErrorResponse::class.java)
        
        val result = validationError.getUserFriendlyMessage()
        assertEquals("companyId: UnknownError", result)
    }
    
    @Test
    fun testErrorParserUtility() {
        // Test ErrorParser utility
        val json = """{"errorCodes":[],"errors":{"companyId":["MustNotBlank"]}}"""
        val result = ErrorParser.parseErrorBody(json, 400)
        assertEquals("companyId: MustNotBlank", result)
    }
    
    @Test
    fun testErrorParserHttpStatusFallback() {
        // Test HTTP status fallback - sekarang hanya return "Error {code}"
        val result = ErrorParser.parseErrorBody("", 400)
        assertEquals("Error 400", result)
        
        val result401 = ErrorParser.parseErrorBody("", 401)
        assertEquals("Error 401", result401)
        
        val result404 = ErrorParser.parseErrorBody("", 404)
        assertEquals("Error 404", result404)
        
        val result500 = ErrorParser.parseErrorBody("", 500)
        assertEquals("Error 500", result500)
    }
    
    @Test
    fun testErrorParserLoginSpecific() {
        // Test login-specific error parsing
        val json = """{"errorCodes":[],"errors":{"username":["MustNotBlank"],"password":["TooShort"]}}"""
        val result = ErrorParser.parseLoginError(json, 400)
        assertTrue(result.contains("username: MustNotBlank"))
        assertTrue(result.contains("password: TooShort"))
    }
    
    @Test
    fun testErrorParserCompanySpecific() {
        // Test company-specific error parsing
        val json = """{"errorCodes":[],"errors":{"companyId":["MustNotBlank"]}}"""
        val result = ErrorParser.parseCompanyError(json, 400)
        assertEquals("companyId: MustNotBlank", result)
    }
}
