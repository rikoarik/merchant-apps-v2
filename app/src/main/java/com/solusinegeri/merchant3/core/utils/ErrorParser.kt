package com.solusinegeri.merchant3.core.utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.solusinegeri.merchant3.data.responses.ErrorResponse
import com.solusinegeri.merchant3.data.responses.ValidationErrorResponse
import com.solusinegeri.merchant3.data.responses.getUserFriendlyMessage
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response

/**
 * Utility class untuk parsing error response dari backend
 * Mendukung multiple format error response dan memberikan user-friendly messages
 */
object ErrorParser {

    private val gson = Gson()

    val errorTypeMap = mapOf(
        "SECURITY_CODE_NOT_MATCH" to ErrorDisplay(
            message = "Pin yang anda masukan salah",
            title = "Transaksi Gagal"
        ),
        "WRONG_SECURITY_CODE_COUNT_EXCEEDED" to ErrorDisplay(
            message = "Percobaan melebihi batas. Silahkan reset PIN Anda melalui aplikasi member atau hubungi admin instansi Anda",
            title = "PIN kartu Anda terblokir"
        ),
        "BAD_REQUEST" to ErrorDisplay(
            message = "Jumlah transaksi melebihi batas maksimum transaksi",
            title = "Transaksi Gagal"
        ),
        "FAILED_TO_UPDATE_BALANCE_AND_ADD_MUTATION" to ErrorDisplay(
            message = "Jumlah transaksi melebihi batas maksimum transaksi",
            title = "Transaksi Gagal"
        ),
        "NOT_FOUND" to ErrorDisplay(
            message = "Data aid tidak ditemukan",
            title = "Transaksi Gagal"
        ),
        "DO_NOT_HAVE_SECURITY_CODE" to ErrorDisplay(
            message = "Kartu Anda belum memiliki PIN. Silahkan buat PIN Anda melalui aplikasi Member atau hubungi admin instansi Anda"
        ),
        "TRANSACTION_AMOUNT_MUST_NOT_BE_BIGGER_THAN_MAX_TRANSACTION_AMOUNT" to ErrorDisplay(
            message = "Jumlah transaksi melebihi batas maksimum transaksi",
            title = "Transaksi Gagal"
        ),
        "PROVIDER_TROUBLE" to ErrorDisplay(
            message = "Terjadi gangguan pada bank, silahkan coba beberapa saat lagi",
            title = "Transaksi Gagal"
        ),
        "MERCHANT_LOCATION_OR_ADDRESS_NOT_SET" to ErrorDisplay(
            message = "Silahkan lengkapi lokasi dan alamat toko anda",
            title = "Transaksi Gagal"
        ),
        "USER_CANNOT_CREATE_INVOICE_FOR_SELF" to ErrorDisplay(
            message = "Anda tidak dapat membuat invoice untuk diri sendiri",
            title = "Transaksi Gagal"
        ),
        "UNAUTHORIZED" to ErrorDisplay(
            message = "Token tidak valid",
            title = "Autentikasi Gagal"
        ),
        "Kartu tidak ditemukan" to ErrorDisplay(
            message = "Kartu tidak ditemukan",
            title = "Transaksi Gagal"
        ),
        "AMOUNT_MINUS_TOTAL_CHARGES_MUST_BE_BIGGER_THAN_ZERO" to ErrorDisplay(
            message = "Nominal topup tidak boleh kurang dari total potongan",
            title = "Transaksi Gagal"
        ),
        "MEMBER_NOT_FOUND" to ErrorDisplay(
            message = "Nomor ID tidak ditemukan",
            title = "Transaksi Gagal"
        ),
        "BALANCE_CANNOT_BE_BIGGER_THAN_MAX_AMOUNT" to ErrorDisplay(
            message = "Top Up gagal, saldo user telah melebihi limit",
            title = "Transaksi Gagal"
        ),

        // Network Connection Errors
        "CONNECTION_ERROR" to ErrorDisplay(
            message = "Tidak dapat mengubungkan ke server.",
            title = "Koneksi Gagal",
            isSnackBar = true
        ),
        "CONNECT_TIMEOUT" to ErrorDisplay(
            message = "Koneksi ke server timeout.",
            title = "Koneksi Timeout",
            isSnackBar = true
        ),
        "READ_TIMEOUT" to ErrorDisplay(
            message = "Permintaan data ke server timeout.",
            title = "Response Timeout",
            isSnackBar = true
        ),
        "NETWORK_ERROR" to ErrorDisplay(
            message = "Silakan periksa koneksi internet Anda~ ",
            title = "Koneksi Internet",
            isSnackBar = true
        ),
        "CARD_NOT_FOUND" to ErrorDisplay(
            message = "Kartu tidak ditemukan",
            title = "Transaksi Gagal"
        ),

        // HTTP Status Code Errors
        "400_BAD_REQUEST" to ErrorDisplay(
            message = "Permintaan tidak valid. Silakan cek kembali data yang dimasukkan.",
            title = "Permintaan Gagal",
            isSnackBar = true
        ),
        "401_UNAUTHORIZED" to ErrorDisplay(
            message = "Sesi Anda telah berakhir. Silakan login kembali.",
            title = "Autentikasi Gagal",
            isSnackBar = true
        ),
        "403_FORBIDDEN" to ErrorDisplay(
            message = "Anda tidak memiliki akses untuk melakukan operasi ini.",
            title = "Akses Ditolak",
            isSnackBar = true
        ),
        "404_NOT_FOUND" to ErrorDisplay(
            message = "Data yang Anda cari tidak ditemukan.",
            title = "Data Tidak Ditemukan",
            isSnackBar = true
        ),
        "405_METHOD_NOT_ALLOWED" to ErrorDisplay(
            message = "Metode permintaan tidak diizinkan.",
            title = "Metode Tidak Diizinkan",
            isSnackBar = true
        ),
        "408_REQUEST_TIMEOUT" to ErrorDisplay(
            message = "Permintaan timeout. Silakan coba lagi.",
            title = "Request Timeout",
            isSnackBar = true
        ),
        "409_CONFLICT" to ErrorDisplay(
            message = "Terjadi konflik data. Silakan cek kembali informasi Anda.",
            title = "Konflik Data",
            isSnackBar = true
        ),
        "422_UNPROCESSABLE_ENTITY" to ErrorDisplay(
            message = "Data yang dimasukkan tidak dapat diproses. Silakan cek kembali.",
            title = "Data Tidak Valid",
            isSnackBar = true
        ),
        "429_TOO_MANY_REQUESTS" to ErrorDisplay(
            message = "Terlalu banyak permintaan. Silakan tunggu beberapa saat.",
            title = "Terlalu Banyak Request",
            isSnackBar = true
        ),
        "500_INTERNAL_SERVER_ERROR" to ErrorDisplay(
            message = "Terjadi kesalahan pada server. Silakan coba beberapa saat lagi.",
            title = "Server Error",
            isSnackBar = true
        ),
        "502_BAD_GATEWAY" to ErrorDisplay(
            message = "Server sedang dalam pemeliharaan. Silakan coba beberapa saat lagi.",
            title = "Server Maintenance",
            isSnackBar = true
        ),
        "503_SERVICE_UNAVAILABLE" to ErrorDisplay(
            message = "Layanan sedang tidak tersedia. Silakan coba beberapa saat lagi.",
            title = "Layanan Tidak Tersedia",
            isSnackBar = true
        ),
        "504_GATEWAY_TIMEOUT" to ErrorDisplay(
            message = "Server tidak merespons. Silakan coba beberapa saat lagi.",
            title = "Server Timeout",
            isSnackBar = true
        ),

        // Business Logic Errors
        "INSUFFICIENT_BALANCE" to ErrorDisplay(
            message = "Saldo Anda tidak mencukupi untuk melakukan transaksi ini.",
            title = "Saldo Tidak Cukup"
        ),
        "TRANSACTION_LIMIT_EXCEEDED" to ErrorDisplay(
            message = "Transaksi melebihi batas maksimum yang diizinkan.",
            title = "Batas Transaksi"
        ),
        "ACCOUNT_BLOCKED" to ErrorDisplay(
            message = "Akun Anda telah diblokir. Silakan hubungi customer service.",
            title = "Akun Diblokir"
        ),
        "INVALID_AMOUNT" to ErrorDisplay(
            message = "Jumlah yang dimasukkan tidak valid.",
            title = "Jumlah Tidak Valid"
        ),
        "DUPLICATE_TRANSACTION" to ErrorDisplay(
            message = "Transaksi yang sama telah dilakukan sebelumnya.",
            title = "Transaksi Duplikat"
        ),
        "EXPIRED_SESSION" to ErrorDisplay(
            message = "Sesi Anda telah berakhir. Silakan login kembali.",
            title = "Sesi Berakhir"
        ),
        "INVALID_CREDENTIALS" to ErrorDisplay(
            message = "Username atau password yang Anda masukkan salah.",
            title = "Kredensial Salah"
        ),
        "ACCOUNT_NOT_ACTIVE" to ErrorDisplay(
            message = "Akun Anda belum aktif. Silakan aktivasi terlebih dahulu.",
            title = "Akun Belum Aktif"
        ),
        "PHONE_NUMBER_NOT_VERIFIED" to ErrorDisplay(
            message = "Nomor telepon Anda belum diverifikasi.",
            title = "Nomor Telepon Belum Diverifikasi"
        ),
        "EMAIL_NOT_VERIFIED" to ErrorDisplay(
            message = "Email Anda belum diverifikasi.",
            title = "Email Belum Diverifikasi"
        ),

        // System Errors
        "DATABASE_ERROR" to ErrorDisplay(
            message = "Terjadi kesalahan pada database. Silakan coba beberapa saat lagi.",
            title = "Database Error",
            isSnackBar = true
        ),
        "EXTERNAL_SERVICE_ERROR" to ErrorDisplay(
            message = "Layanan eksternal sedang bermasalah. Silakan coba beberapa saat lagi.",
            title = "Layanan Eksternal Error",
            isSnackBar = true
        ),
        "ENCRYPTION_ERROR" to ErrorDisplay(
            message = "Terjadi kesalahan pada enkripsi data.",
            title = "Enkripsi Error",
            isSnackBar = true
        ),
        "VALIDATION_ERROR" to ErrorDisplay(
            message = "Data yang dimasukkan tidak sesuai dengan format yang diharapkan.",
            title = "Validasi Error",
            isSnackBar = true
        ),

        // File Upload Errors
        "FILE_TOO_LARGE" to ErrorDisplay(
            message = "Ukuran file terlalu besar. Maksimal 10MB.",
            title = "File Terlalu Besar"
        ),
        "INVALID_FILE_TYPE" to ErrorDisplay(
            message = "Tipe file tidak didukung. Silakan pilih file yang sesuai.",
            title = "Tipe File Tidak Didukung"
        ),
        "FILE_CORRUPTED" to ErrorDisplay(
            message = "File yang diupload rusak. Silakan pilih file lain.",
            title = "File Rusak"
        ),

        // Payment Errors
        "PAYMENT_FAILED" to ErrorDisplay(
            message = "Pembayaran gagal. Silakan coba lagi atau pilih metode pembayaran lain.",
            title = "Pembayaran Gagal"
        ),
        "PAYMENT_EXPIRED" to ErrorDisplay(
            message = "Batas waktu pembayaran telah berakhir.",
            title = "Pembayaran Expired"
        ),
        "PAYMENT_CANCELLED" to ErrorDisplay(
            message = "Pembayaran dibatalkan.",
            title = "Pembayaran Dibatalkan"
        ),
        "INSUFFICIENT_FUNDS" to ErrorDisplay(
            message = "Dana tidak mencukupi untuk melakukan pembayaran.",
            title = "Dana Tidak Cukup"
        ),

        // General Errors
        "UNKNOWN_ERROR" to ErrorDisplay(
            message = "Terjadi kesalahan tidak diketahui. Silakan coba beberapa saat lagi.",
            title = "Kesalahan Tidak Diketahui",
            isSnackBar = true
        ),
        "MAINTENANCE_MODE" to ErrorDisplay(
            message = "Aplikasi sedang dalam pemeliharaan. Silakan coba beberapa saat lagi.",
            title = "Mode Pemeliharaan",
            isSnackBar = true
        ),
        "VERSION_OUTDATED" to ErrorDisplay(
            message = "Versi aplikasi Anda sudah usang. Silakan update ke versi terbaru.",
            title = "Versi Usang",
            isSnackBar = true
        )
    )

