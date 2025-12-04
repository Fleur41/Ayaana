package com.sam.ayaana.presentation.screens.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.ayaana.datastore.DatastoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val datastoreRepository: DatastoreRepository
) : ViewModel() {

    // Single source of truth for theme
    val theme: StateFlow<String> = datastoreRepository.theme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "light"
        )

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            val theme = if (isDark) "dark" else "light"
            datastoreRepository.saveTheme(theme)
        }
    }
}
//@HiltViewModel
//class ThemeViewModel @Inject constructor(
//    private val datastoreRepository: DatastoreRepository
//) : ViewModel() {
//
//    private val _isDarkTheme = MutableStateFlow(false)
//    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()
//
//    val theme: StateFlow<String> = datastoreRepository.theme
//        .map { it }
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = "light"
//        )
//
//    init {
//        loadTheme()
//    }
//
//    private fun loadTheme() {
//        viewModelScope.launch {
//            val theme = datastoreRepository.getTheme()
//            _isDarkTheme.value = theme == "dark"
//        }
//    }
//
//    fun toggleTheme(isDark: Boolean) {
//        viewModelScope.launch {
//            val theme = if (isDark) "dark" else "light"
//            datastoreRepository.saveTheme(theme)
////            _isDarkTheme.value = isDark
//        }
//    }
//}
