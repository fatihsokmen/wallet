package com.github.fatihsokmen.wallet.data

import com.github.fatihsokmen.wallet.core.di.Dispatcher
import com.github.fatihsokmen.wallet.core.di.Dispatchers.*
import com.github.fatihsokmen.wallet.presentation.home.model.Currency
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.math.BigDecimal
import javax.inject.Inject

interface EthereumPriceRepository {
    fun getEthereumPriceIn(currency: Currency): Flow<BigDecimal>
    fun getGasFee(): Flow<Result<BigDecimal>>
}

class EthereumPriceRepositoryImpl @Inject constructor(
    private val coinGeckoApiService: CoinGeckoApiService,
    private val etherScanApiService: EtherScanApiService,
    @Dispatcher(IO) private val dispatcher: CoroutineDispatcher
) : EthereumPriceRepository {

    override fun getEthereumPriceIn(currency: Currency) = flow {
        val currencyCode = currency.name.lowercase()
        emit(
            BigDecimal(
                coinGeckoApiService.getEthereumPriceIn(
                    currencyCode
                ).ethereum[currencyCode]
            )
        )
    }.flowOn(dispatcher)

    override fun getGasFee(): Flow<Result<BigDecimal>> = flow {
        emit(
            runCatching {
                BigDecimal.valueOf(
                    etherScanApiService.getGasFee().result.FastGasPrice
                )
            }
        )
    }.flowOn(dispatcher)
}