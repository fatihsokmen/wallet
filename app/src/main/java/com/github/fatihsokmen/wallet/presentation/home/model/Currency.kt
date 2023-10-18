package com.github.fatihsokmen.wallet.presentation.home.model

import androidx.annotation.DrawableRes
import com.github.fatihsokmen.wallet.R

/**
 * Screen model that holds currency symbol, flag resource id and display name
 */
enum class Currency(
    val symbol: String,
    val displayName: String,
    @DrawableRes val flag: Int
) {
    USD(symbol = "$", "Dollars", R.drawable.usd),
    EUR(symbol = "€", "Euros", R.drawable.eur),
    GBP(symbol = "£", "Pounds", R.drawable.gbp)
}