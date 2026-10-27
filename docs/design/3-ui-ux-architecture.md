# Amadino — Phase 3: UI/UX & Architecture Design

**Status:** Draft for user approval
**Covers:** 3.1 Dashboard · 3.2 Student form · 3.3 Attendance flow · 3.4 SMS counter/history · 3.5 Architecture plan

---

## 0. Design Principles

- **Material 3** with Jetpack Compose; single-activity architecture.
- **RTL-first** (Persian) with English fallback; all strings externalized in `strings.xml`.
- **Fast operator flow:** attendance reachable in ≤3 taps from the dashboard.
- **Large touch targets**, high-contrast status chips, minimal typing.
- **Offline-first:** no loading spinners waiting on network; local Room reads.
- **Defaults applied (from §7 open questions):** currency = Toman (integer); SMS on ENTRY/EXIT only; exit = manual action; one shift per student; Persian default SMS template. *These can be revised on your feedback.*

---

## 3.1 Main Dashboard

**Layout (top → bottom):**
1. **Top app bar:** app name "Amadino", current date, SMS counter chip (e.g., "SMS: 1240"), settings icon.
2. **Week strip:** horizontal row of 7 day chips (SAT…FRI). School days highlighted; non-school days dimmed/disabled. Today auto-selected.
3. **Selected day section:**
   - List of **active shifts** for that day.
   - Each shift card shows: shift name, `start–end`, **monitor name** (with a person icon), class-time count, and a "Take Attendance" button.
   - Inactive shifts collapsed under "Inactive" (expandable), not shown in attendance flow.
4. **Quick actions (FAB or bottom bar):** Students · Finance · Reports · SMS Log.
5. **Empty state:** if no shifts for the day → "No shifts. Add a shift."

**Components:** `DaySelector`, `ShiftCard`, `MonitorBadge`, `SmsCounterChip`, `QuickActionBar`.

---

## 3.2 Student Profile Form

**Fields (grouped):**
- **Identity:** Full name*, Father name*, National ID (optional), Grade*.
- **Contact:** Mobile 1* (primary SMS), Mobile 2 (fallback SMS). Phone-keyboard input, live validation.
- **Financial:** Total tuition*, Current debt (read-only, auto = tuition on create; editable via payments).
- **Status:** Active toggle.
- **Save / Cancel** actions; validation errors inline under fields.

**Student list screen:** searchable list (name/grade), each row shows name, grade, debt badge (red if >0), and SMS-reachability icon.

**Validation (per 2.2):** name/father/grade non-empty; mobile1 required & valid; mobile2 valid if present; tuition ≥0; debt ≤ tuition; nationalId unique.

---

## 3.3 Attendance Flow (per Class Time)

**Flow:**
1. Dashboard → tap shift → **class-time picker** (list of class times in the shift; highlights the current/next one by time).
2. **Attendance screen:**
   - Header: shift name, class time name + time range, monitor name, date.
   - Student rows: name + status segmented control **Present / Late / Absent** (default unset).
   - Bulk actions: "Mark all Present", "Mark all Absent".
   - Sticky **Save** button; shows count (e.g., "Saved 24/25").
3. **Upsert behavior:** if records already exist for today/class time, rows pre-load existing status; Save updates (no duplicates).
4. **Post-save:** optional prompt "Send entry SMS to parents?" → triggers ENTRY SMS per 2.4. Exit action available to send EXIT SMS.

**Duplicate prevention UI:** Save button disabled if no changes; already-saved rows show a check mark.

---

## 3.4 SMS Counter & History

- **Counter display:** persistent chip on dashboard + full card in Settings: `Sent / Failed / Skipped` with `lastUpdated`; soft-warning banner when `totalSent ≥ threshold` (default 1000).
- **SMS Log screen:**
  - Filterable list (status, event type, student, date range).
  - Row: student, event (ENTRY/EXIT), destination, status chip (green SUCCESS / red FAILED / gray SKIPPED / amber PENDING), timestamp, error reason (if failed), "fallback used" tag.
  - Tap row → full message body + retry button (manual re-send).

---

## 3.5 Architecture Plan

### Layer diagram
```
UI (Compose) → ViewModel (StateFlow) → Repository → Room DAO / SmsManager
```

### Package structure (`com.amadino.app`)
```
data/
  local/        (Room: AppDatabase, entities, DAOs, converters)
  repository/   (StudentRepository, ShiftRepository, AttendanceRepository,
                 SmsRepository, FinanceRepository)
  sms/          (SmsSender wrapper over SmsManager, permission helper)
domain/
  model/        (domain models / enums)
  usecase/      (RecordAttendanceUseCase, SendSmsUseCase, RecordPaymentUseCase)
ui/
  theme/        (Material3 theme, typography, RTL)
  dashboard/    (DashboardScreen + ViewModel)
  students/     (StudentListScreen, StudentFormScreen + ViewModels)
  attendance/   (ClassTimePickerScreen, AttendanceScreen + ViewModel)
  finance/      (DebtorsScreen, PaymentsScreen + ViewModels)
  reports/      (ReportsScreen + ViewModel)
  sms/          (SmsLogScreen, SmsCounterCard + ViewModel)
  components/   (DaySelector, ShiftCard, StatusChip, ...)
di/             (manual DI container / Hilt if approved)
```

### Key technical decisions
| Decision | Choice | Rationale |
|----------|--------|-----------|
| UI | Jetpack Compose + Material 3 | Modern, official, simple |
| Nav | Navigation-Compose, single Activity | Standard, lightweight |
| State | ViewModel + StateFlow | Testable, lifecycle-safe |
| DB | Room (SQLite) | Official, local, relational |
| DI | Manual container (no Hilt initially) | Avoid overhead; can add Hilt later |
| Async | Coroutines + Flow | Official, simple |
| SMS | `SmsManager` + `SmsManager.sendMultipartTextMessage` | Official API, no cost |
| Time | `java.time` (desugared) | Clean date/time handling |
| Permissions | Runtime SEND_SMS with graceful denial | Safety |
| Money | Integer Toman | No float errors |

### Navigation graph
```
Dashboard
 ├─ Students (list) → StudentForm (add/edit)
 ├─ Attendance → ClassTimePicker → AttendanceScreen
 ├─ Finance → Debtors → Payments
 ├─ Reports
 └─ SMS → SmsLog
```

### Room entities (preview)
`WeekDay`, `Shift`, `ClassTime`, `Student`, `Attendance`, `SmsLog`, `SmsCounter`, `Payment` (payment history implied by 2.2 SC-FN-02).

---

## 3.6 Acceptance Criteria (Phase 3)

- [ ] Dashboard shows week strip, active shifts, and monitor names per shift.
- [ ] Student form covers identity, dual mobiles, and financial fields with validation.
- [ ] Attendance flow is per class time with upsert and bulk actions.
- [ ] SMS counter and history log designs are defined.
- [ ] Architecture (layers, packages, nav, decisions) is documented and approved.

---

*Approval authorizes Phase 4 (Android Project Foundation).*
