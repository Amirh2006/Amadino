# آمادینو (Amadino)

Offline-first student management for small institutes: weekly shifts, class-time
attendance, SMS notifications to parents, tuition/payment tracking and reports.
Everything is stored locally in Room — no server, no account, no internet
permission.

## Features

- **Dashboard** — Persian week strip (school days highlighted), active shifts for
  the selected day, persistent SMS counter, quick actions.
- **Students** — searchable list, create/edit form with inline validation
  (required fields, Iranian mobile format, Toman amounts), soft `isActive` flag.
- **Attendance** — shift → class time → student rows with Present / Late /
  Absent, bulk mark-all, upsert per `(student, class time, date)` so re-saving
  never duplicates rows.
- **SMS** — ENTRY prompt after saving attendance, manual EXIT action, automatic
  fallback from mobile 1 to mobile 2, per-message log with status chips and
  failure reasons, sent/failed/skipped counter.
- **Finance** — debtor list and payment recording that lowers the student's debt.
- **Reports** — student counts, today's attendance breakdown, financial totals,
  SMS totals.

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
│   ├── repository/ Student, Shift, Attendance, Sms, Finance
│   └── sms/        SmsSender, SmsSentReceiver, SmsResultBus, SmsTemplates
├── domain/
│   ├── model/      SmsEventType
│   ├── usecase/    SendSmsUseCase
│   └── validation/ StudentValidator
├── di/             AppContainer, ViewModel factory
└── ui/             theme, navigation, dashboard, students, attendance,
                    finance, reports, sms, components
```

Design documents live in [`docs/design/`](docs/design/):

- [3. UI/UX & architecture](docs/design/3-ui-ux-architecture.md)
- [7. SMS sending architecture](docs/design/7-sms-sending.md)

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

- Shifts and class times are seeded/entered directly in the database; there is
  no management UI yet.
- SMS templates are constants (no settings screen) and the SMS log has no retry
  action.
- Delivery reports (`deliveredIntent`) are not tracked; only the platform "sent"
  result is stored.
- Week-day school flags default to Saturday–Wednesday; editing them is pending.
