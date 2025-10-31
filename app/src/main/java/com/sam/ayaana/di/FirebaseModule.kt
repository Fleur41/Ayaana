package com.sam.ayaana.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.sam.ayaana.authentication.GoogleAuthUiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

class FirebaseModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideGoogleAuthUiClient(
        @ApplicationContext context: Context
    ): GoogleAuthUiClient {
        return GoogleAuthUiClient(
            context = context
        )
    }
}