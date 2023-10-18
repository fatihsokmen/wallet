package com.github.fatihsokmen.wallet.data

import com.github.fatihsokmen.wallet.data.model.GetGasFeeResponseDto
import retrofit2.http.GET

interface EtherScanApiService {

    @GET("api?module=gastracker&action=gasoracle&apikey=1YSIZ6UQU76ACDXJ95E1FVER3CWHEM385S")
    suspend fun getGasFee(): GetGasFeeResponseDto
}