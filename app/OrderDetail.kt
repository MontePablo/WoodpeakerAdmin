package com.example.woodpeaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.woodpeaker.daos.ProductDao
import com.example.woodpeaker.databinding.ActivityOrderDetailBinding
import com.example.woodpeaker.models.Order
import com.example.woodpeaker.models.Product
import com.example.woodpeaker.models.User
import com.google.gson.Gson

class OrderDetail : AppCompatActivity() {
    lateinit var binding:ActivityOrderDetailBinding
    lateinit var order: Order
    lateinit var orderId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        order=Gson().fromJson(intent.getStringExtra("order"), Order::class.java)
        orderId =intent.getStringExtra("orderId")!!

        Glide.with(binding.orderDetailImage.context).load(order.image).into(binding.orderDetailImage)
        binding.orderDetailImage.setOnClickListener(View.OnClickListener {
            ProductDao.getProduct(order.productId).addOnSuccessListener { document->
                val product=document.toObject(Product::class.java)
                val gson = Gson()
                val intent = Intent(this, ProductDetail::class.java)
                intent.putExtra("product", gson.toJson(product))
                startActivity(intent)
            }
        })

        binding.orderDetailPrice.text=order.finalPriceAftrDiscnt
        binding.orderDetailTitle.text=order.title
        binding.orderDetailDateTime.text=order.dateTime
        binding.paymentStatus.text=order.paymentId
        binding.orderDetailDeliveryInstruction.text=order.instruction


    }
}