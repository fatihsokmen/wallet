package com.github.fatihsokmen.wallet.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.fatihsokmen.wallet.domain.CalculateEthereumAmountInFiatCurrencyUseCase
import com.github.fatihsokmen.wallet.domain.CalculateEthereumGasFeeUseCase
import com.github.fatihsokmen.wallet.presentation.home.model.Currency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import java.math.BigDecimal
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val calculateEthereumAmountUseCase: CalculateEthereumAmountInFiatCurrencyUseCase,
    private val calculateEthereumGasFee: CalculateEthereumGasFeeUseCase
) : ViewModel() {

    private val inputAmount = MutableStateFlow("0")
    private val inputCurrency = MutableStateFlow(Currency.USD)
    private val inputExchangeMode = MutableStateFlow(ExchangeMode.FIAT_TO_ETH)

    /**
     * We define inputs above and combine them to produce latest screen state
     * - inputAmount: Input field to input currency or eth
     * - inputCurrency: Currency selected on bottom sheet (default usd)
     */
    val uiState =
        combine(inputAmount, inputCurrency, inputExchangeMode) { amount, currency, conversion ->
            val sanitisedAmount = (amount.toBigDecimalOrNull() ?: BigDecimal.ZERO).toString()
            UiInputs(
                value = sanitisedAmount,
                currencySelection = currency,
                exchangeMode = conversion
            )
        }
            // skip some keystrokes to avoid bunch multiple api calls
            .debounce(WAIT_BETWEEN_KEYSTROKES_IN_MS)
            // We have now all inputs and ready to produce screen state
            .flatMapLatest { inputs ->
                combine(
                    calculateEthereumAmountUseCase.execute(
                        inputs.currencySelection, BigDecimal(inputs.value)
                    ),
                    calculateEthereumGasFee.execute(),
                    inputExchangeMode
                ) { ethAmount, ethGasFee, exchangeMode ->
                    UiState.Success(
                        ethAmount,
                        ethGasFee,
                        inputs.currencySelection,
                        inputs.exchangeMode,
                        insufficientBalance = ethAmount > WALLET_ETH_BALANCE
                    )
                }.catch {
                    UiState.Error(it.message.orEmpty())
                }
            }
            .shareIn(
                replay = 1, // On configuration change, replay last value
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
            )

    fun onNewAmount(value: String) {
        inputAmount.tryEmit(value)
    }

    fun onNewFiat(value: String) {
        val currency = Currency.valueOf(value)
        inputCurrency.tryEmit(currency)
    }

    fun onSwitchInputModel(newExchange: ExchangeMode) {
        this.inputExchangeMode.tryEmit(newExchange)
    }

    companion object {
        private const val WAIT_BETWEEN_KEYSTROKES_IN_MS: Long = 300

        val WALLET_ETH_BALANCE = BigDecimal.TEN
    }
}

enum class ExchangeMode {
    FIAT_TO_ETH,
    ETH_TO_FIAT
}

fun ExchangeMode.isFiatToEth() =
    this == ExchangeMode.FIAT_TO_ETH

data class UiInputs(
    val value: String,
    val currencySelection: Currency,
    val exchangeMode: ExchangeMode
)

sealed class UiState {
    data class Success(
        val ethAmount: BigDecimal,
        val ethGasFee: BigDecimal,
        val currency: Currency,
        val exchange: ExchangeMode,
        val insufficientBalance: Boolean
    ) : UiState()

    data class Error(val message: String) : UiState()
}

