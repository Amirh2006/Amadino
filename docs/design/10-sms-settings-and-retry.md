# 10. Editable SMS templates and message retry

## Goals

Two gaps from Phase 8:

1. SMS texts were compile-time constants, so every institute sent the same
   wording.
2. A message that failed (no SIM credit, radio off, no permission) could only
   be re-sent by re-saving attendance.

## Template storage

A new Room table holds one row per event type:

```sql
CREATE TABLE sms_template (
    event_type TEXT NOT NULL PRIMARY KEY,  -- ENTRY / EXIT
    body       TEXT NOT NULL,
    updated_at INTEGER NOT NULL
)
```

- **Missing row = default.** `SmsTemplates.resolveTemplate(eventType, stored)`
  returns the stored body when it is non-blank, otherwise
  `DEFAULT_ENTRY` / `DEFAULT_EXIT`. Resetting deletes the row, so no migration
  of old defaults is needed when the wording changes in a future release.
- **Database v2** with a hand-written `MIGRATION_1_2` that only creates the
  table. Existing data is preserved. `fallbackToDestructiveMigration` stays as
  a last-resort guard for unknown upgrade paths.
- **Read at send time**, not at startup: `SendSmsUseCase` asks
  `SmsTemplateRepository` for the body of the event being sent, so edits apply
  to the next message without restarting the app.

## Editor

`settings/sms_templates` (reachable from the SMS log top bar «قالبها»):

- Two multi-line fields, one per event type.
- Live preview rendered with sample values (`علی رضایی`, `1405/08/10`,
  `08:15`) so placeholder mistakes are visible immediately.
- Validation: the body must be non-blank and must contain `{student}`;
  `{date}` and `{time}` are optional.
- «بازگردانی به قالب پیش‌فرض» deletes both overrides after confirmation.
- Saving shows a snackbar confirmation.

Supported placeholders are listed in `SmsTemplates.PLACEHOLDERS` and shown at
the top of the screen: `{student}`, `{date}`, `{time}`.

## Retry

`SmsLogViewModel.retry(log)`:

1. Ignores `SUCCESS` rows and rows already being retried (per-id set exposed as
   `retryingIds`, so only that button is disabled).
2. Loads the student; if they were deleted, reports «دانش‌آموز این پیامک یافت
   نشد».
3. Rebuilds the `date` / `time` strings from `log.sentAt`, so a retry of a
   message sent yesterday still says yesterday — it is a re-send of the same
   notification, not a new event.
4. Calls `SendSmsUseCase.send(...)` with the original `attendanceId`, which
   re-runs template resolution, mobile-1 → mobile-2 fallback, logging and the
   SMS counter.
5. Reports the result in a snackbar.

Each retry therefore adds a new `sms_log` row; the original failed row stays
for audit. The counter's `total_failed` only grows for attempts that fail, so a
successful retry increments `total_sent` as expected.

## Permission note

Retry uses the same `SmsSender` path as attendance, so a missing `SEND_SMS`
permission surfaces as a `FAILED` log with a reason instead of a crash. The
runtime permission prompt lives on the attendance screen; retry assumes it was
granted earlier.
