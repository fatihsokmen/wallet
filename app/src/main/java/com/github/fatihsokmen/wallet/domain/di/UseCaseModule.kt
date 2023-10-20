package com.github.fatihsokmen.wallet.domain.di

import com.github.fatihsokmen.wallet.domain.CalculateEthereumAmountInFiatCurrencyUseCase
import com.github.fatihsokmen.wallet.domain.CalculateEthereumAmountInFiatCurrencyUseCaseImpl
import com.github.fatihsokmen.wallet.domain.CalculateEthereumGasFeeUseCase
import com.github.fatihsokmen.wallet.domain.CalculateEthereumGasFeeUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface UseCaseModule {

    @Binds
    fun bindsEthereumAmountUseCase(
        repository: CalculateEthereumAmountInFiatCurrencyUseCaseImpl
    ): CalculateEthereumAmountInFiatCurrencyUseCase

    @Binds
    fun bindsEthereumGasFeeUseCase(
        repository: CalculateEthereumGasFeeUseCaseImpl
    ): CalculateEthereumGasFeeUseCase
}