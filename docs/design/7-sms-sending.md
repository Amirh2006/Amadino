# 7. SMS Sending Architecture

Status: implemented in Phase 7 (`develop`)

## Goal

Notify parents when a student enters or leaves the institute, using the
device's own SIM through the official `SmsManager` API. No backend, no
third-party SMS gateway, no internet permission.

## Components

```
AttendanceScreen
   └─ AttendanceViewModel
        └─ SendSmsUseCase ──> SmsRepository ──> Room (sms_log, sms_counter)
             └─ SmsSender ──> android.telephony.SmsManager
                  └─ SmsSentReceiver ──> SmsResultBus ──> awaiting coroutine
```

| File | Responsibility |
|------|----------------|
| `data/sms/SmsSender.kt` | Permission check, multipart split, dispatch, 60 s result wait |
| `data/sms/SmsSentReceiver.kt` | Manifest receiver for the platform's sent-result broadcast |
| `data/sms/SmsResultBus.kt` | In-process `SharedFlow` mapping `requestId` → result code |
| `data/sms/SmsTemplates.kt` | Persian default ENTRY/EXIT templates with placeholders |
| `domain/model/SmsEventType.kt` | `ENTRY` / `EXIT` |
| `domain/usecase/SendSmsUseCase.kt` | Render, send, fallback, log, counter |
| `di/AppContainer.kt` | Wires `SmsSender` + `SendSmsUseCase` |

## Why a manifest receiver instead of a dynamic one

Android 13+ requires `RECEIVER_EXPORTED` / `RECEIVER_NOT_EXPORTED` flags for
dynamically registered receivers, and sent-result broadcasts come from the
system. A manifest-declared, non-exported receiver with an explicit
`PendingIntent` component works on every supported API level (26–36) without
runtime registration flags.

## Flow

1. User saves attendance → `AttendanceViewModel.save()` upserts records.
2. A prompt asks whether to send ENTRY SMS (default per requirements §2.4).
3. If `SEND_SMS` is not granted, the screen launches the runtime permission
   request; the send continues only after a grant.
4. `SendSmsUseCase` renders the template (`{student}`, `{date}`, `{time}`),
   sends to `mobile1`, and on failure retries `mobile2` once.
5. Every attempt is written to `sms_log` with `SUCCESS`, `FAILED` or
   `SKIPPED` (no phone number) and increments `sms_counter`.
6. EXIT is a manual action ("ارسال پیامک خروج") for the records saved today.

## Counter semantics

- `total_sent` — platform reported `RESULT_OK`
- `total_failed` — platform error or missing permission
- `total_skipped` — student has no mobile number

## Templates

```
ENTRY: {student} عزیز، ورود شما در {date} ساعت {time} ثبت شد. آمادینو
EXIT:  {student} عزیز، خروج شما در {date} ساعت {time} ثبت شد. آمادینو
```

Short by design so most messages fit in one 70-character part and avoid
multipart billing. Templates are constants for now; user editing is a
follow-up.

## Known limitations

- No retry button in the SMS log yet (design §3.4 mentions it).
- No scheduled/automatic exit detection: EXIT is manual by design.
- Delivery reports (`deliveredIntent`) are not tracked, only sent results.
