package com.amadino.app.domain.model // Replace with your actual package name

// وضعیت‌های ممکن برای هر دانش‌آموز در هر زنگ
enum class AttendanceStatus {
    PRESENT,      // حاضر
    LATE,         // تاخیر
    ABSENT,       // غایب
    NOT_RECORDED  // ثبت‌نشده
}

// جزئیات یک زنگ کلاسی
data class SlotAttendance(
    val slotTime: String, // مثلاً "08:00 - 09:00"
    val status: AttendanceStatus = AttendanceStatus.NOT_RECORDED,
    val checkInTime: String? = null,  // ساعت ورود
    val checkOutTime: String? = null, // ساعت خروج
    val smsStatus: String = "PENDING" // SENT, FAILED, PENDING, SKIPPED
)

// مدل کامل برای صفحه جزئیات
data class StudentAttendanceDetail(
    val studentId: String,
    val studentName: String,
    val date: String, // مثلاً "1404-08-15"
    val slots: List<SlotAttendance>
)