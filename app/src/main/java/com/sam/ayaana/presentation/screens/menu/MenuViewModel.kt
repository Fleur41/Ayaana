package com.sam.ayaana.presentation.screens.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.ayaana.authentication.AuthRepository
import com.sam.ayaana.datastore.DatastoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val datastoreRepository: DatastoreRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isPrivateAccount = MutableStateFlow(false)
    val isPrivateAccount: StateFlow<Boolean> = _isPrivateAccount.asStateFlow()


    fun togglePrivacySetting(isPrivate: Boolean) {
        viewModelScope.launch {
            datastoreRepository.savePrivacySetting(isPrivate)
            _isPrivateAccount.value = isPrivate
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}