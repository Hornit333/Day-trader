package com.example.tradingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tradingapp.databinding.FragmentProductsBinding
import com.example.tradingapp.models.Ticker
import com.example.tradingapp.services.CoinbaseApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductsFragment : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductAdapter
    private val products = listOf("BTC-USD", "ETH-USD", "SOL-USD", "DOGE-USD", "ADA-USD")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProductAdapter(emptyList())
        binding.recyclerView.adapter = adapter

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
