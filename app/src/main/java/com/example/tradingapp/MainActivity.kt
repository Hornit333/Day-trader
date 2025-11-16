package com.example.tradingapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingapp.models.Ticker
import com.example.tradingapp.services.CoinbaseApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private val products = listOf("BTC-USD", "ETH-USD", "SOL-USD", "DOGE-USD", "ADA-USD")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProductAdapter(emptyList())
        recyclerView.adapter = adapter

        fetchPrices()
    }

    private fun fetchPrices() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.coinbase.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(CoinbaseApi::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val tickers = mutableListOf<Ticker>()
            products.forEach { id ->
                try {
                    val response = api.getTicker(id)
                    tickers.add(response.data)
                } catch (e: Exception) { }
            }
            withContext(Dispatchers.Main) {
                adapter.update(tickers)
            }
        }
    }
}
