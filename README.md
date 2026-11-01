# آمادینو (Amadino)

Offline-first student management for small institutes: weekly shifts, class-time
attendance, SMS notifications to parents, tuition/payment tracking and reports.
Everything is stored locally in Room — no server, no account, no internet
permission.

## Features

- **Dashboard** — Persian week strip (school days highlighted), active shifts for
  the selected day, persistent SMS counter, quick actions.
- **Schedule management** — toggle school days, CRUD shifts per day and the
  class-time bell schedule per shift (validated time ranges).
- **Students** — searchable list, create/edit form with inline validation
  (required fields, Iranian mobile format, Toman amounts), soft `isActive` flag.
- **Attendance** — shift → class time → student rows with Present / Late /
  Absent, bulk mark-all, upsert per `(student, class time, date)` so re-saving
  never duplicates rows.
- **SMS** — ENTRY prompt after saving attendance, manual EXIT action, automatic
  fallback from mobile 1 to mobile 2, per-message log with status chips,
  failure reasons and delivery reports, retry for failed/skipped messages
  (with a permission prompt), editable templates with live preview,
  sent/failed/skipped counter.
- **Finance** — debtor list and payment recording that lowers the student's debt.
- **Reports** — student counts, today's attendance breakdown, financial totals,
  SMS totals.
- **Backup** — export every table to a JSON file via the system file picker and
  restore it later; no cloud, no storage permission, ids preserved.

## Tech stack

| Area | Choice |
|------|--------|
| Language | Kotlin 2.4.21 |
| UI | Jetpack Compose (BOM 2026.09.00), Material 3, RTL |
| Architecture | Single Activity, Navigation-Compose, ViewModel + StateFlow, Repository |
| Persistence | Room 2.8.5 via KSP (no KAPT) |
| Async | Coroutines + Flow |
| SMS | `android.telephony.SmsManager` + runtime `SEND_SMS` |
| DI | Manual `AppContainer` (no Hilt) |
| Build | AGP 9.4.1, Gradle 9.6.0, JDK 17, minSdk 26, targetSdk 36 |

## Architecture

```
UI (Compose) → ViewModel (StateFlow) → Repository → Room DAO
                                     ↘ UseCase → SmsSender → SmsManager
```

```
com.amadino.app
├── data/
│   ├── local/      Room database, entities, DAOs, converters
│   ├── repository/ Student, Shift, Attendance, Sms, SmsTemplate, Finance
│   ├── backup/     BackupManager (JSON export/import)
│   └── sms/        SmsSender, SmsSentReceiver, SmsResultBus, SmsTemplates
├── domain/
│   ├── model/      SmsEventType
│   ├── usecase/    SendSmsUseCase
│   └── validation/ StudentValidator, ScheduleValidator
├── di/             AppContainer, ViewModel factory
└── ui/             theme, navigation, dashboard, students, attendance,
                    finance, reports, sms, settings, components
```

Design documents live in [`docs/design/`](docs/design/):

- [3. UI/UX & architecture](docs/design/3-ui-ux-architecture.md)
- [7. SMS sending architecture](docs/design/7-sms-sending.md)
- [9. Schedule management](docs/design/9-schedule-management.md)
- [10. SMS templates & retry](docs/design/10-sms-settings-and-retry.md)
- [11. Delivery reports & permission-aware retry](docs/design/11-delivery-reports.md)
- [12. Backup & restore](docs/design/12-backup-and-restore.md)

Requirements are consolidated in
[`docs/requirements/2.5-consolidated-requirements.md`](docs/requirements/2.5-consolidated-requirements.md).

## Build

Requirements: JDK 17+ and an Android SDK with platform 37 (`compileSdk 37`).

```powershell
# Debug APK → app/build/outputs/apk/debug/app-debug.apk
.\gradlew.bat :app:assembleDebug

# Unit tests
.\gradlew.bat :app:testDebugUnitTest

# Release APK (R8 + resource shrinking) → app/build/outputs/apk/release/
.\gradlew.bat :app:assembleRelease
```

### Release signing

Create `keystore.properties` in the project root (git-ignored):

```properties
storeFile=C:\\path\\to\\release.jks
storePassword=…
keyAlias=…
keyPassword=…
```

Without that file `assembleRelease` produces an unsigned APK.

## Data & privacy

- All data stays on the device (Room/SQLite).
- The only privileged capability is `SEND_SMS`, requested at send time.
- No analytics, no network calls.

## Known limitations

- Delivery reports are best-effort; carriers that never send them leave the
  message in `PENDING`.
- Class-time ordering uses a numeric `sortOrder` field instead of drag-and-drop.
- Retrying a message re-sends it with the original timestamp.
