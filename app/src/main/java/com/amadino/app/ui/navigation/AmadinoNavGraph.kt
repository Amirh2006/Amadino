package com.amadino.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.amadino.app.di.AppContainer
import com.amadino.app.di.containerViewModelFactory
import com.amadino.app.ui.attendance.AttendanceScreen
import com.amadino.app.ui.attendance.AttendanceViewModel
import com.amadino.app.ui.attendance.ClassTimePickerScreen
import com.amadino.app.ui.attendance.ClassTimePickerViewModel
import com.amadino.app.ui.dashboard.DashboardScreen
import com.amadino.app.ui.dashboard.DashboardViewModel
import com.amadino.app.ui.finance.DebtorsScreen
import com.amadino.app.ui.finance.DebtorsViewModel
import com.amadino.app.ui.finance.PaymentsScreen
import com.amadino.app.ui.finance.PaymentsViewModel
import com.amadino.app.ui.reports.ReportsScreen
import com.amadino.app.ui.reports.ReportsViewModel
import com.amadino.app.ui.settings.ClassTimeFormScreen
import com.amadino.app.ui.settings.ClassTimeFormViewModel
import com.amadino.app.ui.settings.ClassTimeListScreen
import com.amadino.app.ui.settings.ClassTimeListViewModel
import com.amadino.app.ui.settings.ShiftFormScreen
import com.amadino.app.ui.settings.ShiftFormViewModel
import com.amadino.app.ui.settings.ShiftListScreen
import com.amadino.app.ui.settings.ShiftListViewModel
import com.amadino.app.ui.settings.SmsTemplateSettingsScreen
import com.amadino.app.ui.settings.SmsTemplateSettingsViewModel
import com.amadino.app.ui.settings.WeekDaySettingsScreen
import com.amadino.app.ui.settings.WeekDaySettingsViewModel
import com.amadino.app.ui.sms.SmsLogScreen
import com.amadino.app.ui.sms.SmsLogViewModel
import com.amadino.app.ui.students.StudentFormScreen
import com.amadino.app.ui.students.StudentFormViewModel
import com.amadino.app.ui.students.StudentListScreen
import com.amadino.app.ui.students.StudentListViewModel

