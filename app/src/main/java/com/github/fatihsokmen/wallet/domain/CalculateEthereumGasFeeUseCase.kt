package com.github.fatihsokmen.wallet.domain

import com.github.fatihsokmen.wallet.data.EthereumPriceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

/**
 * This is to calculate fastest ETH gas fee
 */
interface CalculateEthereumGasFeeUseCase {
    fun execute(): Flow<BigDecimal>
}

class CalculateEthereumGasFeeUseCaseImpl @Inject constructor(
    private val ethereumPriceRepository: EthereumPriceRepository
) : CalculateEthereumGasFeeUseCase {
    override fun execute() = ethereumPriceRepository.getGasFee()
        .map {
            it.getOrThrow().multiply(BigDecimal(21000))
                .divide(BigDecimal(100000000), 3, RoundingMode.HALF_UP)
                .stripTrailingZeros()
        }
}