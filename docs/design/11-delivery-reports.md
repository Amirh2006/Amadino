# 11. Delivery reports and permission-aware retry

## Delivery reports

`SmsManager` distinguishes two callbacks:

- **sent** — the platform handed the message to the radio (`SmsSentReceiver`,
  Phase 7). A non-`RESULT_OK` code means it never left the device.
- **delivered** — the carrier confirmed the handset received it. This is what
  Phase 11 adds.

### Flow

```
SendSmsUseCase.send()
  ├─ insert sms_log row (status = PENDING)
  ├─ SmsSender.send(destination, body, logId)
  │     ├─ sentIntent     → SmsSentReceiver     → SmsResultBus → awaited here
  │     └─ deliveredIntent→ SmsDeliveredReceiver → sms_log.delivery_status
  └─ update row: SUCCESS/FAILED (+ delivery_status = PENDING on success)
```

Key points:

- The log row is inserted **before** sending so the delivered `PendingIntent`
  can carry its id (`extra_log_id`). This is why `sms_log.status` documents a
  `PENDING` value.
- `SmsDeliveredReceiver` is manifest-declared and writes on
  `Dispatchers.IO` via `goAsync()`; it resolves the database through
  `AppContainer.getInstance(context)`.
- `delivery_status` is `NULL` when no report is possible (send failed) and
  `PENDING` once the send succeeded but the carrier has not answered.
- Multipart messages receive one delivered intent per part; the last broadcast
  wins, which matches the platform behaviour.

### Storage

Database v3 adds a nullable column:

```sql
ALTER TABLE sms_log ADD COLUMN delivery_status TEXT
```

Existing rows keep `NULL` → shown as "no delivery information", so old history
is not misrepresented.

### UI

The SMS log card and detail dialog show:

| Value | Label |
|-------|-------|
| `PENDING` | در انتظار تحویل |
| `DELIVERED` | تحویل شد |
| `FAILED` | تحویل نشد (error color) |

## Permission-aware retry

`SmsLogScreen` checks `Context.hasSmsPermission()` before retrying:

- granted → `viewModel.retry(log)` immediately;
- not granted → `ActivityResultContracts.RequestPermission()` for `SEND_SMS`,
  remembering the pending log. On grant the retry runs; on denial the snackbar
  explains that permission is required.

The request lives in the screen (not the ViewModel) because permission dialogs
are an Activity concern; the ViewModel stays testable and Android-free apart
from `SmsEventType`.

## Counter semantics

`total_sent` counts platform-accepted messages, `total_failed` counts send
failures, `total_skipped` counts missing numbers. A later delivery failure does
**not** decrement `total_sent` — the counter measures attempts, the log carries
the outcome.