    fun parseErrorResponse(response: Response<*>): String =
        parseErrorBody(response.errorBody()?.string(), response.code())

    fun parseLoginError(response: Response<*>): String =
        parseErrorBody(response.errorBody()?.string(), response.code())

    fun parseCompanyError(response: Response<*>): String =
        parseErrorBody(response.errorBody()?.string(), response.code())

    // Backward compatibility overloads for existing callers/tests
    fun parseErrorResponse(errorBody: String, httpCode: Int): String =
        parseErrorBody(errorBody, httpCode)

    fun parseLoginError(errorBody: String, httpCode: Int): String =
        parseErrorBody(errorBody, httpCode)

    fun parseCompanyError(errorBody: String, httpCode: Int): String =
        parseErrorBody(errorBody, httpCode)

    /**
     * Get user-friendly message berdasarkan HTTP status code
     */
    fun getHttpStatusMessage(httpCode: Int): String {
        return when (httpCode) {
            400 -> "Data yang dimasukkan tidak valid. Silakan periksa kembali."
            401 -> "Username atau password salah. Silakan coba lagi."
            403 -> "Akses ditolak. Silakan hubungi administrator."
            404 -> "Data tidak ditemukan. Silakan periksa kembali."
            409 -> "Data sudah ada. Silakan gunakan data yang berbeda."
            422 -> "Data tidak valid. Silakan periksa kembali input Anda."
            429 -> "Terlalu banyak permintaan. Silakan tunggu sebentar."
            500 -> "Terjadi kesalahan pada server. Silakan coba lagi nanti."
            502 -> "Server sedang dalam maintenance. Silakan coba lagi nanti."
            503 -> "Service tidak tersedia. Silakan coba lagi nanti."
            else -> "Terjadi kesalahan: $httpCode"
        }
    }

