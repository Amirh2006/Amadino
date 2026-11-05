package com.amadino.app.utils
import java.util.Calendar

fun getCurrentJalaliDate(): String {
    val calendar = Calendar.getInstance()
    val gy = calendar.get(Calendar.YEAR)
    val gm = calendar.get(Calendar.MONTH) + 1
    val gd = calendar.get(Calendar.DAY_OF_MONTH)

    val gDaysInMonth = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
    var jy = if (gy <= 1600) 0 else 979
    val gy2 = gy - (if (gy <= 1600) 621 else 1600)
    val gy3 = if (gm > 2) (gy2 + 1) else gy2

    var days = (365 * gy2) + ((gy3 + 3) / 4) - ((gy3 + 99) / 100) + ((gy3 + 399) / 400) - 80 + gd + gDaysInMonth[gm - 1]

    jy += 33 * (days / 12053)
    days %= 12053
    jy += 4 * (days / 1461)
    days %= 1461

    if (days > 365) {
        jy += (days - 1) / 365
        days = (days - 1) % 365
    }

    val jm = if (days < 186) 1 + (days / 31) else 7 + ((days - 186) / 30)
    val jd = 1 + (if (days < 186) (days % 31) else ((days - 186) % 30))

    val monthNames = arrayOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )

    return "\u200F${toPersianDigits(jd.toString())} ${monthNames[jm - 1]} ${toPersianDigits(jy.toString())}"
}