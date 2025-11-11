package com.sam.ayaana.di

import com.sam.ayaana.datastore.DatastoreManager
import com.sam.ayaana.datastore.DatastoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideDatastoreManager(
        @ApplicationContext context: android.content.Context
    ): DatastoreManager = DatastoreManager(context)

    @Provides
    @Singleton
    fun provideDatastoreRepository(
        datastoreManager: DatastoreManager
    ): DatastoreRepository = DatastoreRepository(datastoreManager)
}