package com.amadino.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadino.app.data.repository.SmsTemplateRepository
import com.amadino.app.data.sms.SmsTemplates
import com.amadino.app.domain.model.SmsEventType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SmsTemplateSettingsViewModel(
    private val smsTemplateRepository: SmsTemplateRepository
) : ViewModel() {

    data class UiState(
        val entry: String = SmsTemplates.DEFAULT_ENTRY,
        val exit: String = SmsTemplates.DEFAULT_EXIT,
        val isLoaded: Boolean = false,
        val entryError: String? = null,
        val exitError: String? = null,
        val saved: Boolean = false
    ) {
        val entryPreview: String
            get() = SmsTemplates.render(entry, PREVIEW_STUDENT, PREVIEW_DATE, PREVIEW_TIME)

        val exitPreview: String
            get() = SmsTemplates.render(exit, PREVIEW_STUDENT, PREVIEW_DATE, PREVIEW_TIME)
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val overrides = smsTemplateRepository.getOverrides().first()
            _state.value = UiState(
                entry = overrides[SmsEventType.ENTRY.value] ?: SmsTemplates.DEFAULT_ENTRY,
                exit = overrides[SmsEventType.EXIT.value] ?: SmsTemplates.DEFAULT_EXIT,
                isLoaded = true
            )
        }
    }

    fun onEntryChange(value: String) {
        _state.value = _state.value.copy(entry = value, entryError = null, saved = false)
    }

    fun onExitChange(value: String) {
        _state.value = _state.value.copy(exit = value, exitError = null, saved = false)
    }

    fun save() {
        val current = _state.value
        val entryError = validate(current.entry)
        val exitError = validate(current.exit)
        if (entryError != null || exitError != null) {
            _state.value = current.copy(entryError = entryError, exitError = exitError)
            return
        }

        viewModelScope.launch {
            smsTemplateRepository.saveTemplate(SmsEventType.ENTRY, current.entry.trim())
            smsTemplateRepository.saveTemplate(SmsEventType.EXIT, current.exit.trim())
            _state.value = _state.value.copy(
                entry = current.entry.trim(),
                exit = current.exit.trim(),
                saved = true
            )
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            smsTemplateRepository.resetTemplate(SmsEventType.ENTRY)
            smsTemplateRepository.resetTemplate(SmsEventType.EXIT)
            _state.value = UiState(isLoaded = true, saved = true)
        }
    }

    fun consumeSaved() {
        _state.value = _state.value.copy(saved = false)
    }

    private fun validate(template: String): String? = when {
        template.isBlank() -> "متن پیامک نمی‌تواند خالی باشد."
        !template.contains("{student}") -> "متن باید شامل {student} باشد."
        else -> null
    }

    private companion object {
        const val PREVIEW_STUDENT = "علی رضایی"
        const val PREVIEW_DATE = "1405/08/10"
        const val PREVIEW_TIME = "08:15"
    }
}
