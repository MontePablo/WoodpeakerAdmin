package com.example.woodpeakeradmin.Daos

import android.util.Log
import com.example.woodpeakeradmin.models.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import java.text.SimpleDateFormat
import java.util.*

object UserDao {
     var user= User()
    var collection:CollectionReference
    init {
        collection = FirebaseDao.db.collection("users")
        getUser(FirebaseDao.auth.uid!!).addOnSuccessListener { document->
            document.toObject(User::class.java)?.let { user ->
                this.user=user
            }
        }
    }
    fun addUser(user: User): Task<Void> {
        var v= collection.document(FirebaseDao.auth.uid!!).set(user)
        Log.d("TAG", "add user:success")
        v.addOnSuccessListener { init() }
        return v
    }
    fun getUser(userId:String): Task<DocumentSnapshot> {
        return collection.document(userId).get()
    }
    private fun init(){
        getUser(FirebaseDao.auth.uid!!).addOnSuccessListener {document ->
            document.toObject(User::class.java)?.let { user ->
                this.user=user
                if(user.packExpiryDate.isNotEmpty()){
                    val formatter = SimpleDateFormat.getDateInstance()
                    val todayCal=Calendar.getInstance()
                    val expCal=Calendar.getInstance()
                    expCal.time=formatter.parse(formatter.format(user.packExpiryDate))

                    if(todayCal.after(expCal)){
                        user.packExpiryDate=""
                        user.packBuyDate=""
                        user.pack="Customer"
                        this.user=user
                        updateUser()
                    }

                }
            }
        }.addOnFailureListener {
            Log.d("TAG","user fetch failed:${it.localizedMessage}")
        }
    }
    fun updateUser(): Task<Void> {
        var v = collection.document(user.id).set(user)
        Log.d("TAG", "update user:success")
        return v
    }
}