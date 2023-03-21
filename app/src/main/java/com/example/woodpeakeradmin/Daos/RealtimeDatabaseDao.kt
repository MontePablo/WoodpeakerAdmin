package com.example.woodpeakeradmin.Daos

import android.util.Log
import android.widget.Toast
import com.example.woodpeakeradmin.models.Product
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

object RealtimeDatabaseDao : ChildEventListener {
    val database = Firebase.database
    val reference = database.getReference("notifications")
    fun addData(key: String,value:String): Task<Void> {
        val v= reference.child(key).setValue(value)
            .addOnCompleteListener {
                Log.d("TAG","data upload finished")
            }.addOnFailureListener {
                Log.d("TAG","realtime data upload failed: ${it.localizedMessage}")
            }
        return v
    }


    fun addValueEventListener(){

    }
    fun addChildEventListener(){
        reference.addChildEventListener(this)
    }

    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        Log.d("TAG",snapshot.value.toString())
        Toast.makeText(context,)
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        TODO("Not yet implemented")
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        TODO("Not yet implemented")
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        TODO("Not yet implemented")
    }

    override fun onCancelled(error: DatabaseError) {
        TODO("Not yet implemented")
    }

}