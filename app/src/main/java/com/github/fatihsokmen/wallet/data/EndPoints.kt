package com.github.fatihsokmen.wallet.data

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class EndPoint(val dispatcher: EndPoints)

enum class EndPoints {
    CoinGecko,
    EtherScan,
}
