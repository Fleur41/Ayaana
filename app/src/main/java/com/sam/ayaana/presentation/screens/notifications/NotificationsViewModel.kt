package com.sam.ayaana.presentation.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.ayaana.Utils.Result
import com.sam.ayaana.domain.model.FollowRequest
import com.sam.ayaana.domain.model.Notification
import com.sam.ayaana.domain.model.SuggestedUser
import com.sam.ayaana.domain.repository.INotificationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsRepository: INotificationsRepository
) : ViewModel() {

    // Notifications state
    private val _notificationsState = MutableStateFlow<NotificationsState>(NotificationsState.Loading)
    val notificationsState: StateFlow<NotificationsState> = _notificationsState.asStateFlow()

    // Follow requests state
    private val _followRequestsState = MutableStateFlow<FollowRequestsState>(FollowRequestsState.Loading)
    val followRequestsState: StateFlow<FollowRequestsState> = _followRequestsState.asStateFlow()

    // Suggested users state
    private val _suggestedUsersState = MutableStateFlow<SuggestedUsersState>(SuggestedUsersState.Loading)
    val suggestedUsersState: StateFlow<SuggestedUsersState> = _suggestedUsersState.asStateFlow()

    // Selected notification for navigation
    private val _selectedNotification = MutableStateFlow<Notification?>(null)
    val selectedNotification: StateFlow<Notification?> = _selectedNotification.asStateFlow()

    init {
        loadAllData()
    }

    fun loadAllData() {
        loadNotifications()
        loadFollowRequests()
        loadSuggestedUsers()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            println("DEBUG VM: Loading notifications...")
            _notificationsState.value = NotificationsState.Loading
            when (val result = notificationsRepository.getNotifications()) {
                is Result.Success -> {
                    println("DEBUG VM: Notifications loaded successfully: ${result.data.size} items")
                    result.data.forEach { notification ->
                        println("DEBUG VM: - ${notification.title}: ${notification.type}")
                    }
                    _notificationsState.value = NotificationsState.Success(result.data)
                }
                is Result.Error -> {
                    println("DEBUG VM: Error loading notifications: ${result.message}")
                    _notificationsState.value = NotificationsState.Error(result.message)
                }
                is Result.Loading -> {
                    println("DEBUG VM: Still loading notifications...")
                    _notificationsState.value = NotificationsState.Loading
                }
            }
        }
    }
//    private fun loadNotifications() {
//        viewModelScope.launch {
//            _notificationsState.value = NotificationsState.Loading
//            when (val result = notificationsRepository.getNotifications()) {
//                is Result.Success -> {
//                    _notificationsState.value = NotificationsState.Success(result.data)
//                }
//                is Result.Error -> {
//                    _notificationsState.value = NotificationsState.Error(result.message)
//                }
//                is Result.Loading -> {
//                    _notificationsState.value = NotificationsState.Loading
//                }
//            }
//        }
//    }

    private fun loadFollowRequests() {
        viewModelScope.launch {
            _followRequestsState.value = FollowRequestsState.Loading
            when (val result = notificationsRepository.getFollowRequests()) {
                is Result.Success -> {
                    _followRequestsState.value = FollowRequestsState.Success(result.data)
                }
                is Result.Error -> {
                    _followRequestsState.value = FollowRequestsState.Error(result.message)
                }
                is Result.Loading -> {
                    _followRequestsState.value = FollowRequestsState.Loading
                }
            }
        }
    }

    private fun loadSuggestedUsers() {
        viewModelScope.launch {
            _suggestedUsersState.value = SuggestedUsersState.Loading
            when (val result = notificationsRepository.getSuggestedUsers()) {
                is Result.Success -> {
                    _suggestedUsersState.value = SuggestedUsersState.Success(result.data)
                }
                is Result.Error -> {
                    _suggestedUsersState.value = SuggestedUsersState.Error(result.message)
                }
                is Result.Loading -> {
                    _suggestedUsersState.value = SuggestedUsersState.Loading
                }
            }
        }
    }

    fun markNotificationAsRead(notificationId: String) {
        viewModelScope.launch {
            notificationsRepository.markNotificationAsRead(notificationId)
            // Refresh notifications after marking as read
            loadNotifications()
        }
    }

    fun acceptFollowRequest(requestId: String) {
        viewModelScope.launch {
            when (val result = notificationsRepository.acceptFollowRequest(requestId)) {
                is Result.Success -> {
                    // Remove from local state
                    _followRequestsState.update { currentState ->
                        if (currentState is FollowRequestsState.Success) {
                            val updatedRequests = currentState.requests.filter { it.id != requestId }
                            FollowRequestsState.Success(updatedRequests)
                        } else {
                            currentState
                        }
                    }
                    // Reload to get updated count
                    loadNotifications()
                }
                is Result.Error -> {
                    // Handle error (could show a snackbar)
                }
                else -> {}
            }
        }
    }

    fun deleteFollowRequest(requestId: String) {
        viewModelScope.launch {
            when (val result = notificationsRepository.deleteFollowRequest(requestId)) {
                is Result.Success -> {
                    // Remove from local state
                    _followRequestsState.update { currentState ->
                        if (currentState is FollowRequestsState.Success) {
                            val updatedRequests = currentState.requests.filter { it.id != requestId }
                            FollowRequestsState.Success(updatedRequests)
                        } else {
                            currentState
                        }
                    }
                }
                is Result.Error -> {
                    // Handle error
                }
                else -> {}
            }
        }
    }

    fun followSuggestedUser(userId: String) {
        viewModelScope.launch {
            when (val result = notificationsRepository.followSuggestedUser(userId)) {
                is Result.Success -> {
                    // Remove from local state
                    _suggestedUsersState.update { currentState ->
                        if (currentState is SuggestedUsersState.Success) {
                            val updatedUsers = currentState.users.filter { it.id != userId }
                            SuggestedUsersState.Success(updatedUsers)
                        } else {
                            currentState
                        }
                    }
                }
                is Result.Error -> {
                    // Handle error
                }
                else -> {}
            }
        }
    }

    fun dismissSuggestedUser(userId: String) {
        viewModelScope.launch {
            when (val result = notificationsRepository.dismissSuggestedUser(userId)) {
                is Result.Success -> {
                    // Remove from local state
                    _suggestedUsersState.update { currentState ->
                        if (currentState is SuggestedUsersState.Success) {
                            val updatedUsers = currentState.users.filter { it.id != userId }
                            SuggestedUsersState.Success(updatedUsers)
                        } else {
                            currentState
                        }
                    }
                }
                is Result.Error -> {
                    // Handle error
                }
                else -> {}
            }
        }
    }

    fun selectNotification(notification: Notification) {
        _selectedNotification.value = notification
    }

    fun clearSelectedNotification() {
        _selectedNotification.value = null
    }

    // State sealed classes
    sealed class NotificationsState {
        data object Loading : NotificationsState()
        data class Success(val notifications: List<Notification>) : NotificationsState()
        data class Error(val message: String) : NotificationsState()
    }

    sealed class FollowRequestsState {
        data object Loading : FollowRequestsState()
        data class Success(val requests: List<FollowRequest>) : FollowRequestsState()
        data class Error(val message: String) : FollowRequestsState()
    }

    sealed class SuggestedUsersState {
        data object Loading : SuggestedUsersState()
        data class Success(val users: List<SuggestedUser>) : SuggestedUsersState()
        data class Error(val message: String) : SuggestedUsersState()
    }
}