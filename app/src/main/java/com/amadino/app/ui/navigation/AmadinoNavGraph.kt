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
                onTakeAttendance = { shiftId ->
                    navController.navigate(
                        AppRoutes.classTimePicker(shiftId, viewModel.selectedDayId.value)
                    )
                }
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
    }
}
