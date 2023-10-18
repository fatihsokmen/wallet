package com.github.fatihsokmen.wallet.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GetPriceResponseDto(
    val ethereum: Map<String, String>
)