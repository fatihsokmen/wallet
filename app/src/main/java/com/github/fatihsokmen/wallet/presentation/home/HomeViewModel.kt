package com.github.fatihsokmen.wallet.presentation.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.fatihsokmen.wallet.R
import com.github.fatihsokmen.wallet.core.StringResources
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import java.io.IOException
import java.math.BigDecimal
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val calculateEthereumAmountUseCase: CalculateEthereumAmountInFiatCurrencyUseCase,
    private val calculateEthereumGasFee: CalculateEthereumGasFeeUseCase,
    private val stringResources: StringResources
) : ViewModel() {

    /**
     * User inputs
     */
    private val inputAmount = MutableStateFlow("0")
    private val inputCurrency = MutableStateFlow(Currency.USD)
    private val inputMode = MutableStateFlow(InputMode.FIAT_TO_ETH)

    /**
     *  Use inputs above and combine them to produce input state
     */
    private val uiInputs =
        combine(inputAmount, inputCurrency, inputMode) { amount, currency, conversion ->
            val sanitisedAmount = (amount.toBigDecimalOrNull() ?: BigDecimal.ZERO).toString()
            UiInputs(
                value = sanitisedAmount,
                currencySelection = currency,
                inputMode = conversion
            )
        }

    /**
     * Produce screen state
     */
    val uiState = uiInputs
        // skip some keystrokes to avoid bunch multiple api calls
        .debounce(WAIT_BETWEEN_KEYSTROKES_IN_MS)
        // We have now all inputs and ready to produce screen state
        .flatMapLatest { inputs ->
            combine(
                if (inputs.inputMode.isFiatToEth()) {
                    calculateEthereumAmountUseCase.execute(
                        inputs.currencySelection,
                        BigDecimal(inputs.value)
                    )
                } else {
                    flowOf(inputs.value.toBigDecimalOrNull() ?: BigDecimal.ZERO)
                },
                calculateEthereumGasFee.execute(),
                inputMode,
                inputAmount
            ) { ethAmount, ethGasFee, inputMode, userInput ->
                UiState(
                    userInput,
                    ethAmount,
                    ethGasFee,
                    inputs.currencySelection,
                    inputMode,
                    sendEnabled = hasSufficientBalance(ethAmount)
                )
            }.catch {
                if (it is IOException) {
                    errorState.value =
                        stringResources.getString(R.string.home_error_internet_connection)
                } else {
                    errorState.value = stringResources.getString(R.string.home_error_generic)
                }
            }
        }
        .shareIn(
            replay = 1, // On configuration change, replay last value
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
        )

    val errorState = mutableStateOf("")

    fun onNewAmount(value: String) {
        inputAmount.tryEmit(value)
    }

    fun onNewFiat(value: String) {
        val currency = Currency.valueOf(value)
        inputCurrency.tryEmit(currency)
    }

    fun onSwitchInputModel(newExchange: InputMode) {
        this.inputMode.tryEmit(newExchange)
    }

    fun clearError() {
        errorState.value = ""
    }

    private fun hasSufficientBalance(ethAmount: BigDecimal) =
        ethAmount > BigDecimal.ZERO && ethAmount <= WALLET_ETH_BALANCE

    companion object {
        private const val WAIT_BETWEEN_KEYSTROKES_IN_MS: Long = 300

        val WALLET_ETH_BALANCE: BigDecimal = BigDecimal.TEN
    }
}

enum class InputMode {
    FIAT_TO_ETH,
    ETH_ONLY
}

fun InputMode.isFiatToEth() =
    this == InputMode.FIAT_TO_ETH

data class UiInputs(
    val value: String,
    val currencySelection: Currency,
    val inputMode: InputMode
)

data class UiState(
    val userInput: String,
    val ethAmount: BigDecimal,
    val ethGasFee: BigDecimal,
    val currency: Currency,
    val inputMode: InputMode,
    val sendEnabled: Boolean,
    val errorMessage: String? = null
)

