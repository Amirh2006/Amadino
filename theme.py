# theme.py
# موضوع رنگ و استایل‌های گرافیکی نسخه 1.2.0 - قالب سایبر-بنفش (Cyber-Violet)
# تمامی رنگ‌ها بر اساس genannte مشخصات Figma انتخاب شده‌اند.

import customtkinter as ctk

# ============================================================================
#}palet رنگ اصلی (Primary Color Palette)
# ============================================================================

# پس‌زمینه اصلی: بنفش ساير قدیمی و تیره (Deep Velvet Dark Purple)
COLOR_BG_DEEP_VELVET = "#0A071B"

# رنگ accents: بنفش سایبری برای هایلایت و لبه کارت‌ها
COLOR_ACCENT_CYBER_PURPLE = "#7B2CBF"

# هشدار/خروج: Coral اژدهایی برای شاخص‌های خروج
COLOR_ALERT_CORAL = "#FF6B35"

# موفقیت/ورود: Aqua برقزی برای ثبت‌ورود و تاییدیه‌ها
COLOR_SUCCESS_AQUA = "#00F5D4"

# טיפوگرافی/جزئیات: لایلاک نرم برای متن و لبه‌های کوچک
COLOR_TEXT_LILAC = "#E0AAFF"

# متن سفید خالص
COLOR_TEXT_WHITE = "#FFFFFF"

# رنگ لبه کانتینر شیشه‌ای (glass container border)
COLOR_GLASS_BORDER = "#4A3F6B"

# رنگ سایه.Internal برای افکت تراکم شیشه‌ای
COLOR_GLASS_SHADOW = "#1A142D"

# رنگ-gradeد-secondary ( graduate بنفش به صورتی)
COLOR_GRADIENT_PURPLE_START = "#7B2CBF"
COLOR_GRADIENT_PURPLE_END = "#B16FF0"

# رنگ-gradeد-teal (آبی-سبز برای دکمه ورود)
COLOR_GRADIENT_TEAL_START = "#00F5D4"
COLOR_GRADIENT_TEAL_END = "#00C4B8"

# رنگ-gradeد-coral (مرمری برای دکمه خروج)
COLOR_GRADIENT_CORAL_START = "#FF6B35"
COLOR_GRADIENT_CORAL_END = "#FF8E5A"

# رنگ-gradeد-purple-pink (بنفش به صورتی برای دکمه پیامک)
COLOR_GRADIENT_SMS_START = "#7B2CBF"
COLOR_GRADIENT_SMS_END = "#FF6BFF"

# ============================================================================
# تنظیمات pareció شیشه‌ای (Glassmorphism Settings)
# ==========================================================================#

# سایز لبه گرد کارت‌ها و دکمه‌ها (Border Radius)
GLASS_CORNER_RADIUS = 24

# ضخامت لبه شیشه‌ای (Border Width)
GLASS_BORDER_WIDTH = 2

# رنگ پس‌زمینه شیشه‌ای با شفافیت simulating
GLASS_BG_COLOR = "#15112E"

# رنگ روی لبه‌ها برای افکت نورانی (Glow)
GLOW_COLOR = "#7B2CBF40"  # بنفش باalpha 40%

# ============================================================================
# فونت‌ها و טיפوگرافی
# ============================================================================

FONT_FAMILY_PRIMARY = "Segoe UI"  # فونت اصلی اینترفیس
FONT_FAMILY_DISPLAY = "Segoe UI"  # فونت نمایشی اعداد و ساعت

FONT_SIZE_TITLE = 28
FONT_SIZE_SUBTITLE = 16
FONT_SIZE_BODY = 14
FONT_SIZE_CAPTION = 12
FONT_SIZE_CLOCK = 48  # ساعت دیجیتال بزرگ

FONT_WEIGHT_BOLD = "bold"
FONT_WEIGHT_NORMAL = "normal"

# ============================================================================
# تنظیمات Layout
# ============================================================================

WINDOW_WIDTH = 1200
WINDOW_HEIGHT = 800
WINDOW_MIN_WIDTH = 1000
WINDOW_MIN_HEIGHT = 700

PADDING_MAIN = 30
PADDING_CARD = 24
GAP_CARD = 20

# ============================================================================
# کلاس استایل مرکزی
# ============================================================================

class CyberVioletTheme:
    """
    کلاس مدیریت استایل قالب سایبر-بنفش.
    همه رنگ‌ها، فونت‌ها و abge measurements در اینجا централизовано هستند.
    """

    def __init__(self):
        # تنظیم مود appearances customtkinter
        ctk.set_appearance_mode("dark")
        ctk.set_default_color_theme("blue")  # پایه، بعداً رنگ‌ها به‌델 Replace می‌شوند

    @staticmethod
    def get_colors() -> dict:
        """
        برگشت دیکشنری تمامی رنگ‌های قالب برای استفاده در کامپوننت‌ها.
        """
        return {
            "bg_deep_velvet": COLOR_BG_DEEP_VELVET,
            "accent_cyber_purple": COLOR_ACCENT_CYBER_PURPLE,
            "alert_coral": COLOR_ALERT_CORAL,
            "success_aqua": COLOR_SUCCESS_AQUA,
            "text_lilac": COLOR_TEXT_LILAC,
            "text_white": COLOR_TEXT_WHITE,
            "glass_border": COLOR_GLASS_BORDER,
            "glass_shadow": COLOR_GLASS_SHADOW,
            "glass_bg": GLASS_BG_COLOR,
            "glow_color": GLOW_COLOR,
            "gradient_purple_start": COLOR_GRADIENT_PURPLE_START,
            "gradient_purple_end": COLOR_GRADIENT_PURPLE_END,
            "gradient_teal_start": COLOR_GRADIENT_TEAL_START,
            "gradient_teal_end": COLOR_GRADIENT_TEAL_END,
            "gradient_coral_start": COLOR_GRADIENT_CORAL_START,
            "gradient_coral_end": COLOR_GRADIENT_CORAL_END,
            "gradient_sms_start": COLOR_GRADIENT_SMS_START,
            "gradient_sms_end": COLOR_GRADIENT_SMS_END,
        }

    @staticmethod
    def apply_window_style(window: ctk.CTk):
        """
        اعمال استایل‌های پایه به پنجره اصلی برنامه.
        """
        window.configure(fg_color=COLOR_BG_DEEP_VELVET)
        window.title("آمدینو | سامانه حضور و غیاب و پیامک")
        window.geometry(f"{WINDOW_WIDTH}x{WINDOW_HEIGHT}")
        window.minsize(WINDOW_MIN_WIDTH, WINDOW_MIN_HEIGHT)
