@file:Suppress("INFERRED_TYPE_VARIABLE_INTO_EMPTY_INTERSECTION_WARNING")

package com.github.fatihsokmen.wallet.data

import app.cash.turbine.test
import com.github.fatihsokmen.wallet.data.model.GetGasFeeResponseDto
import com.github.fatihsokmen.wallet.data.model.GetGasFeeResponseResultDto
import com.github.fatihsokmen.wallet.data.model.GetPriceResponseDto
import com.github.fatihsokmen.wallet.presentation.home.model.Currency
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal

@RunWith(Parameterized::class)
class EthereumPriceRepositoryImplTest(
    private val currencyCode: Currency,
    private val apiResponse: String,
    private val expected: BigDecimal
) {

    private val coinGeckoApiService: CoinGeckoApiService = mockk(relaxed = true)
    private val etherScanApiService: EtherScanApiService = mockk(relaxed = true)

    @Test
    fun `GIVEN currency WHEN executed THEN ethereum price should return in currency`() = runTest {
        val code = currencyCode.name.lowercase()
        coEvery {
            coinGeckoApiService.getEthereumPriceIn(code)
        } returns GetPriceResponseDto(mapOf(code to apiResponse))

        val dispatcher = StandardTestDispatcher(testScheduler)
        val subject =
            EthereumPriceRepositoryImpl(coinGeckoApiService, etherScanApiService, dispatcher)

        subject.getEthereumPriceIn(currency = currencyCode).test {
            awaitItem() shouldBe expected
            awaitComplete()
        }
    }

    @Test
    fun `GIVEN use case WHEN executed successfully THEN ethereum gas fee should be fetched from api`() =
        runTest {
            coEvery {
                etherScanApiService.getGasFee()
            } returns GetGasFeeResponseDto(result = GetGasFeeResponseResultDto(10))

            val dispatcher = StandardTestDispatcher(testScheduler)
            val subject =
                EthereumPriceRepositoryImpl(coinGeckoApiService, etherScanApiService, dispatcher)

            subject.getGasFee().test {
                val actual = awaitItem()
                actual.shouldBeInstanceOf<Result<Long>>()
                actual.getOrThrow() shouldBe BigDecimal(10)
                awaitComplete()
            }
        }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf(Currency.USD, "1500", BigDecimal("1500")),
                arrayOf(Currency.EUR, "1400", BigDecimal("1400")),
                arrayOf(Currency.GBP, "1200", BigDecimal("1200"))
            )
        }
    }
}