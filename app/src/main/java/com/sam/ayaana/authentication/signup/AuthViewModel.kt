package com.sam.ayaana.authentication.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.ayaana.authentication.AuthRepository
import com.sam.ayaana.datastore.DatastoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val datastoreRepository: DatastoreRepository
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> get() = _authState.asStateFlow()

    init {
        Log.d("TAG", "Created an instance of ${this::class.simpleName}");

    }
    fun signUp(email: String, password: String){
        viewModelScope.launch (Dispatchers.IO){
            _authState.value = AuthState.Loading
            delay(2_000)
            authRepository.signUp(
                email = email,
                password = password,
                onSignUpSuccess = {
                    saveIsAuthenticated(true)
                    _authState.value = AuthState.Success
                },
                onSignUpFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Unknown error")
                }
//                       email: paul12345@gmail.com, chichi3456@gmail.com, rajesh12
//                        pwd: paul12345, chichi3456, rajesh12
            )
        }
    }
    fun signIn(email: String, password: String){
        viewModelScope.launch (Dispatchers.IO){
            Log.d("SignInFlow", "2. ViewModel's signIn function called. Email: $email")
            _authState.value = AuthState.Loading
            delay(2_000)
            authRepository.signIn(
                email = email,
                password = password,
                onSignInSuccess = {
                    saveIsAuthenticated(true)
                    _authState.value = AuthState.Success
                },
                onSignInFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Unknown error")
                }
            )
        }
    }
    fun saveIsAuthenticated(authenticated: Boolean) {
        viewModelScope.launch(Dispatchers.IO){
            datastoreRepository.saveIsAuthenticated(authenticated)
        }
    }

    override fun onCleared() {
        Log.d("TAG", "Clearing an instance of ${this::class.simpleName}");

        super.onCleared()
    }

}

sealed interface AuthState{
    data object Initial: AuthState
    data object Loading: AuthState
    data object Success: AuthState
    //data class Success(val message: String): AuthState //might bring some issue
    data class Error(val message: String): AuthState
}
