# Amadino

A native Android application for comprehensive student management — tracking attendance per class time, shift management, financial tracking, and SMS notifications to parents.

## Product Overview

Amadino is a simple, reliable, and cost-effective school-administration app that stores data for 50–60 students locally on a smartphone. The school administrator can:

- Manage attendance based on specific class times and daily shifts (categorized by days of the week)
- Track each student's financial status (total tuition and current debt)
- Send entry/exit SMS notifications to a primary or secondary registered phone number
- Display the monitor/supervisor assigned to each shift
- Track the total number of SMS messages sent

## Key Principles

- **Zero server cost** — no web hosting, internet accounts, or external SMS panels
- **Local data storage** — Room (SQLite) on the device
- **Direct SMS** — official Android `SmsManager` API using the device's SIM card
- **Simple UI, fast operations, strict data privacy**
- **Professional Git workflow** — active development on the `develop` branch

## Core Technologies

| Layer | Technology |
|-------|------------|
| Language | Kotlin |
| UI Framework | Jetpack Compose |
| Database | Room (SQLite) |
| Architecture | ViewModel + Repository pattern |
| SMS Integration | Android `SmsManager` (official API) |
| Version Control | Git & GitHub |

## GitHub & Git Protocol

- Remote: `https://github.com/Amirh2006/Amadino.git`
- All active development, commits, and pushes are made on the `develop` branch first.
- Do not push directly to `main`/`master` during active development.
- Commit messages follow [Conventional Commits](https://www.conventionalcommits.org/): `<type>(<scope>): <imperative summary>`
  - Example: `feat(students): add dual mobile numbers`
  - Example: `fix(attendance): prevent duplicate class records`

## Project Phases

1. Environment & Repository Initialization
2. Requirements & Specifications Analysis
3. UI/UX & Architecture Design
4. Android Project Foundation
5. Database & Comprehensive Modeling
6. Class-Time Attendance Logic
7. Advanced SMS Management
8. History, Finance, and Backups
9 & 10. Testing, Quality Assurance & Final APK

## License

All rights reserved. Local-first, privacy-focused student management.