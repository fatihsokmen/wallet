package com.github.fatihsokmen.wallet.data.model

data class GetGasFeeResponseDto(
    val result: GetGasFeeResponseResultDto
)

data class GetGasFeeResponseResultDto(
    val FastGasPrice: Long
)