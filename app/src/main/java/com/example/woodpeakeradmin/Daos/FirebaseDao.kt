package com.example.woodpeakeradmin.Daos
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirebaseDao {
    val db = Firebase.firestore
//    val database = Firebase.database
    var auth: FirebaseAuth = Firebase.auth
    init {
        Log.d("TAG","firebaseDao: uid: ${auth.uid} null? ${auth.uid==null}  currentuser: ${auth.currentUser} null? ${auth.currentUser==null} ")
    }
}