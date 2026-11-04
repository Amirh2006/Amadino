# main.py
# نقطه ورودی برنامه آمدینو نسخه 1.2.0
# قالب سایبر-بنفش (Cyber-Violet) با افکت شیشه‌ای و ذخیره‌سازی در users.json

import customtkinter as ctk
from ui.main_window import MainWindow


def main():
    """
    راه‌اندازی برنامه آمدینو.
    """
    # فعال‌سازیScaling DPI برای نمایش درست در مانیتورهای با تراکم پیکسل بالا
    ctk.set_widget_scaling(1.0)
    ctk.set_window_scaling(1.0)

    app = MainWindow()
    app.mainloop()


if __name__ == "__main__":
    main()
