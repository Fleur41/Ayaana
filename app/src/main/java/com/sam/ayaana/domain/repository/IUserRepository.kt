package com.sam.ayaana.domain.repository

import com.sam.ayaana.domain.model.User
import kotlinx.coroutines.flow.Flow

interface IUserRepository{
    suspend fun getCurrentUser(): Flow<Result<User>>
    suspend fun getUserById(userId: String): Flow<Result<User>>
    suspend fun searchUsers(query: String): Flow<Result<List<User>>>
    suspend fun followUser(userId: String): Result<Boolean>
    suspend fun unfollowUser(userId: String): Result<Boolean>
    suspend fun updateProfile(user: User): Result<Boolean>
    suspend fun getFollowers(userId: String): Flow<Result<List<User>>>
    suspend fun getFollowing(userId: String): Flow<Result<List<User>>>
}