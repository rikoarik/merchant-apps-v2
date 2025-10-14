package com.solusinegeri.merchant3.core.utils

import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import android.widget.TextView
import com.google.android.material.chip.ChipGroup
import java.text.SimpleDateFormat
import java.util.*

/**
 * Manager untuk handle custom date filter
 */
class DateFilterManager(
    private val context: Context,
    private val chipGroup: ChipGroup,
    private val btnCustomDate: View,
    private val layoutSelectedRange: View,
    private val layoutCustomRange: View,
    private val tvSelectedDateRange: TextView,
    private val tvDateCount: TextView,
    private val etStartDate: TextView,
    private val etEndDate: TextView,
    private val btnCancelCustom: View,
    private val btnApplyCustom: View,
    private val onDateRangeChanged: (startDate: String, endDate: String, period: String) -> Unit
) {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    
    private var currentStartDate = ""
    private var currentEndDate = ""
    
    init {
        setupListeners()
    }
    
    private fun setupListeners() {
        // Custom date button
        btnCustomDate.setOnClickListener {
            showCustomDateRange()
        }
        
        // Custom date inputs
        etStartDate.setOnClickListener {
            showDatePicker(true)
        }
        
        etEndDate.setOnClickListener {
            showDatePicker(false)
        }
        
        // Custom date action buttons
        btnCancelCustom.setOnClickListener {
            hideCustomDateRange()
        }
        
        btnApplyCustom.setOnClickListener {
            applyCustomDateRange()
        }
    }
    
    private fun showCustomDateRange() {
        layoutCustomRange.visibility = View.VISIBLE
        layoutSelectedRange.visibility = View.GONE
        
        // Clear previous inputs
        etStartDate.text = ""
        etEndDate.text = ""
    }
    
    private fun hideCustomDateRange() {
        layoutCustomRange.visibility = View.GONE
    }
    
    private fun applyCustomDateRange() {
        val startDateText = etStartDate.text.toString()
        val endDateText = etEndDate.text.toString()
        
        if (startDateText.isNotEmpty() && endDateText.isNotEmpty()) {
            try {
                val startDate = displayDateFormat.parse(startDateText)
                val endDate = displayDateFormat.parse(endDateText)
                
                if (startDate != null && endDate != null) {
                    currentStartDate = dateFormat.format(startDate)
                    currentEndDate = dateFormat.format(endDate)
                    
                    val dayDiff = ((endDate.time - startDate.time) / (1000 * 60 * 60 * 24)).toInt() + 1
                    val displayText = "$startDateText - $endDateText"
                    
                    // Uncheck all chips
                    chipGroup.clearCheck()
                    
                    // Show selected range
                    layoutSelectedRange.visibility = View.VISIBLE
                    tvSelectedDateRange.text = displayText
                    tvDateCount.text = "($dayDiff hari)"
                    
                    hideCustomDateRange()
                    onDateRangeChanged(currentStartDate, currentEndDate, "CUSTOM")
                }
            } catch (e: Exception) {
                // Handle parsing error
            }
        }
    }
    
    private fun showDatePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        
        // Parse current date if available
        if (isStartDate && etStartDate.text.isNotEmpty()) {
            try {
                val parsedDate = displayDateFormat.parse(etStartDate.text.toString())
                if (parsedDate != null) {
                    calendar.time = parsedDate
                }
            } catch (e: Exception) {
                // Use current date if parsing fails
            }
        } else if (!isStartDate && etEndDate.text.isNotEmpty()) {
            try {
                val parsedDate = displayDateFormat.parse(etEndDate.text.toString())
                if (parsedDate != null) {
                    calendar.time = parsedDate
                }
            } catch (e: Exception) {
                // Use current date if parsing fails
            }
        }
        
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val formattedDate = displayDateFormat.format(selectedDate.time)
                
                if (isStartDate) {
                    etStartDate.text = formattedDate
                } else {
                    etEndDate.text = formattedDate
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        datePickerDialog.show()
    }
}
