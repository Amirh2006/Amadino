<div align="center">

<!-- 🖼️ Project Logo -->
<img src="docs/assets/logo.png" alt="Amadino Logo" width="250"/>

# 📚 آمادینو | Amadino
**سیستم هوشمند مدیریت حضور و غیاب و ارسال پیامک خودکار | Smart Attendance & Automated SMS System**

[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg?style=for-the-badge&logo=android)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin-blue.svg?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg?style=for-the-badge&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Version](https://img.shields.io/badge/Version-1.2.4-purple.svg?style=for-the-badge)]()
[![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)

### 🌐 Choose Language | انتخاب زبان
**[🇬🇧 English Version](#-english-version)** | **[🇮🇷 نسخه فارسی](#-نسخه-فارسی)**

</div>

---

<div dir="ltr" id="english-version">

## 🇬🇧 English Version

### 🌟 About The Project
**Amadino** is a native, **Offline-First** Android application designed to simplify the management of small and medium educational institutes. Unlike many similar apps, Amadino stores all student data, attendance records, and settings locally on the device with high security. No servers, no internet required. Built with modern Android technologies and focusing on **Glassmorphism UI** and a seamless user experience.

### ✨ Features
- **🎨 Dynamic UI/UX:** Supports 3 beautiful themes (Cyber Violet, Solar Dawn, Emerald Matrix) with Glassmorphism effects.
- **📋 Smart Attendance:** Record check-in, check-out, and absences independently per class shift.
- **✉️ Automated SMS:** Dynamically generated SMS templates for parents with variables like `{name}`, `{date}`, and `{time}`.
- **📊 Data & Reports:** Secure local JSON storage, History logs, and Excel (CSV) export.
- **🌍 RTL Support:** Fully optimized for Persian (Farsi) language, featuring the Jalali calendar and Persian digits.

### 🛠️ Tech Stack
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Architecture:** MVVM (Model-View-ViewModel)
- **Async Processing:** Kotlin Coroutines & StateFlow
- **Local Database:** JSON File (with Atomic Writes & Mutex Locks)
- **SMS API:** Android `SmsManager`

### 🏗️ Project Architecture
```text
com.amadino.app
│
├── 📂 data                  # Data Layer
│   ├── 📂 local             # JSON Management (UserDataManager)
│   └── 📂 sms               # SMS Service (SmsSender)
│
├── 📂 domain                # Domain Layer
│   ├── 📂 model             # Data Models (Student, Attendance)
│   └── 📂 usecase           # Business Logic (SendSmsUseCase)
│
├── 📂 ui                    # Presentation Layer
│   ├── 📂 components        # Reusable UI (GlassCard, Buttons)
│   ├── 📂 theme             # Dynamic Themes & Colors
│   └── 📂 MainViewModel.kt  # State Management
│
└── 📂 utils                 # Helpers (DateUtils, CsvExporter)
```

### 📸 Screenshots
<div align="center"> 
  <img src="docs/assets/screenshot_home.png" alt="Dashboard" width="250" style="border-radius: 12px; margin: 10px;"/> 
  <img src="docs/assets/screenshot_dark.png" alt="Cyber Violet Theme" width="250" style="border-radius: 12px; margin: 10px;"/> 
  <img src="docs/assets/screenshot_light.png" alt="Solar Dawn Theme" width="250" style="border-radius: 12px; margin: 10px;"/> 
</div>

### 🚀 Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/Amirh2006/Amadino.git
   ```
2. Open the project in Android Studio.
3. Build and install on your connected device:
   ```bash
   ./gradlew installDebug
   ```

### 📖 Usage
1. Open the app and go to **Settings** to choose your preferred theme.
2. Go to **Manage Shifts** to define your class times (e.g., 08:00 - 09:00).
3. Tap the **+** button to add a new student (Name, Parent Phone, Class, etc.).
4. From the main dashboard, tap the **Shift Card** to select the active shift, and tap the **Student Card** to select a student.
5. Use the action buttons to **Check-In**, **Check-Out**, or mark as **Absent**.
6. Use the **SMS** buttons to automatically notify parents.
7. Go to **History** to view past records and export them to CSV.

### 🤝 Call for Contribution
We welcome any contributions to this project! If you have ideas for improvement or want to add a new feature, please follow these steps:
1. **Fork** the project.
2. Create a new branch: `git checkout -b feature/NewFeature`
3. Commit your changes: `git commit -m "Add NewFeature"`
4. Push the branch: `git push origin feature/NewFeature`
5. Open a **Pull Request** so your changes can be reviewed and merged into the main branch.

### 🐛 Bug Reporting
We care about the quality of the app. If you encounter bugs, crashes, or unexpected behavior:
* Please visit the [**Issues**](https://github.com/Amirh2006/Amadino/issues) section in our GitHub repository.
* Create a **New Issue**.
* Provide detailed information (Android version, steps to reproduce, and screenshots if possible) so we can fix it ASAP.

### 👨‍💻 Author
This project is designed and developed with ❤️ by **Amirhossein Porsharifi**.
* **GitHub:** [@Amirh2006](https://github.com/Amirh2006)

### 📬 Contact Us
If you have questions, feedback, or business proposals, feel free to reach out:
* **Email:** amir.h.porsharifi@gmail.com
* **GitHub:** Via Issues or Discussions in the repository.

</div>

---

<div dir="rtl" id="نسخه-فارسی">

## 🇮🇷 نسخه فارسی

### 🌟 درباره برنامه
**آمادینو (Amadino)** یک اپلیکیشن اندروید بومی (Native) و کاملاً آفلاین (Offline-First) است که با هدف حل چالش‌های مدیریتی موسسات آموزشی کوچک و متوسط طراحی شده است. برخلاف بسیاری از نرم‌افزارهای مشابه، آمادینو تمامی اطلاعات دانش‌آموزان، سوابق حضور و غیاب و تنظیمات را به صورت محلی و با بالاترین سطح امنیت روی دستگاه شما ذخیره می‌کند و هیچ‌گونه نیازی به سرور یا اتصال به اینترنت ندارد. این پروژه با تکیه بر طراحی مدرن (Glassmorphism) و رابط کاربری کاملاً راست‌چین، تجربه‌ای روان را برای مدیران آموزشی فراهم می‌کند.

### ✨ قابلیت‌ها
- **🎨 طراحی داینامیک:** پشتیبانی از ۳ تم رنگی (بنفش سایبری، روشن خورشیدی، سبز ماتریکس) به همراه افکت‌های شیشه‌ای.
- **📋 مدیریت حضور و غیاب:** ثبت دقیق وضعیت (ورود، خروج، غایب) به تفکیک شیفت‌های کلاسی بدون تداخل داده‌ها.
- **✉️ پیامک خودکار:** ارسال پیامک به والدین با قالب‌های داینامیک (متغیرهای نام، تاریخ و ساعت).
- **📊 گزارش‌گیری:** ذخیره‌سازی امن با فایل JSON، صفحه تاریخچه و خروجی فایل اکسل (CSV).
- **🇮🇷 طراحی بومی:** کاملاً راست‌چین، همراه با تقویم شمسی دقیق و ساعت دیجیتال با اعداد فارسی.

### 🛠️ تکنولوژی‌های استفاده شده
- **زبان:** Kotlin
- **رابط کاربری (UI):** Jetpack Compose (Material 3)
- **معماری:** MVVM (Model-View-ViewModel)
- **مدیریت State:** Kotlin Coroutines & StateFlow
- **دیتابیس:** Local JSON File (مجهز به Mutex و Atomic Writes)
- **سیستم پیامک:** Android `SmsManager`

### 🏗️ معماری پروژه
<div dir="ltr">

```text
com.amadino.app
│
├── 📂 data                  # Data Layer
│   ├── 📂 local             # JSON Management (UserDataManager)
│   └── 📂 sms               # SMS Service (SmsSender)
│
├── 📂 domain                # Domain Layer
│   ├── 📂 model             # Data Models (Student, Attendance)
│   └── 📂 usecase           # Business Logic (SendSmsUseCase)
│
├── 📂 ui                    # Presentation Layer
│   ├── 📂 components        # Reusable UI (GlassCard, Buttons)
│   ├── 📂 theme             # Dynamic Themes & Colors
│   └── 📂 MainViewModel.kt  # State Management
│
└── 📂 utils                 # Helpers (DateUtils, CsvExporter)
```

</div>

### 📸 تصاویر برنامه
<div align="center"> 
  <img src="docs/assets/screenshot_home.png" alt="Dashboard" width="250" style="border-radius: 12px; margin: 10px;"/> 
  <img src="docs/assets/screenshot_dark.png" alt="Cyber Violet Theme" width="250" style="border-radius: 12px; margin: 10px;"/> 
  <img src="docs/assets/screenshot_light.png" alt="Solar Dawn Theme" width="250" style="border-radius: 12px; margin: 10px;"/> 
</div>

### 🚀 نصب و راه‌اندازی
۱. مخزن را کلون کنید:
   <div dir="ltr">

   ```bash
   git clone https://github.com/Amirh2006/Amadino.git
   ```

   </div>
۲. پروژه را در Android Studio باز کنید.
۳. برای ساخت و نصب مستقیم روی گوشی متصل شده:
   <div dir="ltr">

   ```bash
   ./gradlew installDebug
   ```

   </div>

### 📖 نحوه کاربرد
۱. وارد برنامه شوید و از بخش **تنظیمات** تم دلخواه خود را انتخاب کنید.
۲. به بخش **مدیریت شیفت‌ها** بروید و زنگ‌های کلاسی (مثلاً 08:00 - 09:00) را تعریف کنید.
۳. با زدن دکمه **+** یک دانش‌آموز جدید (نام، شماره والدین، کلاس و...) اضافه کنید.
۴. در صفحه اصلی، روی **کارت شیفت** بزنید تا شیفت فعال انتخاب شود، و روی **کارت دانش‌آموز** بزنید تا فرد مورد نظر انتخاب شود.
۵. از دکمه‌های عملیات برای **ثبت ورود**، **ثبت خروج** یا **ثبت غیبت** استفاده کنید.
۶. با دکمه‌های **پیامک** می‌توانید به صورت خودکار به والدین اطلاع‌رسانی کنید.
۷. به بخش **تاریخچه** بروید تا سوابق را ببینید و خروجی اکسل بگیرید.

### 🤝 درخواست همکاری برای توسعه
ما از هرگونه همکاری در توسعه این پروژه استقبال می‌کنیم! اگر ایده‌ای برای بهبود برنامه دارید یا می‌خواهید ویژگی جدیدی اضافه کنید، لطفاً این مراحل را دنبال کنید:
۱. پروژه را **Fork** کنید.
۲. یک شاخه (Branch) جدید بسازید: <span dir="ltr">`git checkout -b feature/NewFeature`</span>
۳. تغییرات خود را کامیت کنید: <span dir="ltr">`git commit -m "Add NewFeature"`</span>
۴. شاخه را پوش کنید: <span dir="ltr">`git push origin feature/NewFeature`</span>
۵. یک **Pull Request** ارسال کنید تا تغییرات شما بررسی و با شاخه اصلی ادغام شود.

### 🐛 گزارش باگ و مشکلات
ما به کیفیت برنامه اهمیت می‌دهیم. اگر در حین استفاده با خطا، کرش یا رفتار غیرمنتظره‌ای مواجه شدید:
* لطفاً به بخش [**Issues**](https://github.com/Amirh2006/Amadino/issues) در مخزن گیت‌هاب ما بروید.
* یک **New Issue** ایجاد کنید.
* مشکل را با جزئیات کامل (نسخه اندروید، مراحل بازتولید باگ، و در صورت امکان اسکرین‌شات) توضیح دهید تا در اسرع وقت بررسی و رفع شود.

### 👨‍💻 نویسنده
این پروژه با عشق و علاقه توسط **امیرحسین پورشریفی (Amirhossein Porsharifi)** طراحی و توسعه داده شده است.
* **گیت‌هاب:** [@Amirh2006](https://github.com/Amirh2006)

### 📬 ارتباط با ما
اگر سوالی دارید، می‌خواهید بازخورد بدهید یا پیشنهاد همکاری تجاری دارید، می‌توانید از راه‌های زیر با ما در ارتباط باشید:
* **ایمیل:** amir.h.porsharifi@gmail.com
* **گیت‌هاب:** از طریق بخش Issues یا Discussions در مخزن پروژه.

<br/>
<div align="center">
<sub>ساخته شده با ❤️ در ایران | Made with ❤️ in Iran</sub>
</div>

</div>