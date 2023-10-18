package com.github.fatihsokmen.wallet.data.di

import com.github.fatihsokmen.wallet.data.EndPoint
import com.github.fatihsokmen.wallet.data.EndPoints
import com.github.fatihsokmen.wallet.data.CoinGeckoApiService
import com.github.fatihsokmen.wallet.data.EtherScanApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun providesCoinGeckoApiService(@EndPoint(EndPoints.CoinGecko) retrofit: Retrofit): CoinGeckoApiService =
        retrofit.create(CoinGeckoApiService::class.java)

    @Provides
    @Singleton
    fun providesEtherScanApiService(@EndPoint(EndPoints.EtherScan) retrofit: Retrofit): EtherScanApiService =
        retrofit.create(EtherScanApiService::class.java)

}