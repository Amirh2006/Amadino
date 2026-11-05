package com.amadino.app.utils

/**
 * Helper function to convert English digits (0-9) to Persian digits (۰-۹).
 */
fun toPersianDigits(input: String): String {
    val englishDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val builder = StringBuilder()
    for (char in input) {
        val index = englishDigits.indexOf(char)
        if (index != -1) {
            builder.append(persianDigits[index])
        } else {
            builder.append(char)
        }
    }
    return builder.toString()
}