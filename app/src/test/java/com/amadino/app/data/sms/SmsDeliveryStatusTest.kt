package com.amadino.app.data.sms

import org.junit.Assert.assertEquals
import org.junit.Test

class SmsDeliveryStatusTest {

    @Test
    fun `RESULT_OK maps to delivered`() {
        // android.app.Activity.RESULT_OK
        assertEquals(SmsDeliveryStatus.DELIVERED, SmsDeliveryStatus.fromResultCode(-1))
    }

    @Test
    fun `other result codes map to failed`() {
        assertEquals(SmsDeliveryStatus.FAILED, SmsDeliveryStatus.fromResultCode(0))
        assertEquals(SmsDeliveryStatus.FAILED, SmsDeliveryStatus.fromResultCode(1))
    }

    @Test
    fun `status values are stable strings stored in the database`() {
        assertEquals("PENDING", SmsDeliveryStatus.PENDING)
        assertEquals("DELIVERED", SmsDeliveryStatus.DELIVERED)
        assertEquals("FAILED", SmsDeliveryStatus.FAILED)
    }
}
