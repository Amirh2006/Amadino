package com.amadino.app.domain.usecase

import com.amadino.app.data.local.entity.SmsLogEntity
import com.amadino.app.data.local.entity.StudentEntity
import com.amadino.app.data.repository.SmsRepository
import com.amadino.app.data.repository.SmsTemplateRepository
import com.amadino.app.data.sms.SmsSendResult
import com.amadino.app.data.sms.SmsSender
import com.amadino.app.data.sms.SmsTemplates
import com.amadino.app.domain.model.SmsEventType

/**
 * Sends one attendance notification, writes an [SmsLogEntity] row and updates
 * the persistent SMS counter. Falls back to the second mobile number when the
 * primary send fails.
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
            return record(
                student = student,
                eventType = eventType,
                attendanceId = attendanceId,
                body = body,
                destination = "",
                status = STATUS_SKIPPED,
                errorReason = "شماره موبایلی ثبت نشده است.",
                fallbackUsed = false
            )
        }

        var destination = primary.ifBlank { fallback }
        var fallbackUsed = primary.isBlank()
        var result = smsSender.send(destination, body)

        if (result is SmsSendResult.Failure && !fallbackUsed && fallback.isNotBlank() && fallback != primary) {
            destination = fallback
            fallbackUsed = true
            result = smsSender.send(destination, body)
        }

        return when (result) {
            is SmsSendResult.Success -> record(
                student = student,
                eventType = eventType,
                attendanceId = attendanceId,
                body = body,
                destination = destination,
                status = STATUS_SUCCESS,
                errorReason = null,
                fallbackUsed = fallbackUsed
            )

            is SmsSendResult.Failure -> record(
                student = student,
                eventType = eventType,
                attendanceId = attendanceId,
                body = body,
                destination = destination,
                status = STATUS_FAILED,
                errorReason = result.reason,
                fallbackUsed = fallbackUsed
            )
        }
    }

    private suspend fun record(
        student: StudentEntity,
        eventType: SmsEventType,
        attendanceId: Int?,
        body: String,
        destination: String,
        status: String,
        errorReason: String?,
        fallbackUsed: Boolean
    ): SmsLogEntity {
        val now = System.currentTimeMillis()
        val log = SmsLogEntity(
            studentId = student.id,
            attendanceId = attendanceId,
            eventType = eventType.value,
            destination = destination,
            messageBody = body,
            status = status,
            errorReason = errorReason,
            sentAt = now,
            fallbackUsed = fallbackUsed
        )
        smsRepository.insertSmsLog(log)
        smsRepository.incrementCounter(
            sent = if (status == STATUS_SUCCESS) 1 else 0,
            failed = if (status == STATUS_FAILED) 1 else 0,
            skipped = if (status == STATUS_SKIPPED) 1 else 0,
            lastUpdated = now
        )
        return log
    }

    private companion object {
        const val STATUS_SUCCESS = "SUCCESS"
        const val STATUS_FAILED = "FAILED"
        const val STATUS_SKIPPED = "SKIPPED"
    }
}
