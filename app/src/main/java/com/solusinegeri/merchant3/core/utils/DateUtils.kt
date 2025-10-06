package com.solusinegeri.merchant3.core.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun formatDateTime(input: String?): String {
        if (input.isNullOrBlank()) return ""
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = parser.parse(input)
            val formatter = SimpleDateFormat("dd/MM/yyyy HH.mm", Locale("id", "ID"))
            formatter.format(date!!)
        } catch (e: Exception) {
            ""
        }
    }
}
