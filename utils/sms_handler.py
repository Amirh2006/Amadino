# utils/sms_handler.py
# مدیریت ارسال پیامک自動 به والدین/سرپرستان دانش‌آموزان
# در نسخه دسکتاپ این ماژول پیامک‌ها را شبیه‌سازی می‌کند و لاگ می‌نویسد.
# برای اتصال به Gateway پیامک واقعی، متد send_sms را پیاده‌سازی کنید.

import json
import os
from datetime import datetime
from typing import Dict, Any, Optional
from pathlib import Path

# مسیر فایل لاگ پیامک‌ها
SMS_LOG_FILE = "sms_log.json"

# وضعیت‌های ممکن پیامک
STATUS_PENDING = "pending"
STATUS_SENT = "sent"
STATUS_DELIVERED = "delivered"
STATUS_FAILED = "failed"


class SMSHandler:
    """
    کلاس مدیریت ارسال و لاگ پیامک‌ها.
    """

    def __init__(self, log_file: str = SMS_LOG_FILE):
        self.log_file = Path(log_file)
        self._ensure_log_exists()

    def _ensure_log_exists(self) -> None:
        """
        اگر فایل لاگ پیامک وجود ندارد، فایل خالی ایجاد می‌کند.
        """
        if not self.log_file.exists():
            self._write_logs([])

    def _read_logs(self) -> list:
        with open(self.log_file, "r", encoding="utf-8") as f:
            return json.load(f)

    def _write_logs(self, logs: list) -> None:
        temp_path = self.log_file.with_suffix(".tmp")
        with open(temp_path, "w", encoding="utf-8") as f:
            json.dump(logs, f, ensure_ascii=False, indent=2)
        os.replace(temp_path, self.log_file)

    def send_sms(
        self,
        recipient: str,
        message: str,
        student_id: Optional[int] = None,
        event_type: str = "checkin",
    ) -> Dict[str, Any]:
        """
        ارسال پیامک (شبیه‌سازی در نسخه دسکتاپ).
        در نسخه تولیدی، این متد باید به Gateway پیامک متصل شود.
        """
        log_entry = {
            "id": int(datetime.now().timestamp() * 1000),
            "student_id": student_id,
            "recipient": recipient,
            "message": message,
            "event_type": event_type,
            "status": STATUS_SENT,  # شبیه‌سازی ارسال موفق
            "sent_at": datetime.now().isoformat(),
            "delivered_at": None,
            "delivered": False,
        }

        logs = self._read_logs()
        logs.append(log_entry)
        self._write_logs(logs)

        return {
            "success": True,
            "status": STATUS_SENT,
            "message": "پیامک با موفقیت ارسال شد (شبیه‌سازی).",
            "log_id": log_entry["id"],
        }

    def mark_delivered(self, log_id: int) -> bool:
        """
        علامت‌گذاری یک پیامک به عنوان تحویل شده.
        """
        logs = self._read_logs()
        for log in logs:
            if log.get("id") == log_id:
                log["delivered"] = True
                log["delivered_at"] = datetime.now().isoformat()
                log["status"] = STATUS_DELIVERED
                self._write_logs(logs)
                return True
        return False

    def get_logs(self, student_id: Optional[int] = None) -> list:
        """
        برگشت تمام لاگ‌های پیامک، یا فقط مربوط به یک دانش‌آموز خاص.
        """
        logs = self._read_logs()
        if student_id is not None:
            return [l for l in logs if l.get("student_id") == student_id]
        return logs


# نمونهглوبال
sms_handler = SMSHandler()

# قالب پیامک ورود
SMS_TEMPLATE_CHECKIN = (
    "سلام {parent_name} عزیز، {student_name} در ساعت {time} به مدرسه attended شد. "
    "سامانه آمدینو"
)

# قالب پیامک خروج
SMS_TEMPLATE_CHECKOUT = (
    "سلام {parent_name} عزیز، {student_name} در ساعت {time} از مدرسه خارج شد. "
    "سامانه آمدینو"
)
