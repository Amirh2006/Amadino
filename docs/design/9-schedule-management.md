# 9. Schedule management (week days, shifts, class times)

## Goal

A fresh install has a seeded week (Saturday–Wednesday school days) but no
shifts or class times, and previously the only way to create them was to write
to the database directly. This phase adds the missing CRUD UI so the app is
usable end to end without external tooling.

## Navigation

```
dashboard ──«مدیریت»──▶ settings/weekdays
                          │
                          ├─ toggle school day (Switch)
                          └─ tap day ──▶ shifts/{dayId}
                                           │
                                           ├─ «افزودن» ──▶ shift_form?dayId=
                                           ├─ tap card ──▶ shift_form?shiftId=&dayId=
                                           └─ «زنگ‌ها» ──▶ class_times/{shiftId}
                                                             │
                                                             ├─ «افزودن» ──▶ class_time_form?shiftId=
                                                             └─ tap card ──▶ class_time_form?classTimeId=&shiftId=
```

Optional ids are encoded as query arguments with `defaultValue = -1`; the
composables treat `-1` as "create" and skip rendering when a required parent id
is missing.

## Layers

| Layer | Type | Responsibility |
|-------|------|----------------|
| Domain | `ScheduleValidator` | Range validation and HH/mm ↔ minutes conversion |
| ViewModel | `WeekDaySettingsViewModel` | Toggle `isSchoolDay`, expose all days |
| ViewModel | `ShiftListViewModel` | Shifts of one day, delete |
| ViewModel | `ShiftFormViewModel` | Create/edit shift, validation, save |
| ViewModel | `ClassTimeListViewModel` | Class times of one shift, delete |
| ViewModel | `ClassTimeFormViewModel` | Create/edit class time, validation, save |
| UI | `TimeFields` | Shared two-field HH/mm input |

All ViewModels are created through `containerViewModelFactory` and only depend
on `ShiftRepository`; no new persistence or schema changes were required.

## Decisions

- **Minutes since midnight** stays the storage format for `startTime` /
  `endTime`, so validation is a plain integer comparison and no timezone or
  locale parsing is involved.
- **Strict ranges:** the end must be after the start and both ends must fall
  inside `00:00`–`23:59`. Overnight shifts are out of scope for a school
  schedule.
- **Deleting is confirmed** with an `AlertDialog`; deleting a shift relies on
  the existing Room foreign-key cascade to remove its class times and
  attendance rows.
- **No reordering drag-and-drop:** class times carry an explicit `sortOrder`
  field edited in the form, matching the existing data model.

## Validation rules

| Field | Rule |
|-------|------|
| Shift name | Required |
| Class-time name | Required |
| Start/end | Both parse as `HH`/`mm`, `00 ≤ HH ≤ 23`, `00 ≤ mm ≤ 59` |
| Range | `end > start` |
| Sort order | Digits only, defaults to `0` |

Persian and Arabic-Indic digits are normalized before parsing, so the numeric
keyboard works regardless of the active input locale.
