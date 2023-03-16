package com.example.woodpeakeradmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.woodpeakeradmin.Daos.OrderDao
import com.example.woodpeakeradmin.Daos.ProductDao
import com.example.woodpeakeradmin.databinding.ActivityOrderDetailBinding
import com.example.woodpeakeradmin.models.Order
import com.example.woodpeakeradmin.models.Product
import com.google.gson.Gson

class OrderDetail : AppCompatActivity() {
    lateinit var binding: ActivityOrderDetailBinding
    lateinit var order: Order
    lateinit var orderId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        order= Gson().fromJson(intent.getStringExtra("order"), Order::class.java)
        orderId =intent.getStringExtra("orderId").toString()

        Glide.with(binding.orderDetailImage.context).load(order.image).into(binding.orderDetailImage)
        binding.orderDetailImage.setOnClickListener(View.OnClickListener {
            ProductDao.getProduct(order.productId).addOnSuccessListener { document->
                val product=document.toObject(Product::class.java)
                val gson = Gson()
                val intent = Intent(this, OrderDetail::class.java)
                intent.putExtra("productId",document.id)
                intent.putExtra("product", gson.toJson(product))
                startActivity(intent)
            }
        })

        binding.orderDetailPrice.text=order.finalPriceAftrDiscnt
        binding.orderDetailTitle.text=order.title
        binding.orderDetailDateTime.text=order.dateTime
        binding.paymentStatus.text= "Payment id: "+order.paymentId
        binding.orderDetailDeliveryInstruction.text=order.instruction
        binding.updateInstruction.setOnClickListener(View.OnClickListener {
            val s=binding.newDeliveryInstruction.text.toString()
            order.instruction=order.instruction+"\n $s"
            OrderDao.updateOrder(order,orderId)
        })


    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}