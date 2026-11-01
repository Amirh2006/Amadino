package com.amadino.app.data.sms

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import java.util.UUID
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

sealed interface SmsSendResult {
    data object Success : SmsSendResult
    data class Failure(val reason: String) : SmsSendResult
}

fun Context.hasSmsPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) ==
        PackageManager.PERMISSION_GRANTED

/**
 * Thin wrapper over the platform [SmsManager]. Sends single- or multi-part
 * messages and waits for the platform's sent-result broadcast.
 */
class SmsSender(private val context: Context) {

    private val smsManager: SmsManager
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }

    suspend fun send(destination: String, body: String): SmsSendResult {
        if (!context.hasSmsPermission()) return SmsSendResult.Failure("SEND_SMS permission not granted")
        if (destination.isBlank()) return SmsSendResult.Failure("missing destination")
        if (body.isBlank()) return SmsSendResult.Failure("empty message")

        val parts = runCatching { smsManager.divideMessage(body) }
            .getOrElse { return SmsSendResult.Failure(it.message ?: "cannot divide message") }

        return runCatching {
            dispatch(destination, parts)
        }.getOrElse { throwable ->
            SmsSendResult.Failure(
                when (throwable) {
                    is SecurityException -> "SEND_SMS permission not granted"
                    is IllegalArgumentException -> throwable.message ?: "invalid destination"
                    else -> throwable.message ?: throwable.javaClass.simpleName
                }
            )
        }
    }

    private suspend fun dispatch(destination: String, parts: List<String>): SmsSendResult =
        coroutineScope {
            val requestId = UUID.randomUUID().toString()
            val result = async(start = CoroutineStart.UNDISPATCHED) {
                withTimeoutOrNull(SEND_TIMEOUT_MS) {
                    SmsResultBus.events.first { it.requestId == requestId }
                }
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestId.hashCode(),
                Intent(context, SmsSentReceiver::class.java)
                    .setAction(SmsSentReceiver.ACTION)
                    .putExtra(SmsSentReceiver.EXTRA_REQUEST_ID, requestId),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (parts.size > 1) {
                val sentIntents = ArrayList<PendingIntent>(parts.size).apply {
                    repeat(parts.size) { add(pendingIntent) }
                }
                smsManager.sendMultipartTextMessage(
                    destination,
                    null,
                    ArrayList(parts),
                    sentIntents,
                    null
                )
            } else {
                smsManager.sendTextMessage(destination, null, parts.first(), pendingIntent, null)
            }

            val event = result.await()
                ?: return@coroutineScope SmsSendResult.Failure("send result timed out")
            if (event.resultCode == Activity.RESULT_OK) {
                SmsSendResult.Success
            } else {
                SmsSendResult.Failure("send failed (code ${event.resultCode})")
            }
        }

    private companion object {
        const val SEND_TIMEOUT_MS = 60_000L
    }
}
