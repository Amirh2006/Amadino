# 12. Local backup and restore

## Goal

All data lives in one Room database on the device, so a lost or reset phone
means lost records. This phase adds a manual, file-based backup with no cloud,
no account and no extra permissions.

## Format

A single UTF-8 JSON document written through the Storage Access Framework:

```json
{
  "app": "amadino",
  "version": 1,
  "exportedAt": 1760000000000,
  "weekDays":    [ { "id": 1, "name": "شنبه", "code": "SAT", "isSchoolDay": true, "sortOrder": 1 } ],
  "shifts":      [ { "id": 1, "dayId": 1, "name": "صبح", "startTime": 480, "endTime": 720, "monitorName": "", "isActive": true } ],
  "classTimes":  [ { "id": 1, "shiftId": 1, "name": "زنگ اول", "startTime": 480, "endTime": 525, "sortOrder": 0 } ],
  "students":    [ { "id": 1, "fullName": "…", "nationalId": null, "mobile2": null, "…": "…" } ],
  "attendance":  [ { "id": 1, "studentId": 1, "classTimeId": 1, "shiftId": 1, "dayId": 1, "attendanceDate": 1760000000000, "status": "PRESENT" } ],
  "smsLogs":     [ { "id": 1, "studentId": 1, "attendanceId": 1, "eventType": "ENTRY", "status": "SUCCESS", "deliveryStatus": "DELIVERED" } ],
  "payments":    [ { "id": 1, "studentId": 1, "amount": 500000 } ],
  "smsCounter":  { "id": 1, "totalSent": 10, "totalFailed": 0, "totalSkipped": 1, "lastUpdated": 1760000000000 },
  "smsTemplates":[ { "eventType": "ENTRY", "body": "{student} …" } ]
}
```

Design choices:

- **Ids are preserved** on restore so foreign keys (shift → day, class time →
  shift, attendance → student/class time/shift/day, sms log → student/attendance,
  payment → student) stay valid.
- **`app` + `version` guard** reject unrelated files and future formats instead
  of half-importing them.
- **Null vs missing:** nullable columns are written as JSON `null`, so
  `nationalId`, `mobile2`, `attendanceId`, `errorReason`, `recordedBy` and
  `deliveryStatus` survive a round trip.
- **Readable output:** `toString(2)` pretty-prints the document, so a backup can
  be inspected or hand-fixed in an editor.

## Restore semantics

Restore is **replace-all**, inside one `db.withTransaction { }`:

1. Clear children first: `sms_log`, `attendance`, `payment`, `class_time`,
   `shift`, `student`, `week_day`, `sms_counter`, `sms_template`.
2. Insert parents first: `week_day`, `shift`, `class_time`, `student`,
   `attendance`, `sms_log`, `payment`, `sms_counter`, `sms_template`.

Either the whole file is applied or nothing changes (the transaction rolls back
on error). The UI asks for confirmation because current data is discarded.

## Storage Access Framework

- Export: `ActivityResultContracts.CreateDocument("application/json")`, default
  name `amadino-backup-yyyyMMdd-HHmm.json`.
- Import: `ActivityResultContracts.OpenDocument()` with
  `application/json`, `text/plain` and `application/octet-stream` so files saved
  with a generic MIME type still open.
- Reading/writing goes through `ContentResolver.openInputStream` /
  `openOutputStream(uri, "wt")` on `Dispatchers.IO`. No storage permission is
  needed because the user picks the document.

## UI

`settings/backup`, reachable from the week-day settings top bar
«پشتیبانگیری»:

- explanation that everything stays on the device;
- «خروجی گرفتن (JSON)» and «بازیابی از فایل» buttons;
- progress indicator while a backup is running;
- snackbar with record counts after success, or the failure reason;
- confirmation dialog before a restore.

## Testing notes

The codec uses `org.json`, which is Android-only, so it is not covered by JVM
unit tests. The pure pieces around it (delivery status, formatting, validation)
are tested; the backup path is verified by compiling and by round-tripping a
document through `buildDocument()`/`restore()` during manual checks.
