package com.amadino.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amadino.app.ui.MainViewModel
import com.amadino.app.ui.components.BrandHeader
import com.amadino.app.ui.components.DashboardStats
import com.amadino.app.ui.components.DashboardStatsRow
import com.amadino.app.ui.components.GlassCard
import com.amadino.app.ui.components.GradientButton
import com.amadino.app.ui.theme.AmadinoTheme
import com.amadino.app.ui.theme.CyberPurple
import com.amadino.app.ui.theme.DeepVelvetDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AmadinoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DeepVelvetDark
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val viewModel: MainViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVelvetDark)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        BrandHeader()

        Spacer(modifier = Modifier.height(16.dp))

        DashboardStatsRow(
            stats = DashboardStats(
                presentCount = 24,
                absentCount = 3,
                smsCount = 27
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "لیست دانش‌آموزان در اینجا قرار می‌گیرد...",
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        GradientButton(
            text = "تایید و ثبت نهایی",
            iconText = "✔", // Using unicode instead of Icons
            gradientColors = listOf(CyberPurple, Color(0xFFE91E63)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            onClick = {
                viewModel.finalizeDay(todayDate) { isSuccess ->
                    if (isSuccess) {
                        Toast.makeText(context, "وضعیت‌های ثبت‌نشده با موفقیت غایب شدند", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "خطا در ثبت نهایی", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}