@Composable
fun AmadinoNavGraph(
    container: AppContainer,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.DASHBOARD
    ) {
        composable(AppRoutes.DASHBOARD) {
            val viewModel: DashboardViewModel = viewModel(
                factory = containerViewModelFactory(container) { appContainer ->
                    DashboardViewModel(
                        shiftRepository = appContainer.shiftRepository,
                        smsRepository = appContainer.smsRepository
                    )
                }
            )
            DashboardScreen(
                viewModel = viewModel,
                onOpenStudents = { navController.navigate(AppRoutes.STUDENT_LIST) },
                onOpenFinance = { navController.navigate(AppRoutes.DEBTORS) },
                onOpenReports = { navController.navigate(AppRoutes.REPORTS) },
                onOpenSmsLog = { navController.navigate(AppRoutes.SMS_LOG) },
                onOpenSettings = { navController.navigate(AppRoutes.WEEK_DAY_SETTINGS) },
                onTakeAttendance = { shiftId ->
                    navController.navigate(
                        AppRoutes.classTimePicker(shiftId, viewModel.selectedDayId.value)
                    )
                }
            )
        }

        composable(AppRoutes.WEEK_DAY_SETTINGS) {
            val viewModel: WeekDaySettingsViewModel = viewModel(
                factory = containerViewModelFactory(container) { appContainer ->
                    WeekDaySettingsViewModel(appContainer.shiftRepository)
                }
            )
            WeekDaySettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onOpenDay = { dayId -> navController.navigate(AppRoutes.shifts(dayId)) }
            )
        }

        composable(
            route = AppRoutes.SHIFT_LIST,
            arguments = listOf(
                navArgument(AppRoutes.ARG_DAY_ID) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val dayId = backStackEntry.arguments?.getInt(AppRoutes.ARG_DAY_ID) ?: return@composable
            val viewModel: ShiftListViewModel = viewModel(
                factory = containerViewModelFactory(container) { appContainer ->
                    ShiftListViewModel(appContainer.shiftRepository, dayId)
                }
            )
            ShiftListScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onAddShift = { navController.navigate(AppRoutes.shiftForm(dayId)) },
                onEditShift = { shiftId ->
                    navController.navigate(AppRoutes.shiftForm(dayId, shiftId))
                },
                onOpenClassTimes = { shiftId ->
                    navController.navigate(AppRoutes.classTimes(shiftId))
                }
            )
        }

        composable(
            route = AppRoutes.SHIFT_FORM,
            arguments = listOf(
                navArgument(AppRoutes.ARG_SHIFT_ID) {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument(AppRoutes.ARG_DAY_ID) {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val dayId = backStackEntry.arguments?.getInt(AppRoutes.ARG_DAY_ID) ?: -1
            if (dayId <= 0) return@composable
            val shiftId = backStackEntry.arguments
                ?.getInt(AppRoutes.ARG_SHIFT_ID)
                ?.takeIf { it > 0 }
            val viewModel: ShiftFormViewModel = viewModel(
                factory = containerViewModelFactory(container) { appContainer ->
                    ShiftFormViewModel(appContainer.shiftRepository, dayId, shiftId)
                }
            )
            ShiftFormScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(
            route = AppRoutes.CLASS_TIME_LIST,
            arguments = listOf(
                navArgument(AppRoutes.ARG_SHIFT_ID) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val shiftId = backStackEntry.arguments?.getInt(AppRoutes.ARG_SHIFT_ID) ?: return@composable
            val viewModel: ClassTimeListViewModel = viewModel(
                factory = containerViewModelFactory(container) { appContainer ->
                    ClassTimeListViewModel(appContainer.shiftRepository, shiftId)
                }
            )
            ClassTimeListScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onAddClassTime = { navController.navigate(AppRoutes.classTimeForm(shiftId)) },
                onEditClassTime = { classTimeId ->
                    navController.navigate(AppRoutes.classTimeForm(shiftId, classTimeId))
                }
            )
        }

        composable(
            route = AppRoutes.CLASS_TIME_FORM,
            arguments = listOf(
                navArgument(AppRoutes.ARG_CLASS_TIME_ID) {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument(AppRoutes.ARG_SHIFT_ID) {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val shiftId = backStackEntry.arguments?.getInt(AppRoutes.ARG_SHIFT_ID) ?: -1
            if (shiftId <= 0) return@composable
            val classTimeId = backStackEntry.arguments
                ?.getInt(AppRoutes.ARG_CLASS_TIME_ID)
                ?.takeIf { it > 0 }
            val viewModel: ClassTimeFormViewModel = viewModel(
                factory = containerViewModelFactory(container) { appContainer ->
                    ClassTimeFormViewModel(appContainer.shiftRepository, shiftId, classTimeId)
                }
            )
            ClassTimeFormScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.STUDENT_LIST) {
            val viewModel: StudentListViewModel = viewModel(
                factory = containerViewModelFactory(container) { appContainer ->
                    StudentListViewModel(appContainer.studentRepository)
                }
            )
            StudentListScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onAddStudent = { navController.navigate(AppRoutes.studentForm()) },
                onEditStudent = { studentId ->
                    navController.navigate(AppRoutes.studentForm(studentId))
                }
            )
        }

        composable(
            route = AppRoutes.STUDENT_FORM,
            arguments = listOf(
                navArgument(AppRoutes.ARG_STUDENT_ID) {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments
                ?.getInt(AppRoutes.ARG_STUDENT_ID)
                ?.takeIf { it > 0 }
            val viewModel: StudentFormViewModel = viewModel(
                factory = containerViewModelFactory(container) { appContainer ->
                    StudentFormViewModel(appContainer.studentRepository, studentId)
                }
            )
            StudentFormScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(
            route = AppRoutes.CLASS_TIME_PICKER,
            arguments = listOf(
                navArgument(AppRoutes.ARG_SHIFT_ID) { type = NavType.IntType },
                navArgument(AppRoutes.ARG_DAY_ID) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val shiftId = backStackEntry.arguments?.getInt(AppRoutes.ARG_SHIFT_ID) ?: return@composable
            val dayId = backStackEntry.arguments?.getInt(AppRoutes.ARG_DAY_ID) ?: return@composable
            val viewModel: ClassTimePickerViewModel = viewModel(
                factory = containerViewModelFactory(container) { appContainer ->
                    ClassTimePickerViewModel(appContainer.shiftRepository, shiftId)
                }
            )
            ClassTimePickerScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSelectClassTime = { classTimeId ->
                    navController.navigate(AppRoutes.attendance(shiftId, dayId, classTimeId))
                }
            )
        }

        composable(
            route = AppRoutes.ATTENDANCE,
            arguments = listOf(
                navArgument(AppRoutes.ARG_SHIFT_ID) { type = NavType.IntType },
                navArgument(AppRoutes.ARG_DAY_ID) { type = NavType.IntType },
                navArgument(AppRoutes.ARG_CLASS_TIME_ID) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val shiftId = backStackEntry.arguments?.getInt(AppRoutes.ARG_SHIFT_ID) ?: return@composable
            val dayId = backStackEntry.arguments?.getInt(AppRoutes.ARG_DAY_ID) ?: return@composable
            val classTimeId = backStackEntry.arguments
                ?.getInt(AppRoutes.ARG_CLASS_TIME_ID)
                ?: return@composable
            val viewModel: AttendanceViewModel = viewModel(
                factory = containerViewModelFactory(container) { appContainer ->
                    AttendanceViewModel(
                        attendanceRepository = appContainer.attendanceRepository,
                        studentRepository = appContainer.studentRepository,
                        shiftRepository = appContainer.shiftRepository,
                        sendSmsUseCase = appContainer.sendSmsUseCase,
                        shiftId = shiftId,
                        dayId = dayId,
                        classTimeId = classTimeId
                    )
                }
            )
            AttendanceScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.DEBTORS) {
            val viewModel: DebtorsViewModel = viewModel(
                factory = containerViewModelFactory(container) { appContainer ->
                    DebtorsViewModel(appContainer.financeRepository)
                }
            )
            DebtorsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onOpenStudent = { studentId ->
                    navController.navigate(AppRoutes.payments(studentId))
                }
            )
        }

        composable(
            route = AppRoutes.PAYMENTS,
            arguments = listOf(
                navArgument(AppRoutes.ARG_STUDENT_ID) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getInt(AppRoutes.ARG_STUDENT_ID) ?: return@composable
            val viewModel: PaymentsViewModel = viewModel(
                factory = containerViewModelFactory(container) { appContainer ->
                    PaymentsViewModel(
                        financeRepository = appContainer.financeRepository,
                        studentRepository = appContainer.studentRepository,
                        studentId = studentId
                    )
                }
            )
            PaymentsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.REPORTS) {
            val viewModel: ReportsViewModel = viewModel(
                factory = containerViewModelFactory(container) { appContainer ->
                    ReportsViewModel(
                        studentRepository = appContainer.studentRepository,
                        attendanceRepository = appContainer.attendanceRepository,
                        financeRepository = appContainer.financeRepository,
                        smsRepository = appContainer.smsRepository
                    )
                }
            )
            ReportsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.SMS_LOG) {
            val viewModel: SmsLogViewModel = viewModel(
                factory = containerViewModelFactory(container) { appContainer ->
                    SmsLogViewModel(
                        smsRepository = appContainer.smsRepository,
                        studentRepository = appContainer.studentRepository
                    )
                }
            )
            SmsLogScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.SMS_TEMPLATE_SETTINGS) {
            val viewModel: SmsTemplateSettingsViewModel = viewModel(
                factory = containerViewModelFactory(container) { appContainer ->
                    SmsTemplateSettingsViewModel(appContainer.smsTemplateRepository)
                }
            )
            SmsTemplateSettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
