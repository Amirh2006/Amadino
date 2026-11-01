package com.amadino.app.data.sms

import android.app.Activity

/**
 * Delivery-report states stored in `sms_log.delivery_status`.
 *
 * `null` means "no report" (e.g. the send itself failed, or the carrier does
 * not support delivery reports).
 */
object SmsDeliveryStatus {

    /** Send succeeded; waiting for the carrier's delivery broadcast. */
    const val PENDING = "PENDING"

    const val DELIVERED = "DELIVERED"
    const val FAILED = "FAILED"

    /** Maps an `SmsManager` delivered-result code to a stored status. */
    fun fromResultCode(resultCode: Int): String =
        if (resultCode == Activity.RESULT_OK) DELIVERED else FAILED
}
