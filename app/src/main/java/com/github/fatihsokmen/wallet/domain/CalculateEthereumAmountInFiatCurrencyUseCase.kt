package com.github.fatihsokmen.wallet.domain

import com.github.fatihsokmen.wallet.data.EthereumPriceRepository
import com.github.fatihsokmen.wallet.presentation.home.model.Currency
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
class CalculateEthereumAmountInFiatCurrencyUseCase @Inject constructor(
    private val ethereumPriceRepository: EthereumPriceRepository
) {
    fun execute(currency: Currency, amount: BigDecimal) =
        ethereumPriceRepository.getEthereumPriceIn(currency)
            .map {
                amount.divide(it, 3, RoundingMode.HALF_UP).setScale(3).stripTrailingZeros()
            }
}