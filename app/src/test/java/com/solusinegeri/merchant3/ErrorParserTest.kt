package com.solusinegeri.merchant3

import com.google.gson.Gson
import com.solusinegeri.merchant3.core.utils.ErrorParser
import com.solusinegeri.merchant3.data.responses.ErrorResponse
import com.solusinegeri.merchant3.data.responses.ValidationErrorResponse
import org.junit.Test
import org.junit.Assert.*

class ErrorParserTest {
    
    private val gson = Gson()
    
    @Test
    fun testStandardErrorResponseParsing() {
        // Test case untuk format: {"detail":{"type":"COMPANY_NOT_FOUND","message":"Company not found","status_code":404,"error":"NOT_FOUND"}}
        val json = """{"detail":{"type":"COMPANY_NOT_FOUND","message":"Company not found","status_code":404,"error":"NOT_FOUND"}}"""
        
        val result = ErrorParser.parseErrorBody(json, 404)
        assertEquals("Company not found", result)
    }
    
    @Test
    fun testStandardErrorResponseWithoutMessage() {
        // Test case untuk format tanpa message, hanya type dan error
        val json = """{"detail":{"type":"COMPANY_NOT_FOUND","message":"","status_code":404,"error":"NOT_FOUND"}}"""
        
        val result = ErrorParser.parseErrorBody(json, 404)
        assertEquals("NOT_FOUND", result)
    }
    
    @Test
    fun testStandardErrorResponseOnlyType() {
        // Test case untuk format hanya dengan type
        val json = """{"detail":{"type":"COMPANY_NOT_FOUND","message":"","status_code":404,"error":""}}"""
        
        val result = ErrorParser.parseErrorBody(json, 404)
        assertEquals("Kode instansi tidak ditemukan. Silakan periksa kembali kode yang Anda masukkan.", result)
    }
    
    @Test
    fun testValidationErrorResponseParsing() {
        // Test case untuk validation error format
        val json = """{"errorCodes":[],"errors":{"companyId":["MustNotBlank"]}}"""
        
        val result = ErrorParser.parseErrorBody(json, 400)
        assertEquals("companyId: MustNotBlank", result)
    }
    
    @Test
    fun testMultipleValidationErrors() {
        // Test case untuk multiple validation errors
        val json = """{"errorCodes":[],"errors":{"companyId":["MustNotBlank"],"username":["TooShort"]}}"""
        
        val result = ErrorParser.parseErrorBody(json, 400)
        assertTrue(result.contains("companyId: MustNotBlank"))
        assertTrue(result.contains("username: TooShort"))
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
        // Test company-specific error parsing dengan format detail
        val json = """{"detail":{"type":"COMPANY_NOT_FOUND","message":"Company not found","status_code":404,"error":"NOT_FOUND"}}"""
        val result = ErrorParser.parseCompanyError(json, 404)
        assertEquals("Company not found", result)
    }
    
    @Test
    fun testErrorParserCompanySpecificValidationError() {
        // Test company-specific error parsing dengan validation error format
        val json = """{"errorCodes":[],"errors":{"companyId":["MustNotBlank"]}}"""
        val result = ErrorParser.parseCompanyError(json, 400)
        assertEquals("companyId: MustNotBlank", result)
    }
    
    @Test
    fun testInvalidJsonHandling() {
        // Test handling invalid JSON
        val invalidJson = """{"invalid": json}"""
        val result = ErrorParser.parseErrorBody(invalidJson, 400)
        assertEquals("Error 400", result)
    }
    
    @Test
    fun testEmptyErrorBody() {
        // Test empty error body
        val result = ErrorParser.parseErrorBody("", 404)
        assertEquals("Error 404", result)
    }
    
    @Test
    fun testNullErrorBody() {
        // Test null error body
        val result = ErrorParser.parseErrorBody(null ?: "", 500)
        assertEquals("Error 500", result)
    }
}
