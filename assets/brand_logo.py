# assets/brand_logo.py
# لگو Neon برند amideeno: intersección کتاب، حرف 'A' و علامت تیک (Checkmark)
# این لگو بدون نیاز به فایل تصویر externe با استفاده از tkinter Canvas رسم می‌شود.
# ابعاد: بنفش سایبری (#7B2CBF) و آkuا الكهربا (#00F5D4) روی پس‌زمینه بنفش ساير قدیمی.

import tkinter as tk
from theme import COLOR_BG_DEEP_VELVET, COLOR_ACCENT_CYBER_PURPLE, COLOR_SUCCESS_AQUA


class NeonLogo(tk.Canvas):
    """
    لگو Neon برندcame: intersección کتاب، حرف 'A' و علامت تیک.
    با اشکال هندسی دکمه‌ای رسم شده است تا در تمامی پلتفرم‌ها قابل نمایش باشد.
    """

    def __init__(
        self,
        master,
        width: int = 120,
        height: int = 120,
        glow_color: str = COLOR_ACCENT_CYBER_PURPLE,
        **kwargs,
    ):
        super().__init__(
            master,
            width=width,
            height=height,
            bg=COLOR_BG_DEEP_VELVET,
            highlightthickness=0,
            **kwargs,
        )
        self.glow_color = glow_color
        self._draw_logo()

    def _draw_logo(self):
        """
        رسم لگو با خطوط هندسی:
        - دایره حاشیه‌ای نورانی
        - حرف 'A' (مثل какرة القلم)
        - علامت تیک در گوشه پایین‌چپ
        - خطوط کتاب (بازوهای گوشه‌دار)
        """
        w, h = 120, 120
        cx, cy = w // 2, h // 2

        # پس‌زمینه دایره‌ای با لبه نورانی
        self.create_oval(
            10, 10, w - 10, h - 10,
            outline=self.glow_color,
            width=3,
            fill="",
        )

        # حرف 'A' (دو خط م شکل و خط افقی وسطی)
        self.create_line(cx - 30, cy + 30, cx, cy - 30, fill=self.glow_color, width=5)
        self.create_line(cx, cy - 30, cx + 30, cy + 30, fill=self.glow_color, width=5)
        self.create_line(cx - 15, cy + 5, cx + 15, cy + 5, fill=self.glow_color, width=4)

        # علامت تیک (checkmark) در گوشه پایین‌وسط
        self.create_line(cx - 25, cy + 10, cx - 10, cy + 25, fill=COLOR_SUCCESS_AQUA, width=6)
        self.create_line(cx - 10, cy + 25, cx + 15, cy - 5, fill=COLOR_SUCCESS_AQUA, width=6)

        # خطوط کتاب (قوس گوشه‌دار در پایین)
        self.create_arc(
            cx - 40, cy - 20, cx + 40, cy + 40,
            start=180, extent=180,
            outline=self.glow_color,
            width=4,
            style="arc",
        )
