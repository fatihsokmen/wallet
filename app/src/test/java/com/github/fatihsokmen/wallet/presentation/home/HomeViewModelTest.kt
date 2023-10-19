package com.github.fatihsokmen.wallet.presentation.home

import app.cash.turbine.test
import com.github.fatihsokmen.wallet.domain.CalculateEthereumAmountInFiatCurrencyUseCase
import com.github.fatihsokmen.wallet.domain.CalculateEthereumGasFeeUseCase
import com.github.fatihsokmen.wallet.presentation.MainDispatcherRule
import com.github.fatihsokmen.wallet.presentation.home.model.Currency
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class HomeViewModelTest {
    @get: Rule
    val rule = MainDispatcherRule()

    private val calculateEthereumAmountUseCase: CalculateEthereumAmountInFiatCurrencyUseCase =
        mockk(relaxed = true)
    private val calculateEthereumGasFee: CalculateEthereumGasFeeUseCase =
        mockk(relaxed = true)

    @Test
    fun GIVEN_eth_price_and_gas_fee_WHEN_screen_launched_THEN_initial_state_should_be_produced() =
        runTest {
            // GIVEN
            givenEthereumPrice(Currency.USD, BigDecimal.ZERO, BigDecimal.ZERO)
            givenGasFee(BigDecimal("0.01"))

            // WHEN
            val subject = HomeViewModel(
                calculateEthereumAmountUseCase,
                calculateEthereumGasFee
            )

            // THEN
            subject.uiState.test {
                val uiState = awaitItem()
                uiState shouldBe UiState.Success(
                    ethAmount = BigDecimal.ZERO,
                    ethGasFee = BigDecimal("0.01"),
                    currency = Currency.USD,
                    exchange = ExchangeMode.FIAT_TO_ETH,
                    insufficientBalance = false
                )
            }
        }

    @Test
    fun GIVEN_eth_price_and_gas_fee_WHEN_input_fiat_value_THEN_new_state_updated() =
        runTest {
            // GIVEN
            givenEthereumPrice(Currency.USD, price = BigDecimal(1000), eth = BigDecimal("0.6"))
            givenGasFee(gas = BigDecimal("0.02"))

            // WHEN
            val subject = HomeViewModel(
                calculateEthereumAmountUseCase,
                calculateEthereumGasFee
            )
            subject.onNewAmount("1000")

            // THEN
            subject.uiState.test {
                val uiState = awaitItem()
                uiState shouldBe UiState.Success(
                    ethAmount = BigDecimal("0.6"),
                    ethGasFee = BigDecimal("0.02"),
                    currency = Currency.USD,
                    exchange = ExchangeMode.FIAT_TO_ETH,
                    insufficientBalance = false
                )
            }
        }

    @Test
    fun GIVEN_eth_price_and_gas_fee_WHEN_eth_bigger_than_balance_THEN_insufficient_balance() =
        runTest() {
            // GIVEN
            givenEthereumPrice(Currency.USD, price = BigDecimal(100000), eth = BigDecimal("75"))
            givenGasFee(gas = BigDecimal("0.06"))

            // WHEN
            val subject = HomeViewModel(
                calculateEthereumAmountUseCase,
                calculateEthereumGasFee
            )
            subject.onNewAmount("100000")

            // THEN
            subject.uiState.test {
                val uiState = awaitItem()
                uiState shouldBe UiState.Success(
                    ethAmount = BigDecimal("75"),
                    ethGasFee = BigDecimal("0.06"),
                    currency = Currency.USD,
                    exchange = ExchangeMode.FIAT_TO_ETH,
                    insufficientBalance = true
                )
            }
        }

    @Test
    fun GIVEN_eth_price_and_gas_fee_WHEN_currency_pickup_clicked_THEN_insufficient_balance() =
        runTest() {
            // GIVEN
            givenEthereumPrice(Currency.GBP, price = BigDecimal(1000), eth = BigDecimal("0.7"))
            givenGasFee(gas = BigDecimal("0.06"))

            // WHEN
            val subject = HomeViewModel(
                calculateEthereumAmountUseCase,
                calculateEthereumGasFee
            )
            subject.onNewAmount("1000")
            subject.onNewFiat("GBP")

            // THEN
            subject.uiState.test {
                val uiState = awaitItem()
                uiState shouldBe UiState.Success(
                    ethAmount = BigDecimal("0.7"),
                    ethGasFee = BigDecimal("0.06"),
                    currency = Currency.GBP,
                    exchange = ExchangeMode.FIAT_TO_ETH,
                    insufficientBalance = false
                )
            }
        }

    private fun givenGasFee(gas: BigDecimal) {
        every {
            calculateEthereumGasFee.execute()
        } returns flowOf(gas)
    }

    private fun givenEthereumPrice(currency: Currency, price: BigDecimal, eth: BigDecimal) {
        every {
            calculateEthereumAmountUseCase.execute(currency, price)
        } returns flowOf(eth)
    }
}