    fun parseErrorBody(errorBody: String?, httpCode: Int = 400): String {
        if (errorBody.isNullOrEmpty()) {
            return getHttpStatusMessage(httpCode)
        }

        runCatching {
            gson.fromJson(errorBody, ValidationErrorResponse::class.java)
        }.getOrNull()?.let { validation ->
            if (validation.errors.isNotEmpty()) {
                return validation.getUserFriendlyMessage()
            }
        }

        runCatching {
            gson.fromJson(errorBody, ErrorResponse::class.java)
        }.getOrNull()?.let { errorResponse ->
            return errorResponse.detail.getUserFriendlyMessage()
        }

        return getHttpStatusMessage(httpCode)
    }

    fun extractMessage(responseBody: ResponseBody?): String {
        responseBody?.use {
            return try {
                val errorBody = it.string()
                if (errorBody.isNullOrEmpty()) {
                    // Jika errorBody kosong, kemungkinan masalah jaringan
                    return "Silakan periksa koneksi internet Anda~ "
                }

                val jsonObject = JSONObject(errorBody)
                Log.e("ErrorParser", jsonObject.toString())

                val detail =
                    if (jsonObject.has("detail")) jsonObject.getJSONObject("detail") else jsonObject

                // Check for message field first
                val message = detail.optString("message", "").trim()
                if (message.isNotEmpty()) {
                    return message
                }
                // Check for errorCodes field (array format)
                if (detail.has("errorCodes")) {
                    val errorCodes = detail.getJSONArray("errorCodes")
                    if (errorCodes.length() > 0) {
                        val errorCode = errorCodes.getString(0)
                        val display = errorTypeMap[errorCode]
                        return display?.message ?: errorCode
                    }
                }

                // Check for type field
                val type = detail.optString("type", "").trim()
                if (type.isNotEmpty()) {
                    val display = errorTypeMap[type]
                    return display?.message ?: type
                }


                // Check for errors field
                if (detail.has("errors")) {
                    val errors = detail.getJSONArray("errors")
                    if (errors.length() > 0) {
                        val error = errors.getString(0)
                        val display = errorTypeMap[error]
                        return display?.message ?: error
                    }
                }

                // Check for status field
                val status = detail.optString("status", "").trim()
                if (status.isNotEmpty()) {
                    val display = errorTypeMap[status]
                    return display?.message ?: status
                }

                // Check for code field
                val code = detail.optString("code", "").trim()
                if (code.isNotEmpty()) {
                    val display = errorTypeMap[code]
                    return display?.message ?: code
                }

                // If no specific error found, return the detail as string
                val detailString = detail.toString()
                if (detailString.isNotEmpty() && detailString != "{}") {
                    return detailString
                }

                // Jika detail kosong juga, kemungkinan masalah jaringan
                "Silakan periksa koneksi internet Anda~ "
            } catch (e: Exception) {
                Log.e("ErrorParser", "Failed to parse error message", e)
                // Jika parsing gagal, kemungkinan masalah jaringan atau response tidak valid
                "Silakan periksa koneksi internet Anda~ "
            }
        }
        // Jika responseBody null, kemungkinan masalah jaringan
        return "Silakan periksa koneksi internet Anda~ "
    }
}
