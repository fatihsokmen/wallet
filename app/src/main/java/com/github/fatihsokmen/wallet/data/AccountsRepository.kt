package com.github.fatihsokmen.wallet.data

import com.github.fatihsokmen.wallet.presentation.home.model.Currency
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
    private val etherScanApiService: EtherScanApiService
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
    }.flowOn(Dispatchers.IO)

    override fun getGasFee(): Flow<Result<BigDecimal>> = flow {
        emit(
            runCatching {
                BigDecimal.valueOf(
                    etherScanApiService.getGasFee().result.FastGasPrice
                )
            }
        )
    }.flowOn(Dispatchers.IO)
}