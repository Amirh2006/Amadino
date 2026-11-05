package com.amadino.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amadino.app.ui.MainViewModel
import com.amadino.app.ui.components.*
import com.amadino.app.ui.theme.*
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AmadinoTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = DeepVelvetDark) {
                    MainScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = CyberPurple) {
                Text("+", fontSize = 24.sp, color = Color.White)
            }
        },
        containerColor = DeepVelvetDark
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).background(DeepVelvetDark)
        ) {
            BrandHeader()

            // Secondary Buttons (Settings & History)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GlassButton("تاریخچه و غیرفعال‌ها", onClick = { Toast.makeText(context, "بخش تاریخچه", Toast.LENGTH_SHORT).show() })
                GlassButton("تنظیمات", onClick = { Toast.makeText(context, "بخش تنظیمات", Toast.LENGTH_SHORT).show() })
            }

            Spacer(modifier = Modifier.height(16.dp))
            DashboardStatsRow(stats = uiState.stats)
            Spacer(modifier = Modifier.height(16.dp))

            // Student List
            Text("لیست دانش‌آموزان", color = SoftLilac, fontSize = 16.sp, modifier = Modifier.padding(horizontal = 16.dp))
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(uiState.students) { student ->
                    val isSelected = student.id == uiState.selectedStudentId
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isSelected) CyberPurple.copy(alpha = 0.3f) else GlassSurface, RoundedCornerShape(12.dp))
                            .border(1.dp, if (isSelected) CyberPurple else Color.Transparent, RoundedCornerShape(12.dp))
                            .clickable { viewModel.selectStudent(if (isSelected) null else student.id) }
                            .padding(16.dp)
                    ) {
                        Text(student.name, color = Color.White, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }

            // Action Panel (Only enabled if a student is selected)
            val isEnabled = uiState.selectedStudentId != null
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GradientButton(
                        text = "ثبت ورود", iconText = "✔",
                        gradientColors = listOf(ElectricAqua, Color(0xFF007BFF)),
                        modifier = Modifier.weight(1f),
                        onClick = { if (isEnabled) viewModel.performAction("CHECK_IN") else Toast.makeText(context, "ابتدا یک دانش‌آموز را انتخاب کنید", Toast.LENGTH_SHORT).show() }
                    )
                    GradientButton(
                        text = "ثبت خروج", iconText = "✖",
                        gradientColors = listOf(FieryCoral, Color(0xFFFF9800)),
                        modifier = Modifier.weight(1f),
                        onClick = { if (isEnabled) viewModel.performAction("CHECK_OUT") else Toast.makeText(context, "ابتدا یک دانش‌آموز را انتخاب کنید", Toast.LENGTH_SHORT).show() }
                    )
                }
                GradientButton(
                    text = "ارسال پیامک به والدین", iconText = "✉",
                    gradientColors = listOf(CyberPurple, Color(0xFFE91E63)),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (isEnabled) viewModel.sendSmsToSelected { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
                        else Toast.makeText(context, "ابتدا یک دانش‌آموز را انتخاب کنید", Toast.LENGTH_SHORT).show()
                    }
                )
                GradientButton(
                    text = "تایید و ثبت نهایی", iconText = "📋",
                    gradientColors = listOf(CyberPurple, Color(0xFF7B2CBF)),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.finalizeDay { success -> Toast.makeText(context, if(success) "ثبت نهایی شد" else "خطا", Toast.LENGTH_SHORT).show() } }
                )
            }
        }
    }

    if (showAddDialog) {
        AddStudentDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, phone -> viewModel.addStudent(name, phone); showAddDialog = false }
        )
    }
}

@Composable
fun AddStudentDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss, containerColor = DeepVelvetDark,
        title = { Text("افزودن دانش‌آموز", color = Color.White) },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("نام", color = SoftLilac) })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("موبایل والدین", color = SoftLilac) })
            }
        },
        confirmButton = { TextButton(onClick = { if (name.isNotBlank() && phone.isNotBlank()) onAdd(name, phone) }) { Text("ثبت", color = CyberPurple) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف", color = SoftLilac) } }
    )
}