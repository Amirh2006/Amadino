package com.amadino.app.ui.students

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadino.app.data.local.entity.StudentEntity
import com.amadino.app.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudentListViewModel(
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val students: StateFlow<List<StudentEntity>> =
        combine(studentRepository.getAllStudents(), _query) { students, query ->
            val trimmed = query.trim()
            if (trimmed.isEmpty()) {
                students
            } else {
                students.filter { student ->
                    student.fullName.contains(trimmed, ignoreCase = true) ||
                        student.grade.contains(trimmed, ignoreCase = true)
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun delete(student: StudentEntity) {
        viewModelScope.launch { studentRepository.deleteStudent(student) }
    }
}
