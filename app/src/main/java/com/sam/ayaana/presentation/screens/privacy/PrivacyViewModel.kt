package com.sam.ayaana.presentation.screens.privacy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.ayaana.domain.model.PrivacyUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivacyViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(PrivacyUiState())
    val uiState: StateFlow<PrivacyUiState> = _uiState

    fun updateAccountPrivacy(isPrivate: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isAccountPrivate = isPrivate
            )
        }
    }

    fun updateCloseFriends(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                closeFriendsEnabled = enabled
            )
        }
    }

    fun updateCrossposting(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                crosspostingEnabled = enabled
            )
        }
    }
}