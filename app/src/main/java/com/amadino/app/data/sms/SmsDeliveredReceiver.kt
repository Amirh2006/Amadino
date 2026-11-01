package com.amadino.app.data.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.amadino.app.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Receives `SmsManager` delivery reports and stores them on the matching
 * `sms_log` row. Declared in the manifest, like [SmsSentReceiver].
 */
class SmsDeliveredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val logId = intent?.getIntExtra(EXTRA_LOG_ID, -1) ?: return
        if (logId <= 0 || context == null) return

        val status = SmsDeliveryStatus.fromResultCode(resultCode)
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                AppContainer.getInstance(context)
                    .smsRepository
                    .updateDeliveryStatus(logId, status)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION = "com.amadino.app.action.SMS_DELIVERED"
        const val EXTRA_LOG_ID = "extra_log_id"
    }
}
