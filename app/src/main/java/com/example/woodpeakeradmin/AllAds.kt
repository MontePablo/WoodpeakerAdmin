package com.example.woodpeakeradmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.woodpeakeradmin.Daos.ProductDao
import com.example.woodpeakeradmin.adapters.ProductsAdapter
import com.example.woodpeakeradmin.adapters.productFuntions
import com.example.woodpeakeradmin.databinding.ActivityAllAdsBinding
import com.example.woodpeakeradmin.models.Product
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.gson.Gson

class AllAds : AppCompatActivity() , productFuntions {
    lateinit var binding:ActivityAllAdsBinding
    lateinit var adapter: ProductsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAllAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerview.layoutManager= LinearLayoutManager(this)
        val query: Query = ProductDao.productCollection
        val options: FirestoreRecyclerOptions<Product> =
            FirestoreRecyclerOptions.Builder<Product>().setQuery(query, Product::class.java).build()
        adapter= ProductsAdapter(options,this)
        binding.recyclerview.adapter=adapter
    }
    override fun productClick(product: Product,productId: String) {
        val gson = Gson()
        val intent = Intent(applicationContext, ProductDetail::class.java)
        intent.putExtra("product", gson.toJson(product))
        intent.putExtra("productId", productId)
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