package com.github.fatihsokmen.wallet.domain

import app.cash.turbine.test
import com.github.fatihsokmen.wallet.data.EthereumPriceRepository
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
class CalculateEthereumGasFeeUseCaseTest(
    private val fastGasFee: BigDecimal,
    private val expectedEthGasFee: BigDecimal
) {

    private val ethereumPriceRepository: EthereumPriceRepository = mockk(relaxed = true)

    @Test
    fun `GIVEN use case WHEN executed THEN should calculate gas fee in eth`() =
        runTest {
            every { ethereumPriceRepository.getGasFee() } returns flowOf(Result.success(fastGasFee))
            val subject = CalculateEthereumGasFeeUseCaseImpl(ethereumPriceRepository)

            subject.execute().test {
                awaitItem() shouldBe expectedEthGasFee
                awaitComplete()
            }
        }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf(BigDecimal(12), BigDecimal("0.003")),
                arrayOf(BigDecimal(50), BigDecimal("0.011")),
                arrayOf(BigDecimal(100), BigDecimal("0.021"))
            )
        }
    }
}

