package com.amadino.app.ui.navigation

/** Route patterns and helpers for the single-activity navigation graph. */
object AppRoutes {
    const val DASHBOARD = "dashboard"

    const val STUDENT_LIST = "student_list"
    const val STUDENT_FORM = "student_form?studentId={studentId}"

    const val CLASS_TIME_PICKER = "class_time_picker/{shiftId}/{dayId}"
    const val ATTENDANCE = "attendance/{shiftId}/{dayId}/{classTimeId}"

    const val DEBTORS = "debtors"
    const val PAYMENTS = "payments/{studentId}"

    const val REPORTS = "reports"
    const val SMS_LOG = "sms_log"

    const val ARG_STUDENT_ID = "studentId"
    const val ARG_SHIFT_ID = "shiftId"
    const val ARG_DAY_ID = "dayId"
    const val ARG_CLASS_TIME_ID = "classTimeId"

    fun studentForm(studentId: Int? = null): String =
        if (studentId == null) "student_form" else "student_form?studentId=$studentId"

    fun classTimePicker(shiftId: Int, dayId: Int): String = "class_time_picker/$shiftId/$dayId"

    fun attendance(shiftId: Int, dayId: Int, classTimeId: Int): String =
        "attendance/$shiftId/$dayId/$classTimeId"

    fun payments(studentId: Int): String = "payments/$studentId"
}
