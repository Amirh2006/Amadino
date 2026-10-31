package com.amadino.app.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

/**
 * Builds a [ViewModelProvider.Factory] for a single ViewModel created from the
 * given [AppContainer]. Keeps manual DI wiring out of the composables.
 */
inline fun <reified VM : ViewModel> containerViewModelFactory(
    container: AppContainer,
    crossinline create: (AppContainer) -> VM
): ViewModelProvider.Factory = viewModelFactory {
    initializer { create(container) }
}
