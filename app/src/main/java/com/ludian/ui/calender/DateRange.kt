package com.ludian.ui.calender

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DateRange(
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    init {
        require(startDate <= endDate) { "Start date must be before or equal to end date" }
    }

    fun isDateInRange(date: LocalDate): Boolean {
        return date.isAfter(startDate.minusDays(1)) && date.isBefore(endDate.plusDays(1))
    }

    fun isValidRange(): Boolean {
        return startDate <= endDate
    }

    // Calculate the number of days in the range
    fun daysInRange(): Long {
        return java.time.Duration.between(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay()).toDays()
    }

    // Format dates
    fun formattedDateRange(formatter: DateTimeFormatter): String {
        return "${startDate.format(formatter)} - ${endDate.format(formatter)}"
    }
}

