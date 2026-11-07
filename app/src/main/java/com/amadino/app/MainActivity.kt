package com.amadino.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amadino.app.ui.HistoryDateUiModel
import com.amadino.app.ui.MainViewModel
import com.amadino.app.ui.ShiftUiModel
import com.amadino.app.ui.StudentUiModel
import com.amadino.app.ui.components.*
import com.amadino.app.ui.theme.*
import com.amadino.app.utils.generateCsvString
import com.amadino.app.utils.toPersianDigits

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val uiState by viewModel.uiState.collectAsState()

            AmadinoTheme(themeName = uiState.themeName) {
                val colors = LocalAppColors.current
                Surface(modifier = Modifier.fillMaxSize(), color = colors.background) {
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
    val colors = LocalAppColors.current

    var showAddDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showShiftsManagerDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var selectedProfileStudent by remember { mutableStateOf<StudentUiModel?>(null) }

    var showStudentSelectDialog by remember { mutableStateOf(false) }
    var showShiftSelectDialog by remember { mutableStateOf(false) }

    val activeShift = uiState.shifts.find { it.id == uiState.activeShiftId }
    val activeStudent = uiState.students.find { it.id == uiState.selectedStudentId }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = colors.accent) {
                Text("+", fontSize = 24.sp, color = Color.White)
            }
        },
        containerColor = colors.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(colors.background)
                .verticalScroll(rememberScrollState())
        ) {
            BrandHeader()

            // Top Buttons Row (تنظیم وزن‌ها برای نمایش بهتر)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GlassButton("تاریخچه", onClick = { showHistoryDialog = true }, modifier = Modifier.weight(1f))
                GlassButton("تنظیمات", onClick = { showSettingsDialog = true }, modifier = Modifier.weight(1f))
                GlassButton("مدیریت شیفت‌ها", onClick = { showShiftsManagerDialog = true }, modifier = Modifier.weight(1.5f))
            }

            Spacer(modifier = Modifier.height(16.dp))
            DashboardStatsRow(stats = uiState.stats)

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GlassCard(modifier = Modifier.weight(1f).clickable { showShiftSelectDialog = true }) {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("شیفت فعال", color = colors.textPrimary.copy(alpha = 0.7f), fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = activeShift?.time ?: "انتخاب نشده",
                            color = colors.accent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                GlassCard(modifier = Modifier.weight(1f).clickable { showStudentSelectDialog = true }) {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("دانش‌آموز", color = colors.textPrimary.copy(alpha = 0.7f), fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = activeStudent?.name ?: "انتخاب نشده",
                            color = colors.success,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val isEnabled = uiState.selectedStudentId != null && uiState.activeShiftId != null
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GradientButton(
                        text = "ثبت ورود", iconText = "✔",
                        gradientColors = listOf(colors.success, colors.success.copy(alpha = 0.7f)),
                        modifier = Modifier.weight(1f),
                        onClick = { if (isEnabled) viewModel.performAction("CHECK_IN") else Toast.makeText(context, "ابتدا شیفت و دانش‌آموز را انتخاب کنید", Toast.LENGTH_SHORT).show() }
                    )
                    GradientButton(
                        text = "ثبت خروج", iconText = "🚪",
                        gradientColors = listOf(Color(0xFFFF9800), colors.alert),
                        modifier = Modifier.weight(1f),
                        onClick = { if (isEnabled) viewModel.performAction("CHECK_OUT") else Toast.makeText(context, "ابتدا شیفت و دانش‌آموز را انتخاب کنید", Toast.LENGTH_SHORT).show() }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GradientButton(
                        text = "ثبت غیبت", iconText = "🚫",
                        gradientColors = listOf(colors.alert, Color(0xFFC62828)),
                        modifier = Modifier.weight(1f),
                        onClick = { if (isEnabled) viewModel.performAction("MARK_ABSENT") else Toast.makeText(context, "ابتدا شیفت و دانش‌آموز را انتخاب کنید", Toast.LENGTH_SHORT).show() }
                    )
                    GradientButton(
                        text = "پیامک ورود", iconText = "✉",
                        gradientColors = listOf(colors.accent, colors.accent.copy(alpha = 0.7f)),
                        modifier = Modifier.weight(1f),
                        onClick = { if (isEnabled) viewModel.sendSmsToSelected("IN") { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() } else Toast.makeText(context, "ابتدا یک دانش‌آموز را انتخاب کنید", Toast.LENGTH_SHORT).show() }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GradientButton(
                        text = "پیامک خروج", iconText = "✉",
                        gradientColors = listOf(Color(0xFFE91E63), Color(0xFFC2185B)),
                        modifier = Modifier.weight(1f),
                        onClick = { if (isEnabled) viewModel.sendSmsToSelected("OUT") { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() } else Toast.makeText(context, "ابتدا یک دانش‌آموز را انتخاب کنید", Toast.LENGTH_SHORT).show() }
                    )
                    GradientButton(
                        text = "ثبت نهایی", iconText = "📋",
                        gradientColors = listOf(colors.accent, colors.accent.copy(alpha = 0.7f)),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.finalizeDay { success -> Toast.makeText(context, if(success) "ثبت نهایی شد" else "خطا", Toast.LENGTH_SHORT).show() } }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // --- All Dialogs ---
    if (showStudentSelectDialog) {
        SelectStudentDialog(
            students = uiState.students,
            selectedId = uiState.selectedStudentId,
            onSelect = { id -> viewModel.selectStudent(id); showStudentSelectDialog = false },
            onProfile = { student -> selectedProfileStudent = student },
            onDismiss = { showStudentSelectDialog = false }
        )
    }

    if (showShiftSelectDialog) {
        SelectShiftDialog(
            shifts = uiState.shifts,
            activeShiftId = uiState.activeShiftId,
            onSelect = { id -> viewModel.selectShift(id); showShiftSelectDialog = false },
            onDismiss = { showShiftSelectDialog = false }
        )
    }

    if (showAddDialog) {
        AddStudentDialog(onDismiss = { showAddDialog = false }, onAdd = { name, phone, className, school, field -> viewModel.addStudent(name, phone, className, school, field); showAddDialog = false })
    }

    if (showSettingsDialog) {
        SettingsDialog(currentTemplateIn = uiState.smsTemplateIn, currentTemplateOut = uiState.smsTemplateOut, currentTheme = uiState.themeName, onThemeChange = { theme -> viewModel.changeTheme(theme) }, onDismiss = { showSettingsDialog = false }, onSaveTemplates = { inT, outT -> viewModel.saveSmsTemplates(inT, outT); showSettingsDialog = false }, onClearData = { viewModel.clearAllData { Toast.makeText(context, "تمام داده‌ها پاک شدند", Toast.LENGTH_SHORT).show() }; showSettingsDialog = false })
    }

    if (showShiftsManagerDialog) {
        ManageShiftsDialog(shifts = uiState.shifts, onDismiss = { showShiftsManagerDialog = false }, onAddShift = { time -> viewModel.addShift(time) }, onUpdateShift = { id, time -> viewModel.updateShift(id, time) }, onDeleteShift = { id -> viewModel.deleteShift(id) })
    }

    if (showHistoryDialog) {
        HistoryDialog(historyData = uiState.history, onDismiss = { showHistoryDialog = false })
    }

    if (selectedProfileStudent != null) {
        StudentProfileDialog(student = selectedProfileStudent!!, onDismiss = { selectedProfileStudent = null })
    }
}

// ==========================================
// SELECTION DIALOGS
// ==========================================

@Composable
fun SelectStudentDialog(
    students: List<StudentUiModel>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    onProfile: (StudentUiModel) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalAppColors.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.background,
        title = { Text("انتخاب دانش‌آموز", color = colors.textPrimary, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right) },
        text = {
            if (students.isEmpty()) {
                Text("لیست خالی است. ابتدا دانش‌آموز اضافه کنید.", color = colors.textPrimary, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 350.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(students) { student ->
                        val isSelected = student.id == selectedId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isSelected) colors.accent.copy(alpha = 0.2f) else colors.glassSurface, RoundedCornerShape(10.dp))
                                .border(1.dp, if (isSelected) colors.accent else Color.Transparent, RoundedCornerShape(10.dp))
                                .clickable { onSelect(student.id) }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(student.name, color = colors.textPrimary, fontSize = 14.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)

                                val statusBadge = when {
                                    student.currentCheckOut != null -> "خارج شده (${student.currentCheckOut})" to colors.textPrimary
                                    student.currentStatus == "PRESENT" -> "حاضر" to colors.success
                                    student.currentStatus == "ABSENT" -> "غایب" to colors.alert
                                    student.currentStatus == "LATE" -> "تاخیر" to Color(0xFFFFC400)
                                    else -> null
                                }
                                if (statusBadge != null) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(modifier = Modifier.background(statusBadge.second.copy(alpha = 0.2f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                        Text(statusBadge.first, color = statusBadge.second, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Box(
                                modifier = Modifier.size(24.dp).background(colors.textPrimary.copy(alpha = 0.1f), CircleShape).clickable { onProfile(student) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("ℹ️", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("بستن", color = colors.textPrimary) } }
    )
}

@Composable
fun SelectShiftDialog(
    shifts: List<ShiftUiModel>,
    activeShiftId: String?,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalAppColors.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.background,
        title = { Text("انتخاب شیفت فعال", color = colors.textPrimary, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right) },
        text = {
            if (shifts.isEmpty()) {
                Text("هیچ شیفتی ثبت نشده است.", color = colors.textPrimary, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(shifts) { shift ->
                        val isActive = shift.id == activeShiftId
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isActive) colors.accent.copy(alpha = 0.2f) else colors.glassSurface, RoundedCornerShape(8.dp))
                                .border(1.dp, if (isActive) colors.accent else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable { onSelect(shift.id) }
                                .padding(16.dp)
                        ) {
                            Text(shift.time, color = if (isActive) colors.accent else colors.textPrimary, fontSize = 14.sp, fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("بستن", color = colors.textPrimary) } }
    )
}

// ==========================================
// OTHER DIALOGS
// ==========================================

@Composable
fun SettingsDialog(
    currentTemplateIn: String,
    currentTemplateOut: String,
    currentTheme: String,
    onThemeChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSaveTemplates: (String, String) -> Unit,
    onClearData: () -> Unit
) {
    val colors = LocalAppColors.current
    var templateIn by remember { mutableStateOf(currentTemplateIn) }
    var templateOut by remember { mutableStateOf(currentTemplateOut) }
    var showClearConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.background,
        title = { Text("تنظیمات برنامه", color = colors.textPrimary) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("انتخاب تم رنگی:", color = colors.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ThemeChip("بنفش سایبری", "CyberViolet", currentTheme, onThemeChange)
                    ThemeChip("روشن خورشیدی", "SolarDawn", currentTheme, onThemeChange)
                    ThemeChip("سبز ماتریکس", "EmeraldMatrix", currentTheme, onThemeChange)
                }

                Divider(color = colors.textPrimary.copy(alpha = 0.2f))

                Text("قالب پیامک ورود:", color = colors.textPrimary, fontSize = 14.sp)
                OutlinedTextField(
                    value = templateIn,
                    onValueChange = { templateIn = it },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary, unfocusedTextColor = colors.textPrimary,
                        focusedBorderColor = colors.accent, unfocusedBorderColor = colors.textPrimary.copy(alpha = 0.5f)
                    )
                )

                Text("قالب پیامک خروج:", color = colors.textPrimary, fontSize = 14.sp)
                OutlinedTextField(
                    value = templateOut,
                    onValueChange = { templateOut = it },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary, unfocusedTextColor = colors.textPrimary,
                        focusedBorderColor = colors.accent, unfocusedBorderColor = colors.textPrimary.copy(alpha = 0.5f)
                    )
                )

                Text("متغیرهای مجاز: {name} (نام)، {date} (تاریخ)، {time} (ساعت)", color = colors.textPrimary.copy(alpha = 0.7f), fontSize = 12.sp)

                TextButton(
                    onClick = { showClearConfirm = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("پاک کردن تمام داده‌ها (Reset)", color = colors.alert)
                }
                Text("نسخه برنامه: ۱.۲.۴", color = colors.textPrimary, fontSize = 12.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = { onSaveTemplates(templateIn, templateOut) }) {
                Text("ذخیره", color = colors.accent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("بستن", color = colors.textPrimary) }
        }
    )

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            containerColor = colors.background,
            title = { Text("هشدار", color = colors.alert) },
            text = { Text("آیا مطمئن هستید؟ تمام دانش‌آموزان و سوابق پاک خواهند شد.", color = colors.textPrimary) },
            confirmButton = {
                TextButton(onClick = { onClearData(); showClearConfirm = false }) {
                    Text("بله، پاک کن", color = colors.alert)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) { Text("انصراف", color = colors.textPrimary) }
            }
        )
    }
}

@Composable
fun ThemeChip(label: String, themeId: String, currentTheme: String, onThemeSelected: (String) -> Unit) {
    val colors = LocalAppColors.current
    val isSelected = themeId == currentTheme
    Box(
        modifier = Modifier
            .background(if (isSelected) colors.accent else colors.glassSurface, RoundedCornerShape(8.dp))
            .border(1.dp, if (isSelected) colors.accent else colors.textPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .clickable { onThemeSelected(themeId) }
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(label, color = if (isSelected) Color.White else colors.textPrimary, fontSize = 12.sp)
    }
}

@Composable
fun HistoryDialog(
    historyData: List<HistoryDateUiModel>,
    onDismiss: () -> Unit
) {
    val colors = LocalAppColors.current
    val context = LocalContext.current
    var expandedDate by remember { mutableStateOf<String?>(null) }

    val createCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            try {
                val csvContent = generateCsvString(historyData)
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(csvContent.toByteArray(Charsets.UTF_8))
                }
                Toast.makeText(context, "فایل با موفقیت ذخیره شد", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "خطا در ذخیره فایل", Toast.LENGTH_SHORT).show()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.background,
        title = { Text("تاریخچه حضور و غیاب", color = colors.textPrimary) },
        text = {
            Column {
                Button(
                    onClick = { createCsvLauncher.launch("Amadino_Report_${System.currentTimeMillis()}.csv") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.success)
                ) {
                    Text("خروجی اکسل (CSV)", color = if(colors.isLight) Color.White else colors.background, fontWeight = FontWeight.Bold)
                }

                if (historyData.isEmpty()) {
                    Text("هیچ سابقه‌ای ثبت نشده است.", color = colors.textPrimary)
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 350.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(historyData) { dateItem ->
                            val isExpanded = expandedDate == dateItem.date
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(colors.glassSurface, RoundedCornerShape(8.dp))
                                        .border(1.dp, colors.accent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .clickable { expandedDate = if (isExpanded) null else dateItem.date }
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(dateItem.date, color = colors.textPrimary, fontWeight = FontWeight.Bold)
                                        Text(if (isExpanded) "▲" else "▼", color = colors.textPrimary)
                                    }
                                }

                                if (isExpanded) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        dateItem.records.forEach { record ->
                                            val statusColor = when(record.status) {
                                                "حاضر" -> colors.success
                                                "غایب" -> colors.alert
                                                "تاخیر" -> colors.textPrimary
                                                else -> colors.textPrimary
                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(record.studentName, color = colors.textPrimary, fontSize = 14.sp)
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Text(record.status, color = statusColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                                    Text("(${record.time})", color = colors.textPrimary.copy(alpha = 0.7f), fontSize = 12.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("بستن", color = colors.textPrimary) } }
    )
}

@Composable
fun ManageShiftsDialog(
    shifts: List<ShiftUiModel>,
    onDismiss: () -> Unit,
    onAddShift: (String) -> Unit,
    onUpdateShift: (String, String) -> Unit,
    onDeleteShift: (String) -> Unit
) {
    val colors = LocalAppColors.current
    var newShiftTime by remember { mutableStateOf("") }
    var editingShift by remember { mutableStateOf<ShiftUiModel?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<ShiftUiModel?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.background,
        title = { Text("مدیریت شیفت‌ها", color = colors.textPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newShiftTime,
                        onValueChange = { newShiftTime = it },
                        label = { Text("شیفت جدید (08:00 - 09:00)", color = colors.textPrimary.copy(alpha = 0.7f)) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colors.textPrimary, unfocusedTextColor = colors.textPrimary,
                            focusedBorderColor = colors.accent, unfocusedBorderColor = colors.textPrimary.copy(alpha = 0.5f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if (newShiftTime.isNotBlank()) { onAddShift(newShiftTime); newShiftTime = "" } },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                    ) {
                        Text("افزودن", color = Color.White)
                    }
                }

                Divider(color = colors.textPrimary.copy(alpha = 0.2f))

                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(shifts) { shift ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(shift.time, color = colors.textPrimary)
                            Row {
                                TextButton(onClick = { editingShift = shift }) { Text("✎", color = colors.success, fontSize = 20.sp) }
                                TextButton(onClick = { showDeleteConfirm = shift }) { Text("🗑", color = colors.alert, fontSize = 20.sp) }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("بستن", color = colors.textPrimary) } }
    )

    if (editingShift != null) {
        var editedTime by remember { mutableStateOf(editingShift!!.time) }
        AlertDialog(
            onDismissRequest = { editingShift = null },
            containerColor = colors.background,
            title = { Text("ویرایش شیفت", color = colors.textPrimary) },
            text = {
                OutlinedTextField(
                    value = editedTime, onValueChange = { editedTime = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary, unfocusedTextColor = colors.textPrimary,
                        focusedBorderColor = colors.accent, unfocusedBorderColor = colors.textPrimary.copy(alpha = 0.5f)
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { onUpdateShift(editingShift!!.id, editedTime); editingShift = null }) { Text("ذخیره", color = colors.accent) }
            },
            dismissButton = { TextButton(onClick = { editingShift = null }) { Text("انصراف", color = colors.textPrimary) } }
        )
    }

    if (showDeleteConfirm != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            containerColor = colors.background,
            title = { Text("حذف شیفت", color = colors.alert) },
            text = { Text("آیا از حذف شیفت ${showDeleteConfirm!!.time} مطمئن هستید؟", color = colors.textPrimary) },
            confirmButton = {
                TextButton(onClick = { onDeleteShift(showDeleteConfirm!!.id); showDeleteConfirm = null }) { Text("حذف", color = colors.alert) }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = null }) { Text("انصراف", color = colors.textPrimary) } }
        )
    }
}

@Composable
fun AddStudentDialog(onDismiss: () -> Unit, onAdd: (String, String, String, String, String) -> Unit) {
    val colors = LocalAppColors.current
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("") }
    var school by remember { mutableStateOf("") }
    var field by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.background,
        title = { Text("افزودن دانش‌آموز جدید", color = colors.textPrimary) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("نام و نام خانوادگی", color = colors.textPrimary.copy(alpha = 0.7f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary, unfocusedTextColor = colors.textPrimary,
                        focusedBorderColor = colors.accent, unfocusedBorderColor = colors.textPrimary.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone, onValueChange = { phone = it },
                    label = { Text("شماره موبایل والدین", color = colors.textPrimary.copy(alpha = 0.7f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary, unfocusedTextColor = colors.textPrimary,
                        focusedBorderColor = colors.accent, unfocusedBorderColor = colors.textPrimary.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = className, onValueChange = { className = it },
                    label = { Text("کلاس (مثلاً دهم)", color = colors.textPrimary.copy(alpha = 0.7f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary, unfocusedTextColor = colors.textPrimary,
                        focusedBorderColor = colors.accent, unfocusedBorderColor = colors.textPrimary.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = school, onValueChange = { school = it },
                    label = { Text("دبیرستان", color = colors.textPrimary.copy(alpha = 0.7f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary, unfocusedTextColor = colors.textPrimary,
                        focusedBorderColor = colors.accent, unfocusedBorderColor = colors.textPrimary.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = field, onValueChange = { field = it },
                    label = { Text("رشته تحصیلی", color = colors.textPrimary.copy(alpha = 0.7f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary, unfocusedTextColor = colors.textPrimary,
                        focusedBorderColor = colors.accent, unfocusedBorderColor = colors.textPrimary.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank() && phone.isNotBlank()) {
                    onAdd(name, phone, className, school, field)
                }
            }) { Text("ثبت", color = colors.accent) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف", color = colors.textPrimary) } }
    )
}

@Composable
fun StudentProfileDialog(student: StudentUiModel, onDismiss: () -> Unit) {
    val colors = LocalAppColors.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.background,
        title = { Text("پروفایل دانش‌آموز", color = colors.textPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ProfileRow("نام و نام خانوادگی", student.name, colors.textPrimary)
                ProfileRow("موبایل والدین", toPersianDigits(student.phone), colors.textPrimary.copy(alpha = 0.8f))
                ProfileRow("کلاس", student.className, colors.accent)
                ProfileRow("دبیرستان", student.school, colors.accent)
                ProfileRow("رشته تحصیلی", student.field, colors.accent)
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("بستن", color = colors.textPrimary) } }
    )
}

@Composable
fun ProfileRow(label: String, value: String, valueColor: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = LocalAppColors.current.textPrimary.copy(alpha = 0.7f), fontSize = 14.sp)
        Text(value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}