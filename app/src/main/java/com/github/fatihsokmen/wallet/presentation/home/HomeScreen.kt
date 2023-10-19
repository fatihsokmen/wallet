@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.github.fatihsokmen.wallet.presentation.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.fatihsokmen.wallet.R
import com.github.fatihsokmen.wallet.presentation.home.model.Currency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.math.BigDecimal

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    scope: CoroutineScope = rememberCoroutineScope()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        UiState.Success(
            userInput = BigDecimal.ZERO.toString(),
            ethAmount = BigDecimal.ZERO,
            ethGasFee = BigDecimal.ZERO,
            currency = Currency.USD,
            inputMode = InputMode.FIAT_TO_ETH,
            sendEnabled = false
        )
    )

    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Hidden,
        skipHiddenState = false
    )

    BottomSheetScaffold(
        modifier = Modifier.fillMaxHeight(),
        scaffoldState = rememberBottomSheetScaffoldState(sheetState),
        sheetPeekHeight = 0.dp,
        sheetContent = {
            CurrencyList(
                current = uiState.currency,
                onDismiss = { scope.launch { sheetState.hide() } },
                onNewCurrency = viewModel::onNewFiat
            )
        }) { padding ->
        HomeContent(
            modifier = Modifier.padding(padding),
            ethAmount = uiState.ethAmount,
            ethGasFee = uiState.ethGasFee,
            currency = uiState.currency,
            buttonLabel = if (uiState.inputMode == InputMode.FIAT_TO_ETH) {
                stringResource(
                    R.string.home_send_button_fiat_label,
                    uiState.currency.symbol,
                    uiState.ethAmount
                )
            } else {
                stringResource(
                    R.string.home_send_button_eth_label,
                    uiState.userInput
                )
            },
            buttonEnabled = uiState.sendEnabled,
            walletBalance = HomeViewModel.WALLET_ETH_BALANCE,
            inputMode = uiState.inputMode,
            onInputAmountChanged = viewModel::onNewAmount,
            onOpenCurrencySelector = { scope.launch { sheetState.expand() } },
            onRotate = viewModel::onSwitchInputModel
        )
    }
}

