package com.amadino.app.data.sms

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * In-process bus that forwards `SmsManager` sent-result broadcasts from the
 * manifest-declared [SmsSentReceiver] back to the coroutine awaiting the send.
 */
object SmsResultBus {

    data class Event(val requestId: String, val resultCode: Int)

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 16)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    fun publish(requestId: String, resultCode: Int) {
        _events.tryEmit(Event(requestId, resultCode))
    }
}
