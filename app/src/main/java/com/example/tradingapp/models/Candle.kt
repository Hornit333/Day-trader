package com.example.tradingapp.models

import com.google.gson.annotations.SerializedName

class Candle(
    @SerializedName("0") val time: Long,
    @SerializedName("1") val low: Double,
    @SerializedName("2") val high: Double,
    @SerializedName("3") val open: Double,
    @SerializedName("4") val close: Double,
    @SerializedName("5") val volume: Double
)
