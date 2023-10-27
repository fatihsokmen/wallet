package com.github.fatihsokmen.wallet.domain

import com.github.fatihsokmen.wallet.data.EthereumPriceRepository
import com.github.fatihsokmen.wallet.presentation.home.model.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

/**
 * This is to calculate how much ETH we can buy by given fiat currency and amount
 *
 *      Ex: ETH prices is $1600
 *
 *      GIVEN input 800 THEN user gets 800/1600=0.5 ETH
 */
interface CalculateFiatPriceByGivenEthereumUseCase {
    fun execute(currency: Currency, ethAmount: BigDecimal): Flow<Result<BigDecimal>>
}

class CalculateFiatPriceByGivenEthereumUseCaseImpl @Inject constructor(
    private val ethereumPriceRepository: EthereumPriceRepository
) : CalculateFiatPriceByGivenEthereumUseCase {
    override fun execute(currency: Currency, ethAmount: BigDecimal) =
        ethereumPriceRepository.getEthereumPriceIn(currency)
            .map {
                Result.success(
                    ethAmount.multiply(it).setScale(3, RoundingMode.HALF_UP).stripTrailingZeros()
                )
            }.catch {
                emit(Result.failure<BigDecimal>(it))
            }

}