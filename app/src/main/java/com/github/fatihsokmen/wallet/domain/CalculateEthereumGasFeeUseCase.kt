package com.github.fatihsokmen.wallet.domain

import com.github.fatihsokmen.wallet.data.EthereumPriceRepository
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

/**
 * This is to calculate fastest ETH gas fee
 */
class CalculateEthereumGasFeeUseCase @Inject constructor(
    private val ethereumPriceRepository: EthereumPriceRepository
) {
    fun execute() = ethereumPriceRepository.getGasFee()
        .map {
            it.getOrThrow().multiply(BigDecimal(21000))
                .divide(BigDecimal(100000000), 3, RoundingMode.HALF_UP)
                .stripTrailingZeros()
        }
}