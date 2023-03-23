package com.example.woodpeakeradmin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.woodpeakeradmin.databinding.*
import com.example.woodpeakeradmin.models.Product
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.timerTask
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import com.example.woodpeakeradmin.Daos.ProductDao
import com.example.woodpeakeradmin.Daos.StorageDao
import com.example.woodpeakeradmin.models.Addon
import id.zelory.compressor.loadBitmap


class NewAd : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isReadPermissionGranted = false
    var model3dlink=""
    var model3dname=""
    lateinit var binding:ActivityNewAdBinding
    var featureArray=ArrayList<CustomviewFeaturesBinding>()
    var imageViewTable:Hashtable<Int,CustomviewImageBinding> = Hashtable<Int,CustomviewImageBinding>()
    var productShape=""
    var orderPublished=false
    var addonTable=Hashtable<String,CustomviewAddonBinding>()
    lateinit var currentImageLayout:LinearLayout

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityNewAdBinding.inflate(layoutInflater)
        setContentView(binding.root)

         permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->
             isReadPermissionGranted = permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
         }
         currentImageLayout=binding.imageLayoutRed
        binding.addFeature.setOnClickListener(View.OnClickListener { addFeature() })
        binding.addImage.setOnClickListener(View.OnClickListener { addImage(currentImageLayout,"999") })
        binding.addAddon.setOnClickListener(View.OnClickListener { addAddon() })
        val list= listOf<String>("Island shape kitchen","I shape kitchen","U shape kitchen","L shape kitchen")
        val shapeAdapter:ArrayAdapter<*>
        shapeAdapter= ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list)
        binding.kitchenShapeSpinner.adapter=shapeAdapter
        binding.kitchenShapeSpinner.onItemSelectedListener=this

        binding.colBlack.setOnClickListener(View.OnClickListener { colorBtnPress("Black")})
        binding.colBlue.setOnClickListener(View.OnClickListener { colorBtnPress("Blue")})
        binding.colRed.setOnClickListener(View.OnClickListener { colorBtnPress("Red")})
        binding.colWhite.setOnClickListener(View.OnClickListener { colorBtnPress("White")})
        binding.colGreen.setOnClickListener(View.OnClickListener { colorBtnPress("Green")})
        binding.colYellow.setOnClickListener(View.OnClickListener { colorBtnPress("Yellow")})
        binding.add3dModel.setOnClickListener(View.OnClickListener {model3dPick()  })
        binding.publish.setOnClickListener(View.OnClickListener {
            uploadData()
        })
    }

    fun deleteImagesFromCloud(link:String){
        if(!orderPublished) {
            if (link.isNotBlank()) {
                StorageDao.deleteProductImage(link).addOnFailureListener {
                    Log.d("TAG", "Delete failed:${it.localizedMessage}")
                }
            } else {
                for (f in imageViewTable) {
                    StorageDao.deleteProductImage(f.value.storeName.text.toString())
                        .addOnFailureListener {
                            Log.d("TAG", "Delete failed:${it.localizedMessage}")
                        }
                }
            }
        }
    }
    fun delete3dModelFromCloud(){
        if(!orderPublished) {

            if (model3dname.isNotBlank()) {
                StorageDao.deleteProductModel(model3dname).addOnFailureListener {
                    Log.d("TAG", "Delete failed:${it.localizedMessage}")
                }
            }
        }
    }

    fun uploadData(){
        val product=Product()
        product.title=binding.productName.text.toString()
        product.price=binding.productPrice.text.toString()
        product.shape=productShape
        for(f in featureArray){
            product.features.add(f.features.text.toString())
        }
        product.description=binding.productDescription.text.toString()

        for((a,f) in imageViewTable) {
            if(f.storeAddon.text.toString()=="999") {
                if (f.storeColorId.text.toString() == binding.imageLayoutBlue.id.toString()) {
                    product.images.blueLink.add(f.storeLink.text.toString())
                    product.images.blueName.add(f.storeName.text.toString())
                } else if (f.storeColorId.text.toString() == binding.imageLayoutRed.id.toString()) {
                    product.images.redName.add(f.storeName.text.toString())
                    product.images.redLink.add(f.storeLink.text.toString())
                } else if (f.storeColorId.text.toString() == binding.imageLayoutYellow.id.toString()) {
                    product.images.yellowName.add(f.storeName.text.toString())
                    product.images.yellowLink.add(f.storeLink.text.toString())
                } else if (f.storeColorId.text.toString() == binding.imageLayoutBlack.id.toString()) {
                    product.images.blackName.add(f.storeName.text.toString())
                    product.images.blackLink.add(f.storeLink.text.toString())
                } else if (f.storeColorId.text.toString() == binding.imageLayoutWhite.id.toString()) {
                    product.images.whiteName.add(f.storeName.text.toString())
                    product.images.whiteLink.add(f.storeLink.text.toString())
                } else if (f.storeColorId.text.toString() == binding.imageLayoutGreen.id.toString()) {
                    product.images.greenName.add(f.storeName.text.toString())
                    product.images.greenLink.add(f.storeLink.text.toString())
                }
            }else{
                val g=addonTable[f.storeAddon.text.toString()]
                g?.storeLink?.text=f.storeLink.text.toString()
                g?.storeName?.text=f.storeName.text.toString()
                addonTable.replace(f.storeAddon.text.toString(),g)
            }
        }
        for(f in addonTable){
            var addon=Addon()
            addon.name=f.value.addonName.text.toString()
            addon.price=f.value.addonPrice.text.toString()
            addon.imageLink=f.value.storeLink.text.toString()
            addon.imageName=f.value.storeName.text.toString()
            addon.quantity="0"
            product.addons.add(addon)
        }
        product.model3dname=this.model3dname
        product.model3dlink=this.model3dlink
        Log.d("TAG","this.model::"+this.model3dlink)
        Log.d("TAG","prodcuct.model::"+product.model3dlink)
        ProductDao.addProduct(product).addOnSuccessListener { Log.d("TAG","productUpload success"); Toast.makeText(this,"sucess", Toast.LENGTH_SHORT).show()
//                        startActivity(Intent(this,MainActivity::class.java))
//                        finish()
        }.addOnFailureListener { Log.d("TAG","productUpload failed:${it.localizedMessage}");Toast.makeText(this,"failed! retry later",
                Toast.LENGTH_SHORT).show()}
        orderPublished=true
    }
    fun colorBtnPress(col:String){
        when(col){
            "Red" ->{
                currentImageLayout=binding.imageLayoutRed
                hideAllLayoutsExcept(currentImageLayout)
            }
            "Green" ->{
                currentImageLayout=binding.imageLayoutGreen
                hideAllLayoutsExcept(currentImageLayout)
            }
            "Blue" ->{
               currentImageLayout=binding.imageLayoutBlue
                hideAllLayoutsExcept(currentImageLayout)
            }
            "White" ->{
                currentImageLayout=binding.imageLayoutWhite
                hideAllLayoutsExcept(currentImageLayout)

            }
            "Black" ->{
                currentImageLayout=binding.imageLayoutBlack
                hideAllLayoutsExcept(currentImageLayout)
            }
            "Yellow" ->{
                currentImageLayout=binding.imageLayoutYellow
                hideAllLayoutsExcept(currentImageLayout)
            }

        }

    }
    fun hideAllLayoutsExcept(exception:LinearLayout){
        binding.imageLayoutRed.visibility=View.GONE
        binding.imageLayoutGreen.visibility=View.GONE
        binding.imageLayoutBlue.visibility=View.GONE
        binding.imageLayoutWhite.visibility=View.GONE
        binding.imageLayoutYellow.visibility=View.GONE
        binding.imageLayoutBlack.visibility=View.GONE
        exception.visibility=View.VISIBLE

    }

    fun addAddon(){
        val addonBinding = CustomviewAddonBinding.inflate(layoutInflater)
        binding.addonLayout.addView(addonBinding.root)
        addonTable.put(addonBinding.hashCode().toString(),addonBinding)
        addonBinding.addImage.setOnClickListener(View.OnClickListener {
            if(addonBinding.imageLayoutInAddon.isEmpty())
                addImage(addonBinding.imageLayoutInAddon,addonBinding.hashCode().toString())
        })
        addonBinding.cancel.setOnClickListener(View.OnClickListener {
            addonTable.remove(addonBinding.hashCode().toString())
            binding.addonLayout.removeView(addonBinding.root)
        })
    }
    private fun addFeature() {
        val featuresBinding=CustomviewFeaturesBinding.inflate(layoutInflater)
        featureArray.add(featuresBinding)
        featuresBinding.cancel.setOnClickListener(View.OnClickListener {
            featureArray.remove(featuresBinding)
            binding.featuresLayout.removeView(featuresBinding.root)
        })
        binding.featuresLayout.addView(featuresBinding.root)
    }
    private fun progressBarFunc(viewBinding: CustomviewImageBinding){
        var counter=0
        var timer=Timer()
        var timertask= timerTask {
            run(){
                super.runOnUiThread(Runnable {
                    viewBinding.progressBar.visibility=View.VISIBLE
                    counter++;
                    viewBinding.progressBar.progress = counter
                    if(counter==100){
                        timer.cancel()
                        viewBinding.progressBar.visibility=View.INVISIBLE
                    }
                })
            }
        }
        timer.schedule(timertask,0,15)
    }
    private fun progressBarFuncFor3dModel(){
        binding.model3dProgressBar.max=300
        var counter=0
        var timer=Timer()
        var timertask= timerTask {
            run(){
                super.runOnUiThread(Runnable {
                    binding.model3dProgressBar.visibility=View.VISIBLE
                    counter++;
                    binding.model3dProgressBar.progress = counter
                    if(counter==300){
                        timer.cancel()
                        binding.model3dProgressBar.visibility=View.INVISIBLE
                        binding.model3dText.text="upload finished"
                    }
                })
            }
        }
        timer.schedule(timertask,0,15)
    }
    private fun addImage(imageLayout: LinearLayout, addonHash: String) {
        val imageBinding=CustomviewImageBinding.inflate(layoutInflater)
        imageBinding.storeAddon.text=addonHash
        imageBinding.delete.setOnClickListener(View.OnClickListener {
            imageViewTable.remove(imageBinding.hashCode())
            imageLayout.removeView(imageBinding.root)
            deleteImagesFromCloud(imageBinding.storeName.text.toString())
        })
        imageBinding.retry.setOnClickListener(View.OnClickListener {
            val imageUri = Uri.parse(imageBinding.storeUri.text.toString())
            CoroutineScope(Default).launch { uploadImage(imageBinding, imageUri) }
            imageBinding.retry.visibility = View.INVISIBLE
            binding.retryToast.visibility = View.INVISIBLE
            imageBinding.imageview.setImageURI(imageUri) })

        imageBinding.insert.setOnClickListener(View.OnClickListener {
            photoPick(imageBinding.hashCode())
            imageViewTable.put(imageBinding.hashCode(),imageBinding)
            Log.d("TAG","imageView: $imageBinding")
        })
        imageLayout.addView(imageBinding.root)
        imageBinding.storeColorId.setText(imageLayout.id.toString())
    }
    fun photoPick(requestCode: Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode)
    }
    fun model3dPick() {
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select 3D model (.glb) file"), 3)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            Log.d("TAG","onActivityResult: Failed")
            return
        }
        else if(requestCode==3){
            val model3dUri=data!!.data
            upload3dModel(model3dUri!!)
            Log.d("TAG","onActivityResult 3dmodel received")
        }else{
            var imageUri=data!!.data
            Log.d("TAG","onActivityResult Image received")
            val imageBinding= imageViewTable[requestCode]
//            imageBinding?.imageview?.setImageURI(imageUri)
            imageBinding?.storeUri?.text = imageUri.toString()
            imageBinding?.insert?.visibility=View.GONE
            CoroutineScope(Default).launch {
                uploadImage(imageBinding!!,imageUri!!)
            }
            return
        }

    }
    suspend fun uploadImage(viewBinding:CustomviewImageBinding,imageUri: Uri){
        progressBarFunc(viewBinding)
//        val directory= Environment.getExternalStorageDirectory().absolutePath
//        val imageFile= File("${directory}${imageUri.path?.replace("/document/primary:","/",true)}")
        val imageFile= File(RealPathUtil.getRealPath(this, imageUri))
        val compressedImage = Compressor.compress(this, imageFile){
            default(720,1280, Bitmap.CompressFormat.JPEG,60)
        }
        Log.d("TAG","orginalSize: ${imageFile.length()} compressedSize:${compressedImage.length()}")
        val fileName = imageUri.hashCode().toString()
        StorageDao.uploadProductImage(compressedImage.toUri(), fileName)!!.addOnSuccessListener {
            Log.d("TAG","upload success")
            viewBinding.imageview.setImageURI(imageUri)
            StorageDao.getImageUrlOfProduct(fileName)!!.addOnSuccessListener {
                val imageLink=it.toString()
                viewBinding.storeLink.setText(imageLink)
                viewBinding.storeName.setText(fileName)
                Log.d("TAG","imageView: $viewBinding")
                Log.d("TAG","getting Url success ${imageLink}")
            }
        }.addOnFailureListener {
            Log.d("TAG","uploadImage onFailure: ${it.localizedMessage}")
            viewBinding.retry.visibility=View.VISIBLE
            viewBinding.imageview.setImageResource(R.drawable.logo_retry_arrow)
            binding.retryToast.visibility=View.VISIBLE
//            viewBinding.imagesViewImage.setImageResource(R.drawable.grey)
        }
    }

    fun upload3dModel(uri: Uri){
        progressBarFuncFor3dModel()
//        val modelFile= File(RealPathUtil.getRealPath(this, uri))
          val fileName = uri.hashCode().toString()+".glb"
          StorageDao.upload3dModel(uri, fileName)!!.addOnSuccessListener {
            Log.d("TAG","upload success")
            StorageDao.get3dModelUrl(fileName)!!.addOnSuccessListener {
                val link=it.toString()
                Log.d("TAG","getting Url success ${link}")
                model3dlink=link
                model3dname=fileName
            }
        }.addOnFailureListener {
            Log.d("TAG","upload 3d model onFailure: ${it.localizedMessage}")
              binding.model3dText.text="failed!try again"
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        productShape=p0?.selectedItem.toString()

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(!orderPublished) {
            deleteImagesFromCloud("")
            delete3dModelFromCloud()
        }
    }




}