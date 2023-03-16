package com.example.woodpeakeradmin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.woodpeakeradmin.Daos.OrderDao
import com.example.woodpeakeradmin.adapters.OrderAdapter
import com.example.woodpeakeradmin.adapters.OrderFunctions
import com.example.woodpeakeradmin.databinding.ActivityOrdersBinding
import com.example.woodpeakeradmin.models.Order
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.gson.Gson

class Orders : AppCompatActivity(), OrderFunctions {
    lateinit var binding:ActivityOrdersBinding
    lateinit var adapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityOrdersBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        binding.recyclerview.layoutManager= LinearLayoutManager(this)
        val query: Query = OrderDao.reference.orderBy("dateTime", Query.Direction.ASCENDING)
        val options: FirestoreRecyclerOptions<Order> = FirestoreRecyclerOptions.Builder<Order>().setQuery(query, Order::class.java).build()
        adapter= OrderAdapter(options,this)
        binding.recyclerview.adapter=adapter
        binding.recyclerview.setLayoutManager(
            WrapContentLinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        )
    }

    override fun orderClick(order: Order, orderId: String) {
        val gson = Gson()
        val intent = Intent(this, OrderDetail::class.java)
        intent.putExtra("order", gson.toJson(order))
        intent.putExtra("orderId",orderId)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}