@Composable
fun HomeContent(
    modifier: Modifier,
    ethAmount: BigDecimal,
    ethGasFee: BigDecimal,
    currency: Currency,
    buttonLabel: String,
    buttonEnabled: Boolean,
    walletBalance: BigDecimal,
    inputMode: InputMode,
    onInputAmountChanged: (String) -> Unit,
    onOpenCurrencySelector: () -> Unit,
    onRotate: (InputMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        Spacer(
            modifier = Modifier.height(dimensionResource(id = R.dimen.home_title_top_padding))
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.85f)
                .align(Alignment.CenterHorizontally),
            text = stringResource(R.string.home_title_send_ethereum),
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(
            modifier = Modifier.height(dimensionResource(id = R.dimen.home_title_bottom_padding))
        )
        CurrencyTextField(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.85f)
                .align(Alignment.CenterHorizontally)
                .height(dimensionResource(id = R.dimen.home_input_height)),
            ethAmount = ethAmount,
            currency = currency,
            walletBalance = walletBalance,
            inputMode = inputMode,
            onInputAmountChanged = onInputAmountChanged,
            onOpenCurrencySelector = onOpenCurrencySelector,
            onRotate = onRotate
        )
        Spacer(
            modifier = Modifier.height(16.dp)
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.70f)
                .align(Alignment.CenterHorizontally),
            text = stringResource(R.string.home_exchange_est_network_fees_eth, ethGasFee),
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(
            modifier = Modifier.weight(1f)
        )
        Button(
            enabled = buttonEnabled,
            modifier = Modifier
                .fillMaxWidth(fraction = 0.8f)
                .align(alignment = Alignment.CenterHorizontally)
                .height(64.dp),
            shape = RoundedCornerShape(16),
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.buttonColors(Color.Black)
        ) {
            Text(
                text = buttonLabel,
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}

@Composable
private fun CurrencyTextField(
    modifier: Modifier,
    ethAmount: BigDecimal,
    currency: Currency,
    walletBalance: BigDecimal,
    inputMode: InputMode,
    onInputAmountChanged: (String) -> Unit,
    onOpenCurrencySelector: () -> Unit,
    onRotate: (InputMode) -> Unit
) {
    CurrencyFrame(
        modifier = modifier,
        startMargin = 24.dp
    ) {
        OutlinedCard(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            border = BorderStroke(1.5.dp, Color.LightGray),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {}
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, bottom = 16.dp, end = 16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(width = 48.dp, height = 48.dp)
                    .clip(CircleShape)
                    .border(BorderStroke(1.dp, Color.Gray))
                    .align(Alignment.CenterVertically),
                shadowElevation = 4.dp
            ) {
                val rotation = remember { Animatable(0f) }
                val coroutineScope = rememberCoroutineScope()
                OutlinedIconButton(
                    modifier = Modifier
                        .padding(0.dp)
                        .background(Color.White)
                        .align(alignment = Alignment.CenterVertically),
                    onClick = {
                        val (target, mode) = if (rotation.value == 0f) {
                            Pair(180f, InputMode.ETH_ONLY)
                        } else {
                            Pair(0f, InputMode.FIAT_TO_ETH)
                        }
                        coroutineScope.launch {
                            rotation.animateTo(target)
                            onRotate(mode)
                        }
                    },
                ) {
                    Icon(
                        modifier = Modifier.rotate(rotation.value),
                        painter = painterResource(id = R.drawable.ic_change),
                        contentDescription = null,
                        tint = Color.DarkGray
                    )
                }
            }
            Spacer(
                modifier = Modifier.width(16.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    val symbol = if (inputMode == InputMode.FIAT_TO_ETH) {
                        currency.symbol
                    } else {
                        stringResource(R.string.home_input_symbol)
                    }
                    Text(
                        text = symbol, style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )
                    var inputAmountState by rememberSaveable { mutableStateOf("") }
                    BasicTextField(
                        modifier = Modifier.weight(1f),
                        value = inputAmountState,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.headlineMedium,
                        onValueChange = {
                            inputAmountState = it
                            onInputAmountChanged(it)
                        },
                        decorationBox = { innerTextField ->
                            if (inputAmountState.isEmpty()) {
                                Text(
                                    stringResource(R.string.home_currency_input_empty_value),
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            }
                            innerTextField()
                        }
                    )
                }
                if (inputMode.isFiatToEth()) {
                    Text(
                        modifier = Modifier.align(Alignment.BottomStart),
                        text = stringResource(
                            R.string.home_exchange_conversion_eth_text,
                            ethAmount
                        ),
                        color = Color.Gray.copy(alpha = 0.6f),
                    )
                }
            }
            Spacer(
                modifier = Modifier.width(16.dp)
            )
            Box(
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    modifier = Modifier.align(Alignment.TopStart),
                    text = stringResource(R.string.home_wallet_balance_eth, walletBalance),
                    color = Color.Blue,
                    style = MaterialTheme.typography.labelLarge
                )
                if (inputMode.isFiatToEth()) {
                    InputChip(
                        modifier = Modifier.align(Alignment.CenterStart),
                        selected = false,
                        onClick = onOpenCurrencySelector,
                        border = null,
                        label = { Text(text = currency.name.uppercase()) },
                        avatar = {
                            Image(
                                painterResource(id = currency.flag),
                                contentDescription = stringResource(R.string.home_currency_flag),
                                Modifier.size(InputChipDefaults.AvatarSize)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = stringResource(R.string.home_currency_select),
                                Modifier.size(InputChipDefaults.AvatarSize)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CurrencyList(
    current: Currency,
    onNewCurrency: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 16.dp,
            ),
            text = stringResource(R.string.home_bottom_shet_displayed_currencies),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
        )
        val currencies = Currency.entries
        LazyColumn {
            items(currencies.size) {
                val currency = currencies[it]
                CurrencyItem(
                    displayName = currency.displayName,
                    code = currency.name,
                    flag = currency.flag,
                    selected = current == currency,
                    onNewCurrency = onNewCurrency,
                    onDismiss = onDismiss
                )
            }
            item {
                CurrencyInfo()
            }
        }
    }
}

@Composable
fun CurrencyItem(
    displayName: String,
    code: String,
    @DrawableRes flag: Int,
    selected: Boolean,
    onNewCurrency: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val border = if (selected) {
        BorderStroke(2.dp, Color.Black)
    } else {
        BorderStroke(1.dp, Color.Gray)
    }
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 8.dp,
                vertical = 8.dp,
            ),
        border = border,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        onClick = {
            onNewCurrency(code)
            onDismiss()
        }
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painterResource(id = flag),
                contentDescription = code,
                Modifier
                    .size(InputChipDefaults.AvatarSize)
                    .align(Alignment.CenterVertically)
            )
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = displayName, fontWeight = FontWeight.Black)
                Text(text = code, fontWeight = FontWeight.Light)
            }
        }
    }
}

@Composable
fun CurrencyInfo() {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 8.dp,
                vertical = 16.dp,
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Icon(
                Icons.Filled.Info, "menu", modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                text = stringResource(R.string.home_bottom_sheet_info),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun CurrencyFrame(
    startMargin: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content,
    ) { measurables, constraints ->
        val frame = measurables[0]
        val body = measurables[1]
        val looseConstraints = constraints.copy(
            minWidth = 0,
            minHeight = 0,
        )
        val framePlaceable =
            frame.measure(looseConstraints.copy(maxWidth = looseConstraints.maxWidth - startMargin.roundToPx()))
        val bodyPlaceable = body.measure(looseConstraints)
        layout(
            width = constraints.maxWidth,
            height = framePlaceable.height
        ) {
            framePlaceable.placeRelative(
                x = startMargin.roundToPx(),
                y = 0,
            )
            bodyPlaceable.placeRelative(
                x = 0,
                y = 0
            )
        }
    }
}