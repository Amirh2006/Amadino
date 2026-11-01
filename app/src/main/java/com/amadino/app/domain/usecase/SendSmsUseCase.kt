package com.amadino.app.domain.usecase

import com.amadino.app.data.local.entity.SmsLogEntity
import com.amadino.app.data.local.entity.StudentEntity
import com.amadino.app.data.repository.SmsRepository
import com.amadino.app.data.repository.SmsTemplateRepository
import com.amadino.app.data.sms.SmsDeliveryStatus
import com.amadino.app.data.sms.SmsSendResult
import com.amadino.app.data.sms.SmsSender
import com.amadino.app.data.sms.SmsTemplates
import com.amadino.app.domain.model.SmsEventType

/**
 * Sends one attendance notification, writes an [SmsLogEntity] row and updates
 * the persistent SMS counter. Falls back to the second mobile number when the
 * primary send fails.
 *
 * The row is inserted as `PENDING` before dispatching so that the delivery
 * report (which arrives later via [com.amadino.app.data.sms.SmsDeliveredReceiver])
 * has a log id to update.
 */
class SendSmsUseCase(
    private val smsRepository: SmsRepository,
    private val smsTemplateRepository: SmsTemplateRepository,
    private val smsSender: SmsSender
) {

    suspend fun send(
        student: StudentEntity,
        eventType: SmsEventType,
        attendanceId: Int?,
        date: String,
        time: String
    ): SmsLogEntity {
        val template = SmsTemplates.resolveTemplate(
            eventType = eventType,
            stored = smsTemplateRepository.getTemplate(eventType)
        )
        val body = SmsTemplates.render(
            template = template,
            studentName = student.fullName,
            date = date,
            time = time
        )

        val primary = student.mobile1.trim()
        val fallback = student.mobile2?.trim().orEmpty()

        if (primary.isBlank() && fallback.isBlank()) {
            return insertSkipped(student, eventType, attendanceId, body)
        }

        var destination = primary.ifBlank { fallback }
        var fallbackUsed = primary.isBlank()

        val now = System.currentTimeMillis()
        val logId = smsRepository.insertSmsLog(
            SmsLogEntity(
                studentId = student.id,
                attendanceId = attendanceId,
                eventType = eventType.value,
                destination = destination,
                messageBody = body,
                status = STATUS_PENDING,
                sentAt = now
            )
        ).toInt()

        var result = smsSender.send(destination, body, logId)

        if (result is SmsSendResult.Failure &&
            !fallbackUsed &&
            fallback.isNotBlank() &&
            fallback != primary
        ) {
            destination = fallback
            fallbackUsed = true
            result = smsSender.send(destination, body, logId)
        }

        val status = if (result is SmsSendResult.Success) STATUS_SUCCESS else STATUS_FAILED
        val reason = (result as? SmsSendResult.Failure)?.reason

        smsRepository.updateResult(
            id = logId,
            status = status,
            errorReason = reason,
            destination = destination,
            fallbackUsed = fallbackUsed
        )
        if (status == STATUS_SUCCESS) {
            smsRepository.updateDeliveryStatus(logId, SmsDeliveryStatus.PENDING)
        }
        smsRepository.incrementCounter(
            sent = if (status == STATUS_SUCCESS) 1 else 0,
            failed = if (status == STATUS_FAILED) 1 else 0,
            skipped = 0,
            lastUpdated = now
        )

        return SmsLogEntity(
            id = logId,
            studentId = student.id,
            attendanceId = attendanceId,
            eventType = eventType.value,
            destination = destination,
            messageBody = body,
            status = status,
            errorReason = reason,
            sentAt = now,
            fallbackUsed = fallbackUsed,
            deliveryStatus = if (status == STATUS_SUCCESS) SmsDeliveryStatus.PENDING else null
        )
    }

    private suspend fun insertSkipped(
        student: StudentEntity,
        eventType: SmsEventType,
        attendanceId: Int?,
        body: String
    ): SmsLogEntity {
        val now = System.currentTimeMillis()
        val log = SmsLogEntity(
            studentId = student.id,
            attendanceId = attendanceId,
            eventType = eventType.value,
            destination = "",
            messageBody = body,
            status = STATUS_SKIPPED,
            errorReason = "شماره موبایلی ثبت نشده است.",
            sentAt = now,
            fallbackUsed = false
        )
        val id = smsRepository.insertSmsLog(log).toInt()
        smsRepository.incrementCounter(sent = 0, failed = 0, skipped = 1, lastUpdated = now)
        return log.copy(id = id)
    }

    private companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_SUCCESS = "SUCCESS"
        const val STATUS_FAILED = "FAILED"
        const val STATUS_SKIPPED = "SKIPPED"
    }
}
