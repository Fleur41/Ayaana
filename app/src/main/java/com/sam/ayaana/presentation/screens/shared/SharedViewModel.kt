package com.sam.ayaana.presentation.screens.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {
    private val _hasProfilePicture = MutableStateFlow(false)
    val hasProfilePicture: StateFlow<Boolean> = _hasProfilePicture.asStateFlow()

    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl: StateFlow<String?> = _profileImageUrl.asStateFlow()

    fun setProfilePicture(hasPicture: Boolean, imageUrl: String? = null) {
        viewModelScope.launch {
            _hasProfilePicture.value = hasPicture
            _profileImageUrl.value = imageUrl
        }
    }

    fun clearProfilePicture() {
        viewModelScope.launch {
            _hasProfilePicture.value = false
            _profileImageUrl.value = null
        }
    }
}