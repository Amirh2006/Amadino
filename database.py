# database.py
# مدیریت پایگاه données محلی users.json با حفاظت از فایل و قفل‌سازی رشته‌ها (thread-safe)
# این ماژول مسئول خواندن، نوشتن و به‌روزرسانی وضعیت حضور/غیاب و وضعیت پیامک است.

import json
import os
import threading
from datetime import datetime
from typing import List, Dict, Optional, Any
from pathlib import Path

# مسیر فایل داده‌های کاربران
USERS_FILE_PATH = "users.json"

# قفل برای جلوگیری از همزمانی نوشتن به فایل ( thread-safe )
_file_lock = threading.Lock()


class UsersDatabase:
    """
    کلاس مدیریت فایل users.json.
    شامل متدهای خواندن دانش‌آموزان، تغییر وضعیت ورود/خروج، و به‌روزرسانی وضعیت پیامک.
    """

    def __init__(self, filepath: str = USERS_FILE_PATH):
        self.filepath = Path(filepath)
        self._ensure_file_exists()

    def _ensure_file_exists(self) -> None:
        """
        اگر فایل users.json وجود ندارد، یک فایل پیش‌فرض با ساختار متداول ایجاد می‌کند.
        """
        if not self.filepath.exists():
            default_data = {
                "version": "1.2.0",
                "updated_at": datetime.now().isoformat(),
                "students": [],
                "settings": {
                    "sms_enabled": True,
                    "auto_notify_parent": True,
                    "theme": "cyber_violet",
                },
            }
            self._write(default_data)

    def _read(self) -> Dict[str, Any]:
        """
        خواندن امن از فایل JSON با کدگذاری UTF-8.
        """
        with _file_lock:
            try:
                with open(self.filepath, "r", encoding="utf-8") as f:
                    return json.load(f)
            except (json.JSONDecodeError, IOError) as exc:
                raise RuntimeError(f"خطا در خواندن فایل {self.filepath}: {exc}")

    def _write(self, data: Dict[str, Any]) -> None:
        """
        نوشتن امن به فایل JSON با indentation و حفظ حروف فارسی (ensure_ascii=False).
        """
        with _file_lock:
            try:
                # نوشتن به فایل موقت برای جلوگیری از حذف داده در صورت خطا
                temp_path = self.filepath.with_suffix(".tmp")
                with open(temp_path, "w", encoding="utf-8") as f:
                    json.dump(data, f, ensure_ascii=False, indent=2)
                # جایگزینی فایل اصلی
                os.replace(temp_path, self.filepath)
            except IOError as exc:
                raise RuntimeError(f"خطا در نوشتن فایل {self.filepath}: {exc}")

    def get_students(self) -> List[Dict[str, Any]]:
        """
        برگشت لیست تمام دانش‌آموزان از فایل users.json.
        """
        data = self._read()
        return data.get("students", [])

    def get_student_by_id(self, student_id: int) -> Optional[Dict[str, Any]]:
        """
        یافتن یک دانش‌آموز بر اساس شناسه یکتای Id.
        """
        students = self.get_students()
        for student in students:
            if student.get("id") == student_id:
                return student
        return None

    def get_active_students(self) -> List[Dict[str, Any]]:
        """
        برگشت فقط دانش‌آموزان فعال (is_active == True).
        """
        return [s for s in self.get_students() if s.get("is_active", False)]

    def check_in(self, student_id: int) -> bool:
        """
        تغییر وضعیت دانش‌آموز به 'present' و ثبت زمان ورود.
        اگر قبلاً حاضر بود، هیچ تغییر نمی‌کند و False برمی‌گرداند.
        """
        data = self._read()
        updated = False

        for student in data.get("students", []):
            if student.get("id") == student_id:
                if student.get("status") != "present":
                    student["status"] = "present"
                    student["last_checkin"] = datetime.now().isoformat()
                    student["sms_sent"] = False  # برای ارسال مجدد پیامک بعد از تغییر وضعیت
                    updated = True
                break

        if updated:
            data["updated_at"] = datetime.now().isoformat()
            self._write(data)

        return updated

    def check_out(self, student_id: int) -> bool:
        """
        تغییر وضعیت دانش‌آموز به 'absent' و ثبت زمان خروج.
        اگر قبلاً غایب بود، هیچ تغییر نمی‌کند و False برمی‌گرداند.
        """
        data = self._read()
        updated = False

        for student in data.get("students", []):
            if student.get("id") == student_id:
                if student.get("status") != "absent":
                    student["status"] = "absent"
                    student["last_checkout"] = datetime.now().isoformat()
                    student["sms_sent"] = False
                    updated = True
                break

        if updated:
            data["updated_at"] = datetime.now().isoformat()
            self._write(data)

        return updated

    def mark_sms_sent(self, student_id: int, delivered: bool = False) -> bool:
        """
        علامت‌گذاری وضعیت ارسال پیامک برای یک دانش‌آموز.
        """
        data = self._read()
        updated = False

        for student in data.get("students", []):
            if student.get("id") == student_id:
                student["sms_sent"] = True
                student["sms_delivered"] = delivered
                updated = True
                break

        if updated:
            data["updated_at"] = datetime.now().isoformat()
            self._write(data)

        return updated

    def get_statistics(self) -> Dict[str, int]:
        """
        محاسبه آمار attending, absent, sms_sent برای نمایش در کارت‌های داشبورد.
        """
        students = self.get_students()
        present = sum(1 for s in students if s.get("status") == "present")
        absent = sum(1 for s in students if s.get("status") == "absent")
        sms_sent = sum(1 for s in students if s.get("sms_sent", False))

        return {
            "total": len(students),
            "present": present,
            "absent": absent,
            "sms_sent": sms_sent,
        }

    def add_student(self, student_data: Dict[str, Any]) -> int:
        """
        افزودن یک دانش‌آموز جدید به فایل users.json و برگشت id 생성 شده.
        """
        data = self._read()
        students = data.get("students", [])

        # تولید id جدید (بیشتر از idهایی که هست)
        new_id = max([s.get("id", 0) for s in students], default=0) + 1

        new_student = {
            "id": new_id,
            "full_name": student_data.get("full_name", ""),
            "father_name": student_data.get("father_name", ""),
            "mobile": student_data.get("mobile", ""),
            "parent_mobile": student_data.get("parent_mobile", ""),
            "grade": student_data.get("grade", ""),
            "is_active": True,
            "status": "absent",
            "last_checkin": None,
            "last_checkout": None,
            "sms_sent": False,
            "sms_delivered": False,
        }

        students.append(new_student)
        data["students"] = students
        data["updated_at"] = datetime.now().isoformat()
        self._write(data)

        return new_id

    def update_student(self, student_id: int, updates: Dict[str, Any]) -> bool:
        """
        به‌روزرسانی اطلاعات یک دانش‌آموز موجود.
        """
        data = self._read()
        updated = False

        for student in data.get("students", []):
            if student.get("id") == student_id:
                student.update(updates)
                student["updated_at"] = datetime.now().isoformat()
                updated = True
                break

        if updated:
            data["updated_at"] = datetime.now().isoformat()
            self._write(data)

        return updated

    def delete_student(self, student_id: int) -> bool:
        """
        حذف یک دانش‌آموز از فایل (حذف نرم - is_active=false).
        """
        data = self._read()
        updated = False

        for student in data.get("students", []):
            if student.get("id") == student_id:
                student["is_active"] = False
                student["updated_at"] = datetime.now().isoformat()
                updated = True
                break

        if updated:
            data["updated_at"] = datetime.now().isoformat()
            self._write(data)

        return updated

    def get_settings(self) -> Dict[str, Any]:
        """
        برگشت تنظیمات برنامه از فایل users.json.
        """
        data = self._read()
        return data.get("settings", {})

    def save_settings(self, settings: Dict[str, Any]) -> None:
        """
        ذخیره تنظیمات در فایل users.json.
        """
        data = self._read()
        data["settings"] = settings
        data["updated_at"] = datetime.now().isoformat()
        self._write(data)


# نمونهглوبال برای استفاده در سرتاسر برنامه
db = UsersDatabase()
