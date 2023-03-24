package com.example.woodpeakeradmin.Daos

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

object RealtimeDatabaseDao
//    : ChildEventListener
{
    lateinit var activity:AppCompatActivity
    val database = Firebase.database
    val reference = database.getReference("fcmTokenOfAdmin")




//    fun addData(key: String,value:String): Task<Void> {
//        val v= reference.child(key).setValue(value)
//            .addOnCompleteListener {
//                Log.d("TAG","data upload finished")
//            }.addOnFailureListener {
//                Log.d("TAG","realtime data upload failed: ${it.localizedMessage}")
//            }
//        return v
//    }
//
//    fun getData(key:String):String{
//        val v= reference.child(key)
//            v.addValueEventListener(object: ValueEventListener {
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                val value = snapshot.value
//                Log.d("TAG", "Value is: " + value)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.w("TAG", "Failed to read value.", error.toException())
//            }
//
//        })
//    }
//
//
//    fun addValueEventListener(){
//
//    }
//    fun addChildEventListener(activity:AppCompatActivity){
//        Log.d("TAG","started addchildevenlistener")
//        this.activity=activity
//        reference.addChildEventListener(this)
//    }
//
//    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//        Log.d("TAG",snapshot.value.toString())
//        Log.d("TAG","onChildAdded")
//        Toast.makeText(activity,"rnnniggggg",Toast.LENGTH_SHORT).show()
//        notification(snapshot.value.toString())
//    }
//
//    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onChildRemoved(snapshot: DataSnapshot) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onCancelled(error: DatabaseError) {
//        TODO("Not yet implemented")
//    }
//    fun notification(data:String){
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel("id", "name", NotificationManager.IMPORTANCE_DEFAULT)
//            val manager = getSystemService(activity, NotificationManager::class.java)
//            if (manager != null) {
//                manager.createNotificationChannel(channel)
//            }
//        }
//        val builder=NotificationCompat.Builder(activity,"id")
//            .setContentText("notifcation is:::")
//            .setSmallIcon(android.R.drawable.sym_def_app_icon)
//            .setAutoCancel(true)
//            .setContentText(data)
//        val managerCompat=NotificationManagerCompat.from(activity)
//        if (ActivityCompat.checkSelfPermission(
//                activity,
//                Manifest.permission.POST_NOTIFICATIONS
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//            activity,
//            arrayOf(
//                Manifest.permission.POST_NOTIFICATIONS,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ),
//            333
//        )
//            return
//        }
//        managerCompat.notify(999,builder.build())
//
//    }
}