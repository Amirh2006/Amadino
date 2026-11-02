# 13. Visual design v1.1.0 — logo, theme, colors

## App icon

The launcher icon was updated to match the brand identity:

- Background: Dark BG `#070D19`
- Foreground: Secondary cyan `#00E5FF` with Tertiary green `#39E079` accent
- Adaptive icon XML (`mipmap-anydpi-v26`) with vector drawable foreground

## Color scheme

All Compose screens now use a custom Material3 color scheme derived from the
design specification:

| Role | Hex | Usage |
|------|-----|-------|
| Primary | `#00E5FF` | Primary action buttons, accents |
| On Primary | `#0B1426` | Text on primary |
| Secondary | `#39E079` | Secondary actions, success |
| On Secondary | `#0B1426` | Text on secondary |
| Surface | `#070D19` | Main background |
| On Surface | `#FFFFFF` | Primary text |
| Surface Variant | `#131E32` | Cards, panels |
| On Surface Variant | `#94A3BB` | Secondary text, hints |
| Background | `#070D19` | Main background |
| On Background | `#FFFFFF` | Primary text |

Dynamic color (Material You theming) is disabled so the brand colors are
consistent across all devices.

## Student form fix

The "افزودن" (Add) flow was verified and improved:

- Missing `Field` typealias import in `StudentFormScreen` was added so the
  form compiles and validation works.
- A snackbar confirmation ("دانش‌آموز با موفقیت ثبت شد.") is shown after a
  successful save, so users get immediate feedback.
- The app name in the launcher is now "آمدینو" (Persian).
