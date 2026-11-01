package com.amadino.app.data.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Receives `SmsManager` sent-result broadcasts and publishes them to
 * [SmsResultBus]. Declared in the manifest so it works on every supported
 * Android version without dynamic receiver registration restrictions.
 */
class SmsSentReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val requestId = intent?.getStringExtra(EXTRA_REQUEST_ID) ?: return
        SmsResultBus.publish(requestId, resultCode)
    }

    companion object {
        const val ACTION = "com.amadino.app.action.SMS_SENT"
        const val EXTRA_REQUEST_ID = "extra_request_id"
    }
}
