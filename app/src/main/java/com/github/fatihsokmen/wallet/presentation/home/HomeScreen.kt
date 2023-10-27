@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.fatihsokmen.wallet.presentation.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.fatihsokmen.wallet.R
import com.github.fatihsokmen.wallet.presentation.home.model.Currency
import com.github.fatihsokmen.wallet.ui.widget.ShimmerAnimation
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import java.math.BigDecimal

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = MaterialTheme.colorScheme.background
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        UiState(
            userInput = BigDecimal.ZERO.toString(),
            ethAmount = BigDecimal.ZERO,
            ethGasFee = BigDecimal.ZERO,
            currency = Currency.USD,
            inputMode = InputMode.FIAT_TO_ETH,
            sendEnabled = false
        )
    )

    val sheetState = rememberModalBottomSheetState()

    val scope = rememberCoroutineScope()

    var showBottomSheet by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Scaffold(modifier = Modifier.fillMaxHeight()) { padding ->
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
            onOpenCurrencySelector = {
                scope.launch {
                    focusManager.clearFocus().also { showBottomSheet = true }
                    viewModel.onLoadCurrencies(uiState.ethAmount)
                }
            },
            onRotate = viewModel::onSwitchInputModel,
            snackbar = {
                val errorState by viewModel.errorState
                if (errorState.isNotBlank()) {
                    Snackbar(
                        modifier = Modifier.testTag("snackbar"),
                        action = {
                            Button(onClick = { viewModel.clearError() }) {
                                Text(stringResource(id = android.R.string.ok))
                            }
                        },
                    ) { Text(text = errorState) }
                }
            }
        )

        if (showBottomSheet) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet = false }
            ) {
                CurrencyList(
                    current = uiState.currency,
                    bottomSheetState = viewModel.bottomSheetState.value,
                    onDismiss = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    },
                    onNewCurrency = viewModel::onNewFiat
                )
            }
        }
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
    onRotate: (InputMode) -> Unit,
    snackbar: @Composable () -> Unit
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
                .fillMaxWidth(fraction = 0.92f)
                .padding(
                    start = 24.dp,
                    top = dimensionResource(id = R.dimen.home_title_top_padding),
                    bottom = dimensionResource(id = R.dimen.home_title_bottom_padding),
                    end = 24.dp
                )
                .align(Alignment.CenterHorizontally),
            text = stringResource(R.string.home_title_send_ethereum),
            style = MaterialTheme.typography.displaySmall.copy(fontSize = 30.sp)
        )
        CurrencyTextField(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.92f)
                .padding(end = 8.dp)
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
        Row(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.92f)
                .padding(start = 24.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Estimated gas fee",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.home_exchange_est_network_fees_eth, ethGasFee),
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Spacer(
            modifier = Modifier.weight(1f)
        )
        Button(
            enabled = buttonEnabled,
            modifier = Modifier
                .fillMaxWidth(fraction = 0.85f)
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
        snackbar()
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
    CurrencyLayout(
        modifier = modifier
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
                .padding(top = 8.dp, bottom = 16.dp)
        ) {
            Spacer(
                modifier = Modifier.width(16.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .testTag("currency-symbol")
                ) {
                    val symbol = if (inputMode == InputMode.FIAT_TO_ETH) {
                        currency.symbol
                    } else {
                        stringResource(R.string.home_input_eth_symbol)
                    }
                    Text(
                        text = symbol,
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp)
                    )
                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )
                    var inputAmountState by rememberSaveable { mutableStateOf("") }
                    BasicTextField(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("user-input"),
                        value = inputAmountState,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp),
                        onValueChange = {
                            inputAmountState = it
                            onInputAmountChanged(it)
                        },
                        decorationBox = { innerTextField ->
                            if (inputAmountState.isEmpty()) {
                                Text(
                                    stringResource(R.string.home_currency_input_empty_value),
                                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp)
                                )
                            }
                            innerTextField()
                        }
                    )
                }
                if (inputMode.isFiatToEth()) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .testTag("exp-amount-text"),
                        text = stringResource(
                            R.string.home_exchange_conversion_eth_text,
                            ethAmount
                        ),
                        color = Color.Gray.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 14.sp),
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
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(end = 32.dp),
                    text = stringResource(R.string.home_wallet_balance_eth, walletBalance),
                    color = Color.Blue,
                    style = MaterialTheme.typography.labelMedium,
                )
                if (inputMode.isFiatToEth()) {
                    InputChip(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .testTag("currency-chip"),
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

        Box {
            Surface(
                modifier = Modifier
                    .size(width = 48.dp, height = 48.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterStart),
                shadowElevation = 4.dp
            ) {
                val rotation = remember { Animatable(0f) }
                val coroutineScope = rememberCoroutineScope()
                OutlinedIconButton(
                    modifier = Modifier
                        .padding(0.dp)
                        .background(Color.White)
                        .testTag("input-mode"),
                    border = BorderStroke(1.dp, Color.LightGray),
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
        }
    }
}

@Composable
fun CurrencyList(
    current: Currency,
    bottomSheetState: BottomSheetState,
    onNewCurrency: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            modifier = Modifier.padding(
                start = 8.dp,
                end = 8.dp,
                bottom = 32.dp,
                top = 8.dp
            ),
            text = stringResource(R.string.home_bottom_shet_displayed_currencies),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
        )
        val currencyStates = bottomSheetState.currencies
        LazyColumn {
            items(currencyStates.size) {
                val state = currencyStates[it]
                CurrencyItem(
                    modifier = Modifier.testTag("${state.currency.name}-option"),
                    displayName = state.currency.displayName,
                    symbol = state.currency.symbol,
                    code = state.currency.name,
                    flag = state.currency.flag,
                    selected = current == state.currency,
                    ethAmount = bottomSheetState.ethAmount.toString(),
                    status = state.status,
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
    modifier: Modifier,
    displayName: String,
    symbol: String,
    code: String,
    @DrawableRes flag: Int,
    selected: Boolean,
    ethAmount: String,
    status: CurrencyStatus,
    onNewCurrency: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val border = if (selected) {
        BorderStroke(2.dp, Color.DarkGray)
    } else {
        BorderStroke(1.dp, Color.LightGray)
    }
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 8.dp,
                vertical = 8.dp,
            )
            .then(modifier),
        border = border,
        shape = RoundedCornerShape(5.dp),
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
                    .size(36.dp)
                    .align(Alignment.CenterVertically)
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f)
            ) {
                Text(text = displayName, fontWeight = FontWeight.Medium)
                Text(text = code, fontWeight = FontWeight.Normal)
            }
            when (status) {
                CurrencyStatus.Loading -> {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.End

                    ) {
                        Text(text = ethAmount, fontWeight = FontWeight.Medium)
                        ShimmerAnimation { brush ->
                            Box(
                                Modifier
                                    .width(48.dp)
                                    .height(20.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brush)
                            )
                        }
                    }
                }

                is CurrencyStatus.Failed -> {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.End

                    ) {
                        Text(text = ethAmount, fontWeight = FontWeight.Medium)
                        Text(
                            text = status.message,
                            fontWeight = FontWeight.Normal,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                is CurrencyStatus.Loaded -> {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.End

                    ) {
                        Text(text = ethAmount, fontWeight = FontWeight.Medium)
                        Text(text = "$symbol${status.price}", fontWeight = FontWeight.Normal)
                    }
                }
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
        shape = RoundedCornerShape(5.dp),
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
fun CurrencyLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content,
    ) { measurables, constraints ->
        val frame = measurables[0]
        val body = measurables[1]
        val switch = measurables[2]
        val looseConstraints = constraints.copy(
            minWidth = 0,
            minHeight = 0,
        )
        val switchPlaceable = switch.measure(looseConstraints)
        val framePlaceable = frame.measure(
            looseConstraints.copy(maxWidth = looseConstraints.maxWidth - switchPlaceable.width / 2)
        )
        val bodyPlaceable = body.measure(
            looseConstraints.copy(maxWidth = looseConstraints.maxWidth - switchPlaceable.width)
        )
        layout(
            width = constraints.maxWidth,
            height = framePlaceable.height
        ) {
            framePlaceable.placeRelative(
                x = switchPlaceable.width / 2,
                y = 0,
            )
            bodyPlaceable.placeRelative(
                x = switchPlaceable.width,
                y = 0
            )
            switchPlaceable.placeRelative(
                x = 0,
                y = (framePlaceable.height - switchPlaceable.height) / 2,
            )
        }
    }
}