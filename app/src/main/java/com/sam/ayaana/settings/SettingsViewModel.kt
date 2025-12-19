package com.sam.ayaana.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.ayaana.authentication.AuthRepository
import com.sam.ayaana.datastore.DatastoreManager
import com.sam.ayaana.datastore.DatastoreRepository
import com.sam.ayaana.navigation.NavigationDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val datastoreRepository: DatastoreRepository
) : ViewModel() {
    private val _startDestination = MutableStateFlow<NavigationDestination>(NavigationDestination.Home) //Splash was there before
    val startDestination: StateFlow<NavigationDestination> get() = _startDestination

    init {
        viewModelScope.launch(Dispatchers.IO){
            delay(1000) //Show splashscreen for 1 seconds
            datastoreRepository.authenticated.collect { authenticated ->
                _startDestination.value = if (authenticated) {
                    NavigationDestination.Home
                } else {
                    NavigationDestination.SignIn
                }
            }
        }
    }
    val authenticated = datastoreRepository.authenticated.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    fun saveIsAuthenticated(authenticated: Boolean) {
        viewModelScope.launch {
            datastoreRepository.saveIsAuthenticated(authenticated)
        }
    }

    fun logout(){
        viewModelScope.launch {
            authRepository.signOut()
            datastoreRepository.saveIsAuthenticated(false)
        }
    }
}
