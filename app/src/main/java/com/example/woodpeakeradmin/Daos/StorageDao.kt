package com.example.woodpeakeradmin.Daos
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

object StorageDao {
    val productImageRef = FirebaseStorage.getInstance().getReference("productImages")
    val productModelsRef = FirebaseStorage.getInstance().getReference("models")

    fun uploadProductImage(imagePathUri: Uri?, fileName: String?): UploadTask? {
        Log.d("TAG", "storageDao uploadImage start")
        return productImageRef.child(fileName!!).putFile(imagePathUri!!)
    }
    fun upload3dModel(modelPathUri: Uri?, fileName: String?): UploadTask? {
        Log.d("TAG", "storageDao upload3dModel start")
        return productModelsRef.child(fileName!!).putFile(modelPathUri!!)
    }
    fun deleteProductImage(fileName: String?): Task<Void> {
        return productImageRef.child(fileName!!).delete()
    }
    fun deleteProductModel(fileName: String?): Task<Void> {
        return productModelsRef.child(fileName!!).delete()
    }
    fun getImageUrlOfProduct(filename: String?): Task<Uri?>? {
        return productImageRef.child(filename!!).downloadUrl
    }
    fun get3dModelUrl(filename: String?): Task<Uri?>? {
        return productModelsRef.child(filename!!).downloadUrl
    }

}