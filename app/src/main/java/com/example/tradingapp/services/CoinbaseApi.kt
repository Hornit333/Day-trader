
package com.example.tradingapp.services

import com.example.tradingapp.models.TickerResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface CoinbaseApi {
    @GET("api/v3/brokerage/products/{id}/ticker")
    suspend fun getTicker(@Path("id") id: String): TickerResponse
}
