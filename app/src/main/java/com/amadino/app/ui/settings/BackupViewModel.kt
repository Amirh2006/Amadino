package com.amadino.app.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadino.app.data.backup.BackupManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BackupViewModel(
    private val backupManager: BackupManager
) : ViewModel() {

    data class UiState(
        val isBusy: Boolean = false,
        val message: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun export(uri: Uri) {
        launchBackup(failure = "پشتیبان‌گیری ناموفق بود") {
            val summary = backupManager.exportTo(uri)
            "پشتیبان‌گیری انجام شد: ${summary.total.toPersian()} رکورد ذخیره شد."
        }
    }

    fun import(uri: Uri) {
        launchBackup(failure = "بازیابی ناموفق بود") {
            val summary = backupManager.importFrom(uri)
            "بازیابی انجام شد: ${summary.students.toPersian()} دانش‌آموز، " +
                "${summary.attendance.toPersian()} حضور، ${summary.payments.toPersian()} پرداخت، " +
                "${summary.smsLogs.toPersian()} پیامک."
        }
    }

    fun consumeMessage() {
        _state.value = _state.value.copy(message = null)
    }

    private fun launchBackup(failure: String, block: suspend () -> String) {
        if (_state.value.isBusy) return
        _state.value = _state.value.copy(isBusy = true, message = null)
        viewModelScope.launch {
            val message = runCatching { block() }
                .getOrElse { error -> "$failure: ${error.message ?: "خطای نامشخص"}" }
            _state.value = UiState(isBusy = false, message = message)
        }
    }

    private fun Int.toPersian(): String = toString().map { char ->
        if (char in '0'..'9') "۰۱۲۳۴۵۶۷۸۹"[char - '0'] else char
    }.joinToString("")
}
