package com.github.fatihsokmen.wallet.presentation.home.di

import com.github.fatihsokmen.wallet.data.EthereumPriceRepository
import com.github.fatihsokmen.wallet.data.di.DataModule
import com.github.fatihsokmen.wallet.presentation.home.model.Currency
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class]
)
@Module
interface TestDataModule {

    @Binds
    @Singleton
    fun bindsAccountsRepository(
        repository: TestEthereumPriceRepositoryImpl
    ): EthereumPriceRepository
}

class TestEthereumPriceRepositoryImpl @Inject constructor() : EthereumPriceRepository {

    override fun getEthereumPriceIn(currency: Currency): Flow<BigDecimal> =
        if (currency == Currency.GBP) {
            flow {
                throw IllegalArgumentException(
                    "To test unhappy path, we use GBP selection to raise exception"
                )
            }
        } else {
            flowOf(BigDecimal("1500"))
        }


    override fun getGasFee(): Flow<Result<BigDecimal>> =
        flowOf(Result.success(BigDecimal("10")))
}