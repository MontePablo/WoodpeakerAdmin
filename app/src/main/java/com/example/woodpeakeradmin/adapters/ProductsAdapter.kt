package com.example.woodpeakeradmin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.woodpeakeradmin.R
import com.example.woodpeakeradmin.models.Product
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.ObservableSnapshotArray

class ProductsAdapter(options: FirestoreRecyclerOptions<Product>, listener:productFuntions) :
    FirestoreRecyclerAdapter<Product, ProductsAdapter.ViewHolder>(options) {
     var listener:productFuntions
    init {
        this.listener=listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Product) {
        holder.title.text=model.title
        holder.price.text=model.price
        if(model.images.redLink.isNotEmpty()){
            val images=model.images.redLink
            Glide.with(holder.image.context).load(images[0]).into(holder.image)
        }else if(model.images.blueLink.isNotEmpty()){
            val images=model.images.blueLink
            Glide.with(holder.image.context).load(images[0]).into(holder.image)
        }else if(model.images.yellowLink.isNotEmpty()){
            val images=model.images.yellowLink
            Glide.with(holder.image.context).load(images[0]).into(holder.image)
        }else if(model.images.blackLink.isNotEmpty()){
            val images=model.images.blackLink
            Glide.with(holder.image.context).load(images[0]).into(holder.image)
        }else if(model.images.greenLink.isNotEmpty()){
            val images=model.images.greenLink
            Glide.with(holder.image.context).load(images[0]).into(holder.image)
        }


        val snapshots: ObservableSnapshotArray<Product> = snapshots
        val productId=snapshots.getSnapshot(holder.bindingAdapterPosition).id
        holder.root.setOnClickListener(View.OnClickListener { listener.productClick(model,productId) })

    }



    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        var image=view.findViewById<ImageView>(R.id.item_product_image)
        var title=view.findViewById<TextView>(R.id.item_product_title)
        var price=view.findViewById<TextView>(R.id.item_product_price)
        var root=view.findViewById<ConstraintLayout>(R.id.item_product_root_view)
    }
}
interface productFuntions{
    fun productClick(product: Product, productId: String)
}
