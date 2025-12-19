package com.sam.ayaana.domain.repository

import com.sam.ayaana.domain.model.User
import kotlinx.coroutines.flow.Flow
import com.sam.ayaana.Utils.Result
import com.sam.ayaana.Utils.toUser
import com.sam.ayaana.Utils.toUserEntity
import com.sam.ayaana.Utils.toUserResponse
import com.sam.ayaana.data.local.dao.UserDao
import com.sam.ayaana.data.remote.api.UserApi
import com.sam.ayaana.datastore.DatastoreRepository
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

interface IUserRepository{
    suspend fun getCurrentUser(): Flow<Result<User>>
    suspend fun getUserById(userId: String): Flow<Result<User>>
    suspend fun searchUsers(query: String): Flow<Result<List<User>>>
    suspend fun followUser(userId: String): Result<Boolean>
    suspend fun unfollowUser(userId: String): Result<Boolean>
    suspend fun updateProfile(user: User): Result<Boolean>
    suspend fun getFollowers(userId: String): Flow<Result<List<User>>>
    suspend fun getFollowing(userId: String): Flow<Result<List<User>>>

    val currentUserProfileImage: Flow<String?>
    suspend fun updateCurrentUserProfileImage(path: String?)
}

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val userDao: UserDao,
    private val datastoreRepository: DatastoreRepository
) : IUserRepository {

    private val _currentUserProfileImage = MutableStateFlow<String?>(null)
    override val currentUserProfileImage: Flow<String?> = _currentUserProfileImage

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        // Load saved image when repository starts
        scope.launch{
            loadSavedProfileImage()
        }
    }

    private suspend fun loadSavedProfileImage() {
        datastoreRepository.getProfileImagePath()?.let { path ->
            _currentUserProfileImage.value = path
        }
    }

    // : Implementation
    override suspend fun updateCurrentUserProfileImage(path: String?) {
        if (path != null) {
            datastoreRepository.saveProfileImagePath(path)
        } else {
            datastoreRepository.clearProfileImagePath()
        }
        _currentUserProfileImage.value = path
    }

    override suspend fun getCurrentUser(): Flow<Result<User>> = flow {
        emit(Result.Loading)
        try {
            // First try to get from local database
            // Note: You might want to store current user ID separately
            val response = userApi.getCurrentUser()
            if (response.success && response.data != null) {
                val userEntity = response.data.toUserEntity()
                userDao.insertUser(userEntity)
                emit(Result.Success(response.data.toUser()))
            } else {
                emit(Result.Error(response.message ?: "Failed to load current user"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Network error"))
        }
    }

    override suspend fun getUserById(userId: String): Flow<Result<User>> = flow {
        emit(Result.Loading)
        try {
            // First try to get from local database
            val localUser = userDao.getUserById(userId)
            localUser.collect { userEntity ->
                if (userEntity != null) {
                    emit(Result.Success(userEntity.toUser()))
                }
            }

            // Then fetch from API and update local database
            val response = userApi.getUserById(userId)
            if (response.success && response.data != null) {
                val userEntity = response.data.toUserEntity()
                userDao.insertUser(userEntity)
                emit(Result.Success(response.data.toUser()))
            } else {
                emit(Result.Error(response.message ?: "Failed to load user"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Network error"))
        }
    }

    override suspend fun searchUsers(query: String): Flow<Result<List<User>>> = flow {
        emit(Result.Loading)
        try {
            val response = userApi.searchUsers(query)
            if (response.success && response.data != null) {
                val userEntities = response.data.map { it.toUserEntity() }
                userDao.insertUsers(userEntities)
                emit(Result.Success(response.data.map { it.toUser() }))
            } else {
                emit(Result.Error(response.message ?: "No users found"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Network error"))
        }
    }

    override suspend fun followUser(userId: String): Result<Boolean> {
        return try {
            val response = userApi.followUser(userId)
            if (response.success) {
                // Update local database to reflect follow status
                updateLocalUserFollowStatus(userId, isFollowing = true)
                Result.Success(true)
            } else {
                Result.Error(response.message ?: "Failed to follow user")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun unfollowUser(userId: String): Result<Boolean> {
        return try {
            val response = userApi.unfollowUser(userId)
            if (response.success) {
                // Update local database to reflect unfollow status
                updateLocalUserFollowStatus(userId, isFollowing = false)
                Result.Success(true)
            } else {
                Result.Error(response.message ?: "Failed to unfollow user")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun updateProfile(user: User): Result<Boolean> {
        return try {
            val response = userApi.updateProfile(user.toUserResponse())
            if (response.success && response.data != null) {
                val userEntity = response.data.toUserEntity()
                userDao.insertUser(userEntity)
                Result.Success(true)
            } else {
                Result.Error(response.message ?: "Failed to update profile")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun getFollowers(userId: String): Flow<Result<List<User>>> = flow {
        emit(Result.Loading)
        try {
            val response = userApi.getFollowers(userId)
            if (response.success && response.data != null) {
                val userEntities = response.data.map { it.toUserEntity() }
                userDao.insertUsers(userEntities)
                emit(Result.Success(response.data.map { it.toUser() }))
            } else {
                emit(Result.Error(response.message ?: "Failed to load followers"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Network error"))
        }
    }

    override suspend fun getFollowing(userId: String): Flow<Result<List<User>>> = flow {
        emit(Result.Loading)
        try {
            val response = userApi.getFollowing(userId)
            if (response.success && response.data != null) {
                val userEntities = response.data.map { it.toUserEntity() }
                userDao.insertUsers(userEntities)
                emit(Result.Success(response.data.map { it.toUser() }))
            } else {
                emit(Result.Error(response.message ?: "Failed to load following"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Network error"))
        }
    }

    private suspend fun updateLocalUserFollowStatus(userId: String, isFollowing: Boolean) {
        val localUser = userDao.getUserById(userId)
        localUser.collect { userEntity ->
            userEntity?.let { entity ->
                val updatedEntity = entity.copy(isFollowing = isFollowing)
                userDao.insertUser(updatedEntity)
            }
        }
    }
}