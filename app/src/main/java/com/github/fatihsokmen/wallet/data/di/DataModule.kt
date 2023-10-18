package com.github.fatihsokmen.wallet.data.di

import com.github.fatihsokmen.wallet.data.EthereumPriceRepository
import com.github.fatihsokmen.wallet.data.EthereumPriceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindsAccountsRepository(
        repository: EthereumPriceRepositoryImpl
    ): EthereumPriceRepository
}
