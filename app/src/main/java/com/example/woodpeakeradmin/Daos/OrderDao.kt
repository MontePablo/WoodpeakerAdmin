package com.example.woodpeakeradmin.Daos

import com.example.woodpeakeradmin.models.Order
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

object OrderDao {
    val reference=FirebaseDao.db.collection("orders")
    fun addOrder(order: Order): Task<Void> {
        return reference.document().set(order)
    }
    fun getOrder(orderId: String): Task<DocumentSnapshot> {
        return reference.document(orderId).get()
    }
    fun updateOrder(order: Order,id:String): Task<Void> {
        return reference.document(id).set(order)
    }

}