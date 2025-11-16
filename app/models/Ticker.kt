package com.example.tradingapp.models

data class TickerResponse(val data: Ticker)
data class Ticker(val productId: String, val price: String, val time: String)
