package com.amadino.app.ui // Replace with your actual package name

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amadino.app.data.local.UserDataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Instantiate the UserDataManager with the app context
    private val userDataManager = UserDataManager(application.applicationContext)

    /**
     * Calls the finalize logic from UserDataManager on a background thread.
     */
    fun finalizeDay(date: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isSuccess = withContext(Dispatchers.IO) {
                try {
                    userDataManager.finalizeDay(date)
                    true
                } catch (e: Exception) {
                    false
                }
            }
            onComplete(isSuccess)
        }
    }
}