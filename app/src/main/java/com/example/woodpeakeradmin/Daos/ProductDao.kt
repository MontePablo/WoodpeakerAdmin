package com.example.woodpeakeradmin.Daos

import android.util.Log
import com.example.woodpeakeradmin.models.Product
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query

object ProductDao {
    var productCollection=FirebaseDao.db.collection("products")
    fun addProduct(product: Product): Task<Void> {
        var v= productCollection.document().set(product)
        Log.d("TAG", "add product:success")
        return v
    }
    fun getProduct(id:String): Task<DocumentSnapshot> {
        return productCollection.document(id).get()
    }
    fun updateProduct(id:String,product: Product):Task<Void>{
        return productCollection.document(id).set(product)

    }

}