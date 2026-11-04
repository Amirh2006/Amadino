# ui/main_window.py
# پنجره اصلی برنامه آمدینو نسخه 1.2.0
# داشبورد اصلی با کارت‌های شیشه‌ای، ساعت دیجیتال، لیست دانش‌آموزان و دکمه‌های اصلی

import customtkinter as ctk
import tkinter as tk
from typing import Optional, Dict, Any
from database import db
from sms_handler import sms_handler, SMS_TEMPLATE_CHECKIN, SMS_TEMPLATE_CHECKOUT
from theme import (
    COLOR_BG_DEEP_VELVET,
    COLOR_ACCENT_CYBER_PURPLE,
    COLOR_ALERT_CORAL,
    COLOR_SUCCESS_AQUA,
    COLOR_TEXT_LILAC,
    COLOR_TEXT_WHITE,
    COLOR_GRADIENT_SMS_START,
    COLOR_GRADIENT_SMS_END,
    FONT_FAMILY_PRIMARY,
    FONT_SIZE_TITLE,
    FONT_SIZE_SUBTITLE,
    FONT_SIZE_BODY,
    FONT_WEIGHT_BOLD,
)
from components import (
    GlassCard,
    GlowButton,
    DigitalClock,
    NeonLogo,
    StudentRow,
    SettingsButton,
    HistoryButton,
)


class MainWindow(ctk.CTk):
    """
    کلاس پنجره اصلی برنامه.
    مدیریت داشبورد، لیست دانش‌آموزان، دکمه‌های ورود/خروج/پیامک و به‌روزرسانی لحظه‌ای رابط.
    """

    def __init__(self):
        super().__init__()

        # اعمال استایل پایه از theme.py
        self.theme = __import__("theme").CyberVioletTheme()
        self.theme.apply_window_style(self)

        # متغیرهای 상태로
        self.student_rows: Dict[int, StudentRow] = {}

        # ساخت antarmuka
        self._build_ui()

        # بارگذاری اولیه داده‌ها
        self.refresh_dashboard()

    def _build_ui(self):
        """
        ساخت تمام اجزای رابط کاربری.
        """

        # ====================================================================
        # کانتینر اصلی با scroll برای صفحات بلند
        # ====================================================================
        self.main_scroll = ctk.CTkScrollableFrame(
            self,
            fg_color=COLOR_BG_DEEP_VELVET,
            corner_radius=0,
        )
        self.main_scroll.pack(fill="both", expand=True, padx=0, pady=0)

        # ====================================================================
        # سطر برتر: لگو + عنوان + ساعت
        # ====================================================================
        self.header_frame = ctk.CTkFrame(
            self.main_scroll,
            fg_color="transparent",
        )
        self.header_frame.pack(fill="x", padx=30, pady=(30, 20))

        # لگو Neon
        self.logo = NeonLogo(self.header_frame, width=100, height=100)
        self.logo.pack(side="left", padx=(0, 20))

        # عنوان و زیرعنوان
        self.title_container = ctk.CTkFrame(
            self.header_frame,
            fg_color="transparent",
        )
        self.title_container.pack(side="left", fill="y")

        self.lbl_title = ctk.CTkLabel(
            self.title_container,
            text="آمدینو",
            font=(FONT_FAMILY_PRIMARY, 40, FONT_WEIGHT_BOLD),
            text_color=COLOR_TEXT_WHITE,
            anchor="w",
        )
        self.lbl_title.pack(anchor="w")

        self.lbl_subtitle = ctk.CTkLabel(
            self.title_container,
            text="سامانه حضور و غیاب و پیامک خودکار",
            font=(FONT_FAMILY_PRIMARY, 16),
            text_color=COLOR_TEXT_LILAC,
            anchor="w",
        )
        self.lbl_subtitle.pack(anchor="w", pady=(2, 0))

        # ساعت دیجیتال در سمت راست هدر
        self.clock = DigitalClock(self.header_frame)
        self.clock.pack(side="right", padx=20)

        # ====================================================================
        # سطر دکمه‌های اصلی عمل (Actions)
        # ====================================================================
        self.actions_frame = ctk.CTkFrame(
            self.main_scroll,
            fg_color="transparent",
        )
        self.actions_frame.pack(fill="x", padx=30, pady=(0, 30))

        self.btn_checkin = GlowButton(
            self.actions_frame,
            text="ثبت ورود",
            command=self.handle_checkin_all,
            fg_color=COLOR_SUCCESS_AQUA,
            hover_color="#00C4B8",
            text_color=COLOR_BG_DEEP_VELVET,
            width=220,
            height=55,
            font_size=17,
            icon="👆",
        )
        self.btn_checkin.pack(side="left", padx=(0, 15))

        self.btn_checkout = GlowButton(
            self.actions_frame,
            text="ثبت خروج",
            command=self.handle_checkout_all,
            fg_color=COLOR_ALERT_CORAL,
            hover_color="#FF8E5A",
            text_color=COLOR_TEXT_WHITE,
            width=220,
            height=55,
            font_size=17,
            icon="🚪",
        )
        self.btn_checkout.pack(side="left", padx=(0, 15))

        self.btn_sms = GlowButton(
            self.actions_frame,
            text="ارسال پیامک",
            command=self.handle_send_sms,
            fg_color=COLOR_ACCENT_CYBER_PURPLE,
            hover_color="#B16FF0",
            text_color=COLOR_TEXT_WHITE,
            width=220,
            height=55,
            font_size=17,
            icon="✉",
        )
        self.btn_sms.pack(side="left", padx=(0, 15))

        self.btn_finalize = GlowButton(
            self.actions_frame,
            text="تایید و ثبت نهایی",
            command=self.handle_finalize,
            fg_color="#B16FF0",
            hover_color=COLOR_ACCENT_CYBER_PURPLE,
            text_color=COLOR_TEXT_WHITE,
            width=260,
            height=55,
            font_size=17,
            icon="✅",
        )
        self.btn_finalize.pack(side="left")

        # دکمه‌های secundarne (تنظیمات و تاریخچه) در سطر پایین‌تر
        self.secondary_frame = ctk.CTkFrame(
            self.main_scroll,
            fg_color="transparent",
        )
        self.secondary_frame.pack(fill="x", padx=30, pady=(0, 30))

        self.btn_settings = SettingsButton(
            self.secondary_frame,
            command=self.open_settings,
        )
        self.btn_settings.pack(side="left", padx=(0, 15))

        self.btn_history = HistoryButton(
            self.secondary_frame,
            command=self.open_history,
        )
        self.btn_history.pack(side="left")

        # ====================================================================
        # سطر کارت‌های وضعیت داشبورد (Dashboard Cards)
        # ====================================================================
        self.stats_frame = ctk.CTkFrame(
            self.main_scroll,
            fg_color="transparent",
        )
        self.stats_frame.pack(fill="x", padx=30, pady=(0, 30))

        self.card_total = GlassCard(
            self.stats_frame,
            title="کل دانش‌آموزان",
            value="0",
            value_color=COLOR_TEXT_LILAC,
            icon="👥",
        )
        self.card_total.pack(side="left", padx=(0, 15))

        self.card_present = GlassCard(
            self.stats_frame,
            title="حاضرین",
            value="0",
            value_color=COLOR_SUCCESS_AQUA,
            icon="✅",
        )
        self.card_present.pack(side="left", padx=(0, 15))

        self.card_absent = GlassCard(
            self.stats_frame,
            title="غایبان",
            value="0",
            value_color=COLOR_ALERT_CORAL,
            icon="❌",
        )
        self.card_absent.pack(side="left", padx=(0, 15))

        self.card_sms = GlassCard(
            self.stats_frame,
            title="پیامک‌های ارسال شده",
            value="0",
            value_color=COLOR_ACCENT_CYBER_PURPLE,
            icon="📱",
        )
        self.card_sms.pack(side="left")

        # ====================================================================
        # سطر لیست دانش‌آموزان
        # ====================================================================
        self.list_label = ctk.CTkLabel(
            self.main_scroll,
            text="لیست دانش‌آموزان",
            font=(FONT_FAMILY_PRIMARY, 20, FONT_WEIGHT_BOLD),
            text_color=COLOR_TEXT_WHITE,
            anchor="w",
        )
        self.list_label.pack(fill="x", padx=30, pady=(10, 15))

        self.list_container = ctk.CTkScrollableFrame(
            self.main_scroll,
            fg_color="transparent",
            height=350,
        )
        self.list_container.pack(fill="both", expand=True, padx=30, pady=(0, 30))

        # ====================================================================
        # نوار وضعیت پایین (Status Bar)
        # ====================================================================
        self.status_bar = ctk.CTkLabel(
            self.main_scroll,
            text="آمدینو v1.2.0 | قالب سایبر-بنفش | استفاده از فایل users.json",
            font=(FONT_FAMILY_PRIMARY, 12),
            text_color=COLOR_TEXT_LILAC,
            anchor="e",
        )
        self.status_bar.pack(fill="x", padx=30, pady=(0, 20))

    # ========================================================================
    # متدهای منطق تجاری (Business Logic)
    # ========================================================================

    def refresh_dashboard(self):
        """
        بارگذاری مجدد داده‌ها از users.json و به‌روزرسانی کارت‌ها و لیست.
        """
        stats = db.get_statistics()
        students = db.get_students()

        # به‌روزرسانی کارت‌ها
        self.card_total.update_value(str(stats["total"]), COLOR_TEXT_LILAC)
        self.card_present.update_value(str(stats["present"]), COLOR_SUCCESS_AQUA)
        self.card_absent.update_value(str(stats["absent"]), COLOR_ALERT_CORAL)
        self.card_sms.update_value(str(stats["sms_sent"]), COLOR_ACCENT_CYBER_PURPLE)

        # پاک‌سازی لیست قبلی
        for widget in self.list_container.winfo_children():
            widget.destroy()
        self.student_rows.clear()

        # افزودن سطرهای دانش‌آموزان
        for student in students:
            row = StudentRow(
                self.list_container,
                student=student,
                on_status_change=self.on_student_status_change,
                on_sms_send=self.on_student_sms,
            )
            row.pack(fill="x", pady=8, padx=5)
            self.student_rows[student["id"]] = row

    def on_student_status_change(self, student_id: int, new_status: str):
        """
        وقتی کاربر دکمه ورود/خروج یک سطر را می‌زند.
        """
        success = False
        if new_status == "present":
            success = db.check_in(student_id)
        else:
            success = db.check_out(student_id)

        if success:
            self.refresh_dashboard()
            self._show_status_message(f"وضعیت دانش‌آموز به {new_status} تغییر کرد.", "info")

    def on_student_sms(self, student_id: int):
        """
        وقتی کاربر دکمه پیامک یک دانش‌آموز را می‌زند.
        """
        student = db.get_student_by_id(student_id)
        if not student:
            return

        recipient = student.get("parent_mobile", student.get("mobile", ""))
        status = student.get("status", "absent")

        if status == "present":
            message = SMS_TEMPLATE_CHECKIN.format(
                parent_name=student.get("father_name", "والدین"),
                student_name=student.get("full_name", "دانش‌آموز"),
                time=datetime.now().strftime("%H:%M"),
            )
        else:
            message = SMS_TEMPLATE_CHECKOUT.format(
                parent_name=student.get("father_name", "والدین"),
                student_name=student.get("full_name", "دانش‌آموز"),
                time=datetime.now().strftime("%H:%M"),
            )

        result = sms_handler.send_sms(
            recipient=recipient,
            message=message,
            student_id=student_id,
            event_type=status,
        )

        if result["success"]:
            delivered = False  # شبیه‌سازی؛ در نسخه واقعی از delivery report گرفته می‌شود
            db.mark_sms_sent(student_id, delivered=delivered)
            self.refresh_dashboard()
            self._show_status_message(
                f"پیامک به {student.get('full_name')} ارسال شد.", "success"
            )
        else:
            self._show_status_message("خطا در ارسال پیامک.", "error")

    def handle_checkin_all(self):
        """
        دکمه 'ثبت ورود' برای تمام دانش‌آموزان غایب.
        """
        students = db.get_students()
        changed = 0
        for student in students:
            if student.get("status") != "present":
                db.check_in(student["id"])
                changed += 1

        if changed > 0:
            self.refresh_dashboard()
            self._show_status_message(f"{changed} دانش‌آموز به عنوان حاضر ثبت شد.", "success")
        else:
            self._show_status_message("همه دانش‌آموزان قبلاً حاضر ثبت شده‌اند.", "info")

    def handle_checkout_all(self):
        """
        دکمه 'ثبت خروج' برای تمام دانش‌آموزان حاضر.
        """
        students = db.get_students()
        changed = 0
        for student in students:
            if student.get("status") != "absent":
                db.check_out(student["id"])
                changed += 1

        if changed > 0:
            self.refresh_dashboard()
            self._show_status_message(f"{changed} دانش‌آموز به عنوان غایب ثبت شد.", "success")
        else:
            self._show_status_message("همه دانش‌آموزان قبلاً غایب ثبت شده‌اند.", "info")

    def handle_send_sms(self):
        """
        دکمه 'ارسال پیامک' برای تمام دانش‌آموزان حاضر که پیامک نکرده‌اند.
        """
        students = db.get_students()
        sent = 0
        for student in students:
            if student.get("status") == "present" and not student.get("sms_sent", False):
                self.on_student_sms(student["id"])
                sent += 1

        if sent > 0:
            self._show_status_message(f"{sent} پیامک ارسال شد.", "success")
        else:
            self._show_status_message("هیچ پیامکی برای ارسال وجود ندارد.", "info")

    def handle_finalize(self):
        """
        دکمه 'تایید و ثبت نهایی' - ذخیره نهایی همه تغییرات و تاییدیه.
        """
        # داده‌ها بلافاصله در فایل users.json ذخیره می‌شوند (database.py هر تغییر را فوراً می‌نویسد)
        stats = db.get_statistics()
        self._show_status_message(
            f"تایید نهایی انجام شد. حاضر: {stats['present']} | غایب: {stats['absent']} | پیامک: {stats['sms_sent']}",
            "success",
        )

    def open_settings(self):
        """
        باز کردن پنل تنظیمات (معمولاً یک پنجره جدید یا تას).
        """
        self._show_status_message("بخش تنظیمات در توسعه است.", "info")

    def open_history(self):
        """
        باز کردن تاریخچه حضور و غیاب.
        """
        self._show_status_message("بخش تاریخچه در توسعه است.", "info")

    def _show_status_message(self, message: str, level: str = "info"):
        """
        نمایش پیام کوتاه در نوار وضعیت پایین.
        """
        color = {
            "success": COLOR_SUCCESS_AQUA,
            "error": COLOR_ALERT_CORAL,
            "info": COLOR_ACCENT_CYBER_PURPLE,
        }.get(level, COLOR_TEXT_LILAC)

        self.status_bar.configure(text=message, text_color=color)
        # بازگردانی متن پیش‌فرض بعد از 5 ثانیه
        self.after(5000, lambda: self.status_bar.configure(
            text="آمدینو v1.2.0 | قالب سایبر-بنفش | استفاده از فایل users.json",
            text_color=COLOR_TEXT_LILAC,
        ))
