package com.github.fatihsokmen.wallet.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @Dispatcher(Dispatchers.IO)
    fun providesIODispatcher(): CoroutineDispatcher = kotlinx.coroutines.Dispatchers.IO

    @Provides
    @Dispatcher(Dispatchers.Default)
    fun providesDefaultDispatcher(): CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Default
}

