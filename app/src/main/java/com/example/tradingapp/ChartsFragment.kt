package com.example.tradingapp

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.tradingapp.databinding.FragmentChartsBinding
import com.example.tradingapp.models.Candle
import com.example.tradingapp.services.CoinbaseApi
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ChartsFragment : Fragment() {

    private var _binding: FragmentChartsBinding? = null
    private val binding get() = _binding!!
    private val products = listOf("BTC-USD", "ETH-USD", "SOL-USD", "DOGE-USD", "ADA-USD")
    private var selectedProduct = products[0]
    private val candleEntries = mutableListOf<CandleEntry>()
    private var webSocket: WebSocket? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, products)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.productSpinner.adapter = adapter
        binding.productSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedProduct = products[position]
                fetchCandles()
                startWebSocket()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        fetchCandles()
        startWebSocket()
    }

    private fun fetchCandles() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.pro.coinbase.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(CoinbaseApi::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getCandles(selectedProduct)
                candleEntries.clear()
                response.forEachIndexed { index, candle ->
                    candleEntries.add(
                        CandleEntry(
                            index.toFloat(),
                            candle.high.toFloat(),
                            candle.low.toFloat(),
                            candle.open.toFloat(),
                            candle.close.toFloat()
                        )
                    )
                }
                withContext(Dispatchers.Main) {
                    updateChart()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startWebSocket() {
        webSocket?.cancel()
        val client = OkHttpClient.Builder().readTimeout(0, TimeUnit.MILLISECONDS).build()
        val request = Request.Builder().url("wss://ws-feed.pro.coinbase.com").build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                val subscribeMsg = "{\"type\":\"subscribe\",\"product_ids\":[\"$selectedProduct\"],\"channels\":[\"ticker\"]}"
                webSocket.send(subscribeMsg)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                if (text.contains("ticker")) {
                    val price = text.split("\"price\":\"")[1].split("\"")[0].toFloat()
                    updateLastCandle(price)
                }
            }
        })
        client.dispatcher.executorService.shutdown()
    }

    private fun updateLastCandle(price: Float) {
        if (candleEntries.isNotEmpty()) {
            val lastEntry = candleEntries.last()
            lastEntry.close = price
            if (price > lastEntry.high) lastEntry.high = price
            if (price < lastEntry.low) lastEntry.low = price

            CoroutineScope(Dispatchers.Main).launch {
                updateChart()
            }
        }
    }

    private fun updateChart() {
        val dataSet = CandleDataSet(candleEntries, "Price").apply {
            color = Color.rgb(80, 80, 80)
            shadowColor = Color.DKGRAY
            shadowWidth = 0.7f
            decreasingColor = Color.RED
            decreasingPaintStyle = Paint.Style.FILL
            increasingColor = Color.GREEN
            increasingPaintStyle = Paint.Style.FILL
            neutralColor = Color.BLUE
            setDrawValues(false)
        }

        binding.candleStickChart.apply {
            description.text = "Candlestick Chart"
            description.textColor = Color.WHITE
            description.textSize = 14f

            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            axisRight.isEnabled = false
            legend.isEnabled = false

            data = CandleData(dataSet)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        webSocket?.cancel()
    }
}
