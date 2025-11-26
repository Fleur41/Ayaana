package com.sam.ayaana.di

import com.sam.ayaana.authentication.AuthRepository
import com.sam.ayaana.authentication.AuthRepositoryImpl
import com.sam.ayaana.domain.repository.ActivityRepositoryImpl
import com.sam.ayaana.domain.repository.ChatRepositoryImpl
import com.sam.ayaana.domain.repository.IActivityRepository
import com.sam.ayaana.domain.repository.IChatRepository
import com.sam.ayaana.domain.repository.IPostRepository
import com.sam.ayaana.domain.repository.IRecentSearchesRepository
import com.sam.ayaana.domain.repository.ISocialAuthRepository
import com.sam.ayaana.domain.repository.IUserRepository
import com.sam.ayaana.domain.repository.PostRepositoryImpl
import com.sam.ayaana.domain.repository.RecentSearchesRepositoryImpl
import com.sam.ayaana.domain.repository.SocialAuthRepositoryImpl
import com.sam.ayaana.domain.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)

abstract class RepositoryModule {
    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository


    @Binds
    abstract fun bindPostRepository(impl: PostRepositoryImpl): IPostRepository

    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): IUserRepository

    @Binds
    abstract fun bindActivityRepository(impl: ActivityRepositoryImpl): IActivityRepository

    @Binds
    abstract fun bindSocialAuthRepository(impl: SocialAuthRepositoryImpl): ISocialAuthRepository

    @Binds
    abstract fun bindRecentSearchesRepository(impl: RecentSearchesRepositoryImpl): IRecentSearchesRepository

    @Binds
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): IChatRepository
}