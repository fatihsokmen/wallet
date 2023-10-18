package com.github.fatihsokmen.wallet.data

import com.github.fatihsokmen.wallet.data.model.GetPriceResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinGeckoApiService {

    @GET("api/v3/simple/price?ids=ethereum")
    suspend fun getEthereumPriceIn(
        @Query("vs_currencies") fiatCurrencyCode: String
    ): GetPriceResponseDto
}