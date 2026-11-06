package com.amadino.app.utils

import com.amadino.app.ui.HistoryDateUiModel

/**
 * Generates a CSV formatted string from the history data.
 * Includes a BOM (\uFEFF) at the beginning so Excel recognizes Persian characters (UTF-8).
 */
fun generateCsvString(history: List<HistoryDateUiModel>): String {
    val sb = StringBuilder()
    // BOM for Excel UTF-8 support
    sb.append('\uFEFF')
    // Headers
    sb.append("تاریخ,نام دانش‌آموز,وضعیت,ساعت\n")

    for (dateItem in history) {
        for (record in dateItem.records) {
            // Escape commas just in case
            val name = record.studentName.replace(",", " ")
            sb.append("${dateItem.date},$name,${record.status},${record.time}\n")
        }
    }
    return sb.toString()
}