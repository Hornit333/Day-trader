package com.example.tradingapp

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TradeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trade)

        val productId = intent.getStringExtra("PRODUCT_ID")

        val buyButton = findViewById<Button>(R.id.buyButton)
        buyButton.setOnClickListener {
            Toast.makeText(this, "You bought $productId", Toast.LENGTH_SHORT).show()
        }

        val sellButton = findViewById<Button>(R.id.sellButton)
        sellButton.setOnClickListener {
            Toast.makeText(this, "You sold $productId", Toast.LENGTH_SHORT).show()
        }
    }
}
