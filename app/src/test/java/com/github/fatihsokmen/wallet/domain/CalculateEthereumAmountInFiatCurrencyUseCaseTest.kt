package com.github.fatihsokmen.wallet.domain

import app.cash.turbine.test
import com.github.fatihsokmen.wallet.data.EthereumPriceRepository
import com.github.fatihsokmen.wallet.presentation.home.model.Currency
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal

@RunWith(Parameterized::class)
class CalculateEthereumAmountInFiatCurrencyUseCaseTest(
    private val currency: Currency,
    private val ethPriceInCurrency: BigDecimal,
    private val amount: BigDecimal,
    private val expectedEthAmount: BigDecimal
) {
    private val ethereumPriceRepository: EthereumPriceRepository = mockk(relaxed = true)

    @Test
    fun `GIVEN currency and amount WHEN executed THEN should calculate eth amount in currency`() =
        runTest {
            every {
                ethereumPriceRepository.getEthereumPriceIn(currency)
            } returns flowOf(ethPriceInCurrency)

            val subject =
                CalculateEthereumAmountInFiatCurrencyUseCaseImpl(
                    ethereumPriceRepository
                )

            subject.execute(currency = currency, amount = amount).test {
                awaitItem() shouldBe expectedEthAmount
                cancelAndConsumeRemainingEvents()
            }
        }

    companion object {
        private val ETH_PRICE_IN_DOLLARS = BigDecimal(2400) // Let's hope :)
        private val ETH_PRICE_IN_EUROS = BigDecimal(2200)
        private val ETH_PRICE_IN_POUNDS = BigDecimal(2000)

        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf(
                    Currency.USD, ETH_PRICE_IN_DOLLARS, BigDecimal(2400), BigDecimal("1")
                ),
                arrayOf(
                    Currency.EUR, ETH_PRICE_IN_EUROS, BigDecimal(2200), BigDecimal("1")
                ),
                arrayOf(
                    Currency.GBP, ETH_PRICE_IN_POUNDS, BigDecimal(2000), BigDecimal("1")
                )
            )
        }
    }
}