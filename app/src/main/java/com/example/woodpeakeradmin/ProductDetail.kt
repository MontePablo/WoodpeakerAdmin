package com.example.woodpeakeradmin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.isEmpty
import com.bumptech.glide.Glide
import com.example.woodpeakeradmin.Daos.ProductDao
import com.example.woodpeakeradmin.Daos.StorageDao
import com.example.woodpeakeradmin.databinding.ActivityProductDetailBinding
import com.example.woodpeakeradmin.databinding.CustomviewAddonBinding
import com.example.woodpeakeradmin.databinding.CustomviewFeaturesBinding
import com.example.woodpeakeradmin.databinding.CustomviewImageBinding
import com.example.woodpeakeradmin.models.Addon
import com.example.woodpeakeradmin.models.Product
import com.google.gson.Gson
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask

//class ProductDetail : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_product_detail)
//    }
//}
class ProductDetail : AppCompatActivity() , AdapterView.OnItemSelectedListener{
    lateinit var binding:ActivityProductDetailBinding

    lateinit var product: Product
    lateinit var productId:String
    var shouldbeDeletedImages=ArrayList<String>()
    lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    var isReadPermissionGranted = false
    var productShape=""
    var featureArray=ArrayList<CustomviewFeaturesBinding>()
    var imageViewTable: Hashtable<Int, CustomviewImageBinding> = Hashtable<Int,CustomviewImageBinding>()
    var addonTable=Hashtable<String, CustomviewAddonBinding>()
    lateinit var currentImageLayout: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        product = Gson().fromJson(intent.getStringExtra("product"), Product::class.java)
        productId=intent.getStringExtra("productId")!!


        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->
            isReadPermissionGranted = permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
        }
        currentImageLayout=binding.imageLayoutRed
        binding.addFeature.setOnClickListener(View.OnClickListener { addFeature() })
        binding.addImage.setOnClickListener(View.OnClickListener { addImage(currentImageLayout,"999") })
        binding.addAddon.setOnClickListener(View.OnClickListener { addAddon() })
        val list= listOf<String>("Island shape kitchen","I shape kitchen","U shape kitchen","L shape kitchen","others")
        val shapeAdapter: ArrayAdapter<*>
        shapeAdapter= ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list)
        binding.kitchenShapeSpinner.adapter=shapeAdapter
        binding.kitchenShapeSpinner.onItemSelectedListener=this

        binding.colBlack.setOnClickListener(View.OnClickListener { colorBtnPress("Black")})
        binding.colBlue.setOnClickListener(View.OnClickListener { colorBtnPress("Blue")})
        binding.colRed.setOnClickListener(View.OnClickListener { colorBtnPress("Red")})
        binding.colWhite.setOnClickListener(View.OnClickListener { colorBtnPress("White")})
        binding.colGreen.setOnClickListener(View.OnClickListener { colorBtnPress("Green")})
        binding.colYellow.setOnClickListener(View.OnClickListener { colorBtnPress("Yellow")})
        loadData()
        binding.publish.setOnClickListener(View.OnClickListener {
            uploadData()
        })
    }

    fun loadData(){
        binding.productDescription.setText(product.description)
        binding.productName.setText(product.title)
        binding.productPrice.setText(product.price)
        binding.publish.text="update"
        var i=0
        while(i< product.images.redLink.size){
            scndAddImage(binding.imageLayoutRed, product.images.redLink[i], product.images.redName[i])
            i++
        }
        i=0
        while(i< product.images.whiteLink.size){
            scndAddImage(binding.imageLayoutWhite, product.images.whiteLink[i], product.images.whiteName[i])
            i++
        }
        i=0
        while(i < product.images.blackLink.size){
            scndAddImage(binding.imageLayoutBlack, product.images.blackLink[i], product.images.blackName[i])
            i++
        }
        i=0
        while(i < product.images.blueLink.size){
            scndAddImage(binding.imageLayoutBlue, product.images.blueLink[i], product.images.blueName[i])
            i++
        }
        i=0
        while(i < product.images.yellowLink.size){
            scndAddImage(binding.imageLayoutYellow, product.images.yellowLink[i], product.images.yellowName[i])
            i++
        }
        i=0
        while(i < product.images.greenLink.size){
            scndAddImage(binding.imageLayoutGreen, product.images.greenLink[i], product.images.greenName[i])
            i++
        }
        for(f in product.features)
            addFeature2(f)
        for(f in product.addons){
            addAddon2(f)
        }
    }
    private fun scndAddImage(imageLayout: LinearLayout, s: String, s1: String) {
        val imageBinding=CustomviewImageBinding.inflate(layoutInflater)
        Glide.with(imageBinding.imageview.context).load(s).into(imageBinding.imageview)
        imageBinding.storeLink.text=s
        imageBinding.storeName.text=s1
        imageBinding.delete.setOnClickListener(View.OnClickListener {
            shouldbeDeletedImages.add(s1)
            imageLayout.removeView(imageBinding.root)
        })
        imageLayout.addView(imageBinding.root)

    }
    fun deleteImages(arr:ArrayList<String>){
        for(f in arr){
            StorageDao.deleteProductImage(f)
        }
    }

    fun deleteImagesFromCloud(name:String){
        if(name.isNotBlank()){
            StorageDao.deleteProductImage(name).addOnFailureListener {
                Log.d("TAG", "Delete failed:${it.localizedMessage}")
            }
        }else {
            for (f in imageViewTable) {
                StorageDao.deleteProductImage(f.value.storeName.text.toString())
                    .addOnFailureListener {
                        Log.d("TAG", "Delete failed:${it.localizedMessage}")
                    }
            }
        }
    }

    fun uploadData(){
        product.title=binding.productName.text.toString()
        product.price=binding.productPrice.text.toString()
        product.shape=productShape
        for(f in featureArray){
            product.features.add(f.features.text.toString())
        }
        product.description=binding.productDescription.text.toString()

        for(f in imageViewTable) {
            if(f.value.storeAddon.text.toString()=="999") {
                if (f.value.storeColorId.text.toString() == binding.colBlue.id.toString()) {
                    product.images.blueLink.add(f.value.storeLink.text.toString())
                    product.images.blueName.add(f.value.storeName.text.toString())
                } else if (f.value.storeColorId.text.toString() == binding.colRed.id.toString()) {
                    product.images.redName.add(f.value.storeName.text.toString())
                    product.images.redLink.add(f.value.storeLink.text.toString())
                } else if (f.value.storeColorId.text.toString() == binding.colYellow.id.toString()) {
                    product.images.yellowName.add(f.value.storeName.text.toString())
                    product.images.yellowLink.add(f.value.storeLink.text.toString())
                } else if (f.value.storeColorId.text.toString() == binding.colBlack.id.toString()) {
                    product.images.blackName.add(f.value.storeName.text.toString())
                    product.images.blackLink.add(f.value.storeLink.text.toString())
                } else if (f.value.storeColorId.text.toString() == binding.colWhite.id.toString()) {
                    product.images.whiteName.add(f.value.storeName.text.toString())
                    product.images.whiteLink.add(f.value.storeLink.text.toString())
                } else if (f.value.storeColorId.text.toString() == binding.colGreen.id.toString()) {
                    product.images.greenName.add(f.value.storeName.text.toString())
                    product.images.greenLink.add(f.value.storeLink.text.toString())
                }
            }else{
                val g=addonTable[f.value.storeAddon.text.toString()]
                g?.storeLink?.text=f.value.storeLink.text.toString()
                g?.storeName?.text=f.value.storeName.text.toString()
                addonTable.replace(f.value.storeAddon.text.toString(),g)
            }
        }
        for(f in addonTable){
            var addon= Addon()
            addon.name=f.value.addonName.text.toString()
            addon.price=f.value.addonPrice.text.toString()
            addon.imageLink=f.value.storeLink.text.toString()
            addon.imageName=f.value.storeName.text.toString()
            addon.quantity="0"
            product.addons.add(addon)
        }
        ProductDao.updateProduct(productId,product).addOnSuccessListener { Log.d("TAG","Update success"); Toast.makeText(this,"sucess", Toast.LENGTH_SHORT).show()
//                        startActivity(Intent(this,MainActivity::class.java))
//                        finish()
        }.addOnFailureListener { Log.d("TAG","UPdate failed:${it.localizedMessage}");Toast.makeText(this,"failed! retry later",
            Toast.LENGTH_SHORT).show()}
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
    fun addAddon2(addon: Addon){
        val addonBinding = CustomviewAddonBinding.inflate(layoutInflater)
        addonBinding.addonPrice.setText(addon.price)
        addonBinding.addonName.setText(addon.name)
        val imageBinding=CustomviewImageBinding.inflate(layoutInflater)
        imageBinding.delete.setOnClickListener(View.OnClickListener { shouldbeDeletedImages.add(addon.imageName) })
        addonBinding.imageLayoutInAddon.addView(imageBinding.root)
        Glide.with(imageBinding.imageview.context).load(addon.imageLink).into(imageBinding.imageview)
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
        val featuresBinding= CustomviewFeaturesBinding.inflate(layoutInflater)
        featureArray.add(featuresBinding)
        featuresBinding.cancel.setOnClickListener(View.OnClickListener {
            featureArray.remove(featuresBinding)
            binding.featuresLayout.removeView(featuresBinding.root)
        })
        binding.featuresLayout.addView(featuresBinding.root)
    }
    private fun addFeature2(s:String) {
        val featuresBinding= CustomviewFeaturesBinding.inflate(layoutInflater)
        featureArray.add(featuresBinding)
        featuresBinding.features.setText(s)
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
            CoroutineScope(Dispatchers.Default).launch { uploadImage(imageBinding, imageUri) }
            imageBinding.retry.visibility = View.INVISIBLE
            binding.retryToast.visibility = View.INVISIBLE
            imageBinding.imageview.setImageURI(imageUri) })

        imageBinding.insert.setOnClickListener(View.OnClickListener {
            photoPick(imageBinding.hashCode())
            imageViewTable.put(imageBinding.hashCode(),imageBinding)
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            Log.d("TAG","onActivityResult: Failed")
            return
        }
        else{
            var imageUri=data!!.data
            Log.d("TAG","onActivityResult Image received")
            val imageBinding= imageViewTable[requestCode]
//            imageBinding?.imageview?.setImageURI(imageUri)
            imageBinding?.storeUri?.text = imageUri.toString()
            imageBinding?.insert?.visibility=View.GONE
            CoroutineScope(Dispatchers.Default).launch {
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


    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        productShape=p0?.selectedItem.toString()

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        deleteImagesFromCloud(" ")
        deleteImages(shouldbeDeletedImages)
        finish()
    }


}