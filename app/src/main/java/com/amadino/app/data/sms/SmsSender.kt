package com.amadino.app.data.sms

import android.content.Context
import android.telephony.SmsManager
import android.util.Log

sealed class SmsSendResult {
    object Success : SmsSendResult()
    data class Failure(val reason: String) : SmsSendResult()
}

class SmsSender(private val context: Context) {

    private fun isValidIranianNumber(phone: String): Boolean {
        return phone.matches(Regex("^09\\d{9}$"))
    }

    /**
     * Sends an SMS safely.
     * Accepts Int parameters to match the existing SendSmsUseCase call signature.
     */
    fun send(
        phoneNumber: String,
        message: String,
        sentRequestCode: Int = 0, // Matching the Int parameter from SendSmsUseCase
        deliveryRequestCode: Int = 0 // Matching the Int parameter from SendSmsUseCase
    ): SmsSendResult {
        if (!isValidIranianNumber(phoneNumber)) {
            Log.e("SmsSender", "Validation failed: Invalid phone number $phoneNumber")
            return SmsSendResult.Failure("شماره تلفن نامعتبر است")
        }
        if (message.isBlank()) {
            Log.e("SmsSender", "Validation failed: Message is empty")
            return SmsSendResult.Failure("متن پیامک نمی‌تواند خالی باشد")
        }

        return try {
            val smsManager = SmsManager.getDefault()
            val parts = smsManager.divideMessage(message)

            // Note: For full PendingIntent tracking (Sent/Delivered),
            // you would generate PendingIntents here using the sentRequestCode and deliveryRequestCode.
            // For now, we pass null to prevent crashes and ensure it sends.
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)

            Log.d("SmsSender", "SMS dispatched to $phoneNumber")
            SmsSendResult.Success
        } catch (e: Exception) {
            Log.e("SmsSender", "Failed to send SMS", e)
            SmsSendResult.Failure(e.localizedMessage ?: "خطای سیستمی در ارسال پیامک")
        }
    }
}