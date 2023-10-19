package com.github.fatihsokmen.wallet.core.di

import com.github.fatihsokmen.wallet.core.StringResources
import com.github.fatihsokmen.wallet.core.StringResourcesImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ResourceModule {

    @Binds
    fun bindStringResources(
        repository: StringResourcesImpl
    ): StringResources
}
