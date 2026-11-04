# ui/components.py
# کامپوننت‌های reusable رابط کاربری با افکت شیشه‌ای (Glassmorphism)
# شامل کارت‌های وضعیت، دکمه‌های-gradient، ساعت دیجیتال و لگو Neon (از assets.brand_logo)

import customtkinter as ctk
import tkinter as tk
from datetime import datetime
from typing import Dict
from theme import (
    COLOR_BG_DEEP_VELVET,
    COLOR_ACCENT_CYBER_PURPLE,
    COLOR_ALERT_CORAL,
    COLOR_SUCCESS_AQUA,
    COLOR_TEXT_LILAC,
    COLOR_TEXT_WHITE,
    COLOR_GLASS_BORDER,
    GLASS_BG_COLOR,
    GLASS_CORNER_RADIUS,
    GLASS_BORDER_WIDTH,
    FONT_FAMILY_PRIMARY,
    FONT_SIZE_BODY,
    FONT_SIZE_CAPTION,
    FONT_SIZE_CLOCK,
    FONT_WEIGHT_BOLD,
    FONT_WEIGHT_NORMAL,
)
from assets.brand_logo import NeonLogo


class GlassCard(ctk.CTkFrame):
    """
    کنتینر شیشه‌ای با لبه گرد، حاشیه‌های نorns و پس‌زمینه تیره.
    برای نمایش آمار و وضعیت در داشبورد استفاده می‌شود.
    """

    def __init__(
        self,
        master,
        title: str,
        value: str,
        value_color: str = COLOR_TEXT_WHITE,
        icon: str = "",
        width: int = 260,
        height: int = 140,
        **kwargs,
    ):
        super().__init__(
            master,
            width=width,
            height=height,
            corner_radius=GLASS_CORNER_RADIUS,
            fg_color=GLASS_BG_COLOR,
            border_width=GLASS_BORDER_WIDTH,
            border_color=COLOR_GLASS_BORDER,
            **kwargs,
        )
        self.pack_propagate(False)  # جلوگیری از تغییر خودکار سایز

        # عنوان کارت
        self.lbl_title = ctk.CTkLabel(
            self,
            text=title,
            font=(FONT_FAMILY_PRIMARY, FONT_SIZE_CAPTION),
            text_color=COLOR_TEXT_LILAC,
        )
        self.lbl_title.pack(pady=(20, 5), padx=20, anchor="w")

        # مقدار عددی / اصلی
        self.lbl_value = ctk.CTkLabel(
            self,
            text=value,
            font=(FONT_FAMILY_PRIMARY, 32, FONT_WEIGHT_BOLD),
            text_color=value_color,
        )
        self.lbl_value.pack(pady=(0, 10), padx=20, anchor="w")

        # آیکون یا توضیح کوچک (اختیاری)
        if icon:
            self.lbl_icon = ctk.CTkLabel(
                self,
                text=icon,
                font=(FONT_FAMILY_PRIMARY, 16),
                text_color=COLOR_ACCENT_CYBER_PURPLE,
            )
            self.lbl_icon.pack(pady=(0, 15), padx=20, anchor="e")

    def update_value(self, new_value: str, color: str = COLOR_TEXT_WHITE):
        """
        به‌روزرسانی مقدار عددی کارت (مثلاً تعداد حاضرین).
        """
        self.lbl_value.configure(text=new_value, text_color=color)


class GlowButton(ctk.CTkButton):
    """
    دکمه با افکت نورانی و رنگ-gradient-like.
    customtkinter پشتیبانی مستقیم از gradient ندارد، بنابراین از رنگ‌های ثابت
    با border свеcą استفاده می‌شود.
    """

    def __init__(
        self,
        master,
        text: str,
        command=None,
        fg_color: str = COLOR_ACCENT_CYBER_PURPLE,
        hover_color: str = COLOR_TEXT_LILAC,
        text_color: str = COLOR_TEXT_WHITE,
        width: int = 200,
        height: int = 50,
        font_size: int = 16,
        icon: str = "",
        **kwargs,
    ):
        full_text = f"{icon}  {text}" if icon else text
        super().__init__(
            master,
            text=full_text,
            command=command,
            width=width,
            height=height,
            corner_radius=GLASS_CORNER_RADIUS,
            fg_color=fg_color,
            hover_color=hover_color,
            text_color=text_color,
            font=(FONT_FAMILY_PRIMARY, font_size, FONT_WEIGHT_BOLD),
            border_width=2,
            border_color=COLOR_TEXT_WHITE,
            **kwargs,
        )


class DigitalClock(ctk.CTkLabel):
    """
    ساعت دیجیتال بزرگ با فرمت فارسی و به‌روزرسانی毎 ثانية.
    """

    def __init__(self, master, **kwargs):
        super().__init__(
            master,
            font=(FONT_FAMILY_PRIMARY, FONT_SIZE_CLOCK, FONT_WEIGHT_BOLD),
            text_color=COLOR_ACCENT_CYBER_PURPLE,
            **kwargs,
        )
        self._update_clock()

    def _update_clock(self):
        now = datetime.now()
        # فرمت: HH:MM:SS
        time_str = now.strftime("%H:%M:%S")
        date_str = now.strftime("%Y/%m/%d")
        self.configure(text=f"{time_str}\n{date_str}")
        self.after(1000, self._update_clock)


