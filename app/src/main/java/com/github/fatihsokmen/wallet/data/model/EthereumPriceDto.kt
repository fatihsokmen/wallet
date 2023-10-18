package com.github.fatihsokmen.wallet.data.model

import kotlinx.serialization.Serializable

@Serializable
data class EthereumPriceDto(
    val usd: Double
)