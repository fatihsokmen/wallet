package com.github.fatihsokmen.wallet.core.di

import com.github.fatihsokmen.wallet.data.EndPoint
import com.github.fatihsokmen.wallet.data.EndPoints
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @EndPoint(EndPoints.CoinGecko)
    fun providesCoinGeckoOkHttpCallFactory(
    ): Call.Factory = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor()
                .apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }
        )
        .build()

    @Provides
    @Singleton
    @EndPoint(EndPoints.EtherScan)
    fun providesEtherScanOkHttpCallFactory(
    ): Call.Factory = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor()
                .apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }
        )
        .build()


    @Provides
    @Singleton
    @EndPoint(EndPoints.EtherScan)
    fun providesEtherScanRetrofit(
        @EndPoint(EndPoints.EtherScan) okhttpCallFactory: Call.Factory
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.etherscan.io/")
            .callFactory(okhttpCallFactory)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @EndPoint(EndPoints.CoinGecko)
    fun providesCoinGeckoRetrofit(
        @EndPoint(EndPoints.CoinGecko) okhttpCallFactory: Call.Factory
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/")
            .callFactory(okhttpCallFactory)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

}
