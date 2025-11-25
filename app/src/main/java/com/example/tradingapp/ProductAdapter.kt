package com.example.tradingapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tradingapp.models.Ticker

class ProductAdapter(private var tickers: List<Ticker>) : RecyclerView.Adapter<ProductAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val symbol: TextView = view.findViewById(R.id.symbolText)
        val price: TextView = view.findViewById(R.id.priceText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val t = tickers[position]
        holder.symbol.text = t.productId
        holder.price.text = "$${t.price}"
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, TradeActivity::class.java)
            intent.putExtra("PRODUCT_ID", t.productId)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = tickers.size

    fun update(new: List<Ticker>) {
        tickers = new
        notifyDataSetChanged()
    }
}
