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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amadino.app.ui.HistoryDateUiModel
import com.amadino.app.ui.MainViewModel
import com.amadino.app.ui.ShiftUiModel
import com.amadino.app.ui.components.*
import com.amadino.app.ui.theme.*
import com.amadino.app.utils.toPersianDigits

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
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showShiftsDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) } // New state for History

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

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Connected to History Dialog
                GlassButton("تاریخچه و غیرفعال‌ها", onClick = { showHistoryDialog = true })
                GlassButton("مدیریت شیفت‌ها", onClick = { showShiftsDialog = true })
                GlassButton("تنظیمات", onClick = { showSettingsDialog = true })
            }

            Spacer(modifier = Modifier.height(16.dp))
            DashboardStatsRow(stats = uiState.stats)

            Spacer(modifier = Modifier.height(16.dp))
            Text("شیفت‌های فعال امروز:", color = SoftLilac, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 16.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.shifts) { shift ->
                    Box(
                        modifier = Modifier
                            .background(GlassSurface, RoundedCornerShape(8.dp))
                            .border(1.dp, ElectricAqua.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(shift.time, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GradientButton(
                        text = "پیامک ورود", iconText = "✉",
                        gradientColors = listOf(CyberPurple, Color(0xFF9D4EDD)),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (isEnabled) viewModel.sendSmsToSelected("IN") { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
                            else Toast.makeText(context, "ابتدا یک دانش‌آموز را انتخاب کنید", Toast.LENGTH_SHORT).show()
                        }
                    )
                    GradientButton(
                        text = "پیامک خروج", iconText = "✉",
                        gradientColors = listOf(Color(0xFFE91E63), Color(0xFFC2185B)),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (isEnabled) viewModel.sendSmsToSelected("OUT") { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
                            else Toast.makeText(context, "ابتدا یک دانش‌آموز را انتخاب کنید", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

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
            onAdd = { name, phone, className, school, field ->
                viewModel.addStudent(name, phone, className, school, field)
                showAddDialog = false
            }
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            currentTemplateIn = uiState.smsTemplateIn,
            currentTemplateOut = uiState.smsTemplateOut,
            onDismiss = { showSettingsDialog = false },
            onSaveTemplates = { inT, outT -> viewModel.saveSmsTemplates(inT, outT); showSettingsDialog = false },
            onClearData = {
                viewModel.clearAllData {
                    Toast.makeText(context, "تمام داده‌ها پاک شدند", Toast.LENGTH_SHORT).show()
                }
                showSettingsDialog = false
            }
        )
    }

    if (showShiftsDialog) {
        ManageShiftsDialog(
            shifts = uiState.shifts,
            onDismiss = { showShiftsDialog = false },
            onAddShift = { time -> viewModel.addShift(time) },
            onUpdateShift = { id, time -> viewModel.updateShift(id, time) },
            onDeleteShift = { id -> viewModel.deleteShift(id) }
        )
    }

    // --- New History Dialog ---
    if (showHistoryDialog) {
        HistoryDialog(
            historyData = uiState.history,
            onDismiss = { showHistoryDialog = false }
        )
    }
}

@Composable
fun HistoryDialog(
    historyData: List<HistoryDateUiModel>,
    onDismiss: () -> Unit
) {
    var expandedDate by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepVelvetDark,
        title = { Text("تاریخچه حضور و غیاب", color = Color.White) },
        text = {
            if (historyData.isEmpty()) {
                Text("هیچ سابقه‌ای ثبت نشده است.", color = SoftLilac)
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(historyData) { dateItem ->
                        val isExpanded = expandedDate == dateItem.date
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(GlassSurface, RoundedCornerShape(8.dp))
                                    .border(1.dp, CyberPurple.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .clickable { expandedDate = if (isExpanded) null else dateItem.date }
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(dateItem.date, color = Color.White, fontWeight = FontWeight.Bold)
                                    Text(if (isExpanded) "▲" else "▼", color = SoftLilac)
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
                                            "حاضر" -> ElectricAqua
                                            "غایب" -> FieryCoral
                                            "تاخیر" -> SoftLilac
                                            else -> Color.White
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(record.studentName, color = Color.White, fontSize = 14.sp)
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Text(record.status, color = statusColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                                Text("(${record.time})", color = SoftLilac, fontSize = 12.sp)
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
        confirmButton = { TextButton(onClick = onDismiss) { Text("بستن", color = SoftLilac) } }
    )
}

// --- Other Dialogs (ManageShiftsDialog, AddStudentDialog, SettingsDialog) ---
// (کدهای این بخش‌ها همان کدهای قبلی هستند و بدون تغییر باقی می‌مانند)

@Composable
fun ManageShiftsDialog(
    shifts: List<ShiftUiModel>,
    onDismiss: () -> Unit,
    onAddShift: (String) -> Unit,
    onUpdateShift: (String, String) -> Unit,
    onDeleteShift: (String) -> Unit
) {
    var newShiftTime by remember { mutableStateOf("") }
    var editingShift by remember { mutableStateOf<ShiftUiModel?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<ShiftUiModel?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepVelvetDark,
        title = { Text("مدیریت شیفت‌ها", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newShiftTime,
                        onValueChange = { newShiftTime = it },
                        label = { Text("شیفت جدید (08:00 - 09:00)", color = SoftLilac) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                            focusedBorderColor = CyberPurple, unfocusedBorderColor = SoftLilac.copy(alpha = 0.5f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if (newShiftTime.isNotBlank()) { onAddShift(newShiftTime); newShiftTime = "" } },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPurple)
                    ) {
                        Text("افزودن", color = Color.White)
                    }
                }

                Divider(color = SoftLilac.copy(alpha = 0.2f))

                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(shifts) { shift ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(shift.time, color = Color.White)
                            Row {
                                TextButton(onClick = { editingShift = shift }) { Text("✎", color = ElectricAqua, fontSize = 20.sp) }
                                TextButton(onClick = { showDeleteConfirm = shift }) { Text("🗑", color = FieryCoral, fontSize = 20.sp) }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("بستن", color = SoftLilac) } }
    )

    if (editingShift != null) {
        var editedTime by remember { mutableStateOf(editingShift!!.time) }
        AlertDialog(
            onDismissRequest = { editingShift = null },
            containerColor = DeepVelvetDark,
            title = { Text("ویرایش شیفت", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = editedTime, onValueChange = { editedTime = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedBorderColor = CyberPurple, unfocusedBorderColor = SoftLilac.copy(alpha = 0.5f)
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { onUpdateShift(editingShift!!.id, editedTime); editingShift = null }) { Text("ذخیره", color = CyberPurple) }
            },
            dismissButton = { TextButton(onClick = { editingShift = null }) { Text("انصراف", color = SoftLilac) } }
        )
    }

    if (showDeleteConfirm != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            containerColor = DeepVelvetDark,
            title = { Text("حذف شیفت", color = FieryCoral) },
            text = { Text("آیا از حذف شیفت ${showDeleteConfirm!!.time} مطمئن هستید؟", color = Color.White) },
            confirmButton = {
                TextButton(onClick = { onDeleteShift(showDeleteConfirm!!.id); showDeleteConfirm = null }) { Text("حذف", color = FieryCoral) }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = null }) { Text("انصراف", color = SoftLilac) } }
        )
    }
}

@Composable
fun AddStudentDialog(onDismiss: () -> Unit, onAdd: (String, String, String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("") }
    var school by remember { mutableStateOf("") }
    var field by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepVelvetDark,
        title = { Text("افزودن دانش‌آموز جدید", color = Color.White) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("نام و نام خانوادگی", color = SoftLilac) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedBorderColor = CyberPurple, unfocusedBorderColor = SoftLilac.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone, onValueChange = { phone = it },
                    label = { Text("شماره موبایل والدین", color = SoftLilac) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedBorderColor = CyberPurple, unfocusedBorderColor = SoftLilac.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = className, onValueChange = { className = it },
                    label = { Text("کلاس (مثلاً دهم)", color = SoftLilac) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedBorderColor = CyberPurple, unfocusedBorderColor = SoftLilac.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = school, onValueChange = { school = it },
                    label = { Text("دبیرستان", color = SoftLilac) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedBorderColor = CyberPurple, unfocusedBorderColor = SoftLilac.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = field, onValueChange = { field = it },
                    label = { Text("رشته تحصیلی", color = SoftLilac) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedBorderColor = CyberPurple, unfocusedBorderColor = SoftLilac.copy(alpha = 0.5f)
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
            }) { Text("ثبت", color = CyberPurple) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف", color = SoftLilac) } }
    )
}

@Composable
fun SettingsDialog(
    currentTemplateIn: String,
    currentTemplateOut: String,
    onDismiss: () -> Unit,
    onSaveTemplates: (String, String) -> Unit,
    onClearData: () -> Unit
) {
    var templateIn by remember { mutableStateOf(currentTemplateIn) }
    var templateOut by remember { mutableStateOf(currentTemplateOut) }
    var showClearConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepVelvetDark,
        title = { Text("تنظیمات برنامه", color = Color.White) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("قالب پیامک ورود:", color = SoftLilac, fontSize = 14.sp)
                OutlinedTextField(
                    value = templateIn,
                    onValueChange = { templateIn = it },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedBorderColor = CyberPurple, unfocusedBorderColor = SoftLilac.copy(alpha = 0.5f)
                    )
                )

                Text("قالب پیامک خروج:", color = SoftLilac, fontSize = 14.sp)
                OutlinedTextField(
                    value = templateOut,
                    onValueChange = { templateOut = it },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedBorderColor = CyberPurple, unfocusedBorderColor = SoftLilac.copy(alpha = 0.5f)
                    )
                )

                Text("متغیرهای مجاز: {name} (نام)، {date} (تاریخ)، {time} (ساعت)", color = SoftLilac.copy(alpha = 0.7f), fontSize = 12.sp)

                TextButton(
                    onClick = { showClearConfirm = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("پاک کردن تمام داده‌ها (Reset)", color = FieryCoral)
                }
                Text("نسخه برنامه: ۱.۲.۳", color = SoftLilac, fontSize = 12.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = { onSaveTemplates(templateIn, templateOut) }) {
                Text("ذخیره", color = CyberPurple)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("بستن", color = SoftLilac) }
        }
    )

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            containerColor = DeepVelvetDark,
            title = { Text("هشدار", color = FieryCoral) },
            text = { Text("آیا مطمئن هستید؟ تمام دانش‌آموزان و سوابق پاک خواهند شد.", color = Color.White) },
            confirmButton = {
                TextButton(onClick = { onClearData(); showClearConfirm = false }) {
                    Text("بله، پاک کن", color = FieryCoral)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) { Text("انصراف", color = SoftLilac) }
            }
        )
    }
}