class StudentRow(ctk.CTkFrame):
    """
    سطر یک دانش‌آموز در لیست با وضعیت رنگی و دکمه تغییر وضعیت.
    """

    def __init__(
        self,
        master,
        student: Dict,
        on_status_change,
        on_sms_send,
        **kwargs,
    ):
        super().__init__(
            master,
            corner_radius=16,
            fg_color=GLASS_BG_COLOR,
            border_width=1,
            border_color=COLOR_GLASS_BORDER,
            **kwargs,
        )
        self.student = student
        self.on_status_change = on_status_change
        self.on_sms_send = on_sms_send

        self._build_ui()

    def _build_ui(self):
        # نام و پایه
        self.lbl_name = ctk.CTkLabel(
            self,
            text=self.student.get("full_name", "نام نشده"),
            font=(FONT_FAMILY_PRIMARY, 15, FONT_WEIGHT_BOLD),
            text_color=COLOR_TEXT_WHITE,
            anchor="w",
        )
        self.lbl_name.grid(row=0, column=0, padx=15, pady=(10, 2), sticky="w")

        self.lbl_grade = ctk.CTkLabel(
            self,
            text=f"پایه {self.student.get('grade', '-')}  |  موبایل: {self.student.get('mobile', '-')}",
            font=(FONT_FAMILY_PRIMARY, 12),
            text_color=COLOR_TEXT_LILAC,
            anchor="w",
        )
        self.lbl_grade.grid(row=1, column=0, padx=15, pady=(0, 10), sticky="w")

        # چسب وضعیت (status chip)
        status = self.student.get("status", "absent")
        status_color = COLOR_SUCCESS_AQUA if status == "present" else COLOR_ALERT_CORAL
        status_text = "حاضر" if status == "present" else "غایب"

        self.chip_status = ctk.CTkLabel(
            self,
            text=status_text,
            font=(FONT_FAMILY_PRIMARY, 12, FONT_WEIGHT_BOLD),
            text_color=COLOR_BG_DEEP_VELVET,
            fg_color=status_color,
            corner_radius=12,
            width=80,
            height=26,
        )
        self.chip_status.grid(row=0, column=1, rowspan=2, padx=15, pady=10)

        # دکمه تغییر وضعیت (ورود/خروج)
        btn_text = "ثبت خروج" if status == "present" else "ثبت ورود"
        btn_color = COLOR_ALERT_CORAL if status == "present" else COLOR_SUCCESS_AQUA

        self.btn_toggle = GlowButton(
            self,
            text=btn_text,
            command=self._toggle_status,
            fg_color=btn_color,
            hover_color=COLOR_TEXT_LILAC,
            width=120,
            height=36,
            font_size=13,
        )
        self.btn_toggle.grid(row=0, column=2, rowspan=2, padx=15, pady=10)

        # دکمه پیامک
        sms_color = COLOR_ACCENT_CYBER_PURPLE
        if self.student.get("sms_sent", False):
            sms_color = "#4A4A4A"  # خاکستری اگر قبلاً ارسال شده

        self.btn_sms = GlowButton(
            self,
            text="پیامک",
            command=self._send_sms,
            fg_color=sms_color,
            hover_color=COLOR_TEXT_LILAC,
            width=90,
            height=36,
            font_size=13,
            icon="✉",
        )
        self.btn_sms.grid(row=0, column=3, rowspan=2, padx=15, pady=10)

        self.grid_columnconfigure(0, weight=1)

    def _toggle_status(self):
        current = self.student.get("status", "absent")
        new_status = "present" if current == "absent" else "absent"
        self.on_status_change(self.student["id"], new_status)

    def _send_sms(self):
        self.on_sms_send(self.student["id"])

    def update_status(self, student: Dict):
        """
        به‌روزرسانی Visual سطر بعد از تغییر وضعیت در پایگاه داده.
        """
        self.student = student
        status = student.get("status", "absent")
        status_color = COLOR_SUCCESS_AQUA if status == "present" else COLOR_ALERT_CORAL
        status_text = "حاضر" if status == "present" else "غایب"

        self.chip_status.configure(text=status_text, fg_color=status_color)

        btn_text = "ثبت خروج" if status == "present" else "ثبت ورود"
        btn_color = COLOR_ALERT_CORAL if status == "present" else COLOR_SUCCESS_AQUA
        self.btn_toggle.configure(text=btn_text, fg_color=btn_color)

        sms_color = COLOR_ACCENT_CYBER_PURPLE
        if student.get("sms_sent", False):
            sms_color = "#4A4A4A"
        self.btn_sms.configure(fg_color=sms_color)


class SettingsButton(GlowButton):
    """
    دکمه تنظیمات با استایل شیشه‌ای secondaire.
    """

    def __init__(self, master, command=None, **kwargs):
        super().__init__(
            master,
            text="تنظیمات",
            command=command,
            fg_color="#2A1F40",
            hover_color=COLOR_GLASS_BORDER,
            text_color=COLOR_TEXT_LILAC,
            width=140,
            height=40,
            font_size=14,
            icon="⚙",
            **kwargs,
        )


class HistoryButton(GlowButton):
    """
    دکمه تاریخچه/غیرفعال‌ها با استایل شیشه‌ای secondaire.
    """

    def __init__(self, master, command=None, **kwargs):
        super().__init__(
            master,
            text="تاریخچه",
            command=command,
            fg_color="#2A1F40",
            hover_color=COLOR_GLASS_BORDER,
            text_color=COLOR_TEXT_LILAC,
            width=140,
            height=40,
            font_size=14,
            icon="📜",
            **kwargs,
        )
