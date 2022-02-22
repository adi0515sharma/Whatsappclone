package com.sample.whatsappclone.ui.Authentications

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.sample.whatsappclone.R
import com.sample.whatsappclone.api.Auth
import com.sample.whatsappclone.api.repository.ImageUploadingRepository
import com.sample.whatsappclone.factory.ImageUploadFactory
import com.sample.whatsappclone.ui.Home.Home
import com.sample.whatsappclone.utils.Resource
import com.sample.whatsappclone.utils.RetrofitObjectInstance
import com.sample.whatsappclone.utils.URIPathHelper
import com.sample.whatsappclone.utils.UserSharedPreferences
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class UserProfile : AppCompatActivity() {
    
    lateinit var profile_img : ImageView
    lateinit var profile_name : EditText
    lateinit var submit : LinearLayout
    val PICK_IMAGE : Int = 12
    var PICK_IMAGE_PATH : String? = ""
    lateinit var phone : String
    var flag : Boolean = true
    var fcm_token : String = ""
    val PERMISSIONS_REQUEST_READ_CONTACTS = 100

    private lateinit var sharedPreferences : UserSharedPreferences

    private val mViewModel: UserProfileViewModel by viewModels{
        ImageUploadFactory(
            ImageUploadingRepository(RetrofitObjectInstance.getInstance().create(Auth::class.java))
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        if(supportActionBar!=null)
            this.supportActionBar?.hide();
        sharedPreferences  = UserSharedPreferences(UserProfile@this)
        sharedPreferences.setUp()
        profile_img = findViewById(R.id.profile_img)
        profile_name = findViewById(R.id.profile_name)
        submit = findViewById(R.id.submit)
        if(intent.hasExtra("phone")){
            phone = intent.getStringExtra("phone").toString()
        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            fcm_token = it.toString()
            Log.e("token", fcm_token)
        }
            .addOnFailureListener {
                Log.e("fcm token failed", "${it.message}")
            }
        profile_img.setOnClickListener {
            loadContacts()
        }


        submit.setOnClickListener {



            if(profile_name.text.isEmpty() || profile_name.text.isBlank())
            {
                return@setOnClickListener
            }

            if(!flag){
                uploading_user()
            }
            else{
                uploading_user_details(phone, "https://cdn-icons-png.flaticon.com/512/149/149071.png", profile_name.text.toString(), fcm_token)
            }



        }
        init_observer()
    }
    fun init_observer(){
        mViewModel.image.observe(this@UserProfile){
            when(it){
                is Resource.Failed ->
                    Log.e("failed to upload image", it.message)

                is Resource.Success->{

                    Log.e("path", it.data.path)
                    Log.e("message", it.data.message)
                    Log.e("status", it.data.status)

                    uploading_user_details(phone, it.data.path, profile_name.text.toString(), fcm_token)

                    return@observe
                }

                is Resource.Loading->
                    Log.e("loading to upload image",".......")

            }
        }
        mViewModel.user_upload.observe(this@UserProfile){
            when(it){
                is Resource.Failed ->
                    Log.e("failed to upload user", it.message)

                is Resource.Success->{
                    Log.e("status", it.data.status)
                    Log.e("status", it.data.info.id)
                    Log.e("status", it.data.info.name)
                    Log.e("status", it.data.info.phone_no)
                    Log.e("status", it.data.info.profile_image)
                    val i : Intent = Intent(this, Home::class.java)
                    startActivity(i)

                    // save to preffernce
                    sharedPreferences.register_user_credential(it.data.info.name, it.data.info.id, it.data.info.profile_image, it.data.info.phone_no)
                    goto_home()
                    return@observe
                }

                is Resource.Loading->
                    Log.e("loading to upload user",".......")

            }
        }

    }


    fun goto_home(){

    }
    fun uploading_user(){
        val file: File = File(PICK_IMAGE_PATH);

        var requestBody: RequestBody = RequestBody.create(MediaType.parse("*/*"), file);

        var fileToUpload: MultipartBody.Part =
                            MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        var filename: RequestBody =
            RequestBody.create(MediaType.parse("text/plain"), file.getName());


        mViewModel.uploadUserProfileImage(fileToUpload, filename)
    }

    fun uploading_user_details(number : String, image_path : String, name : String, fcm_token : String){
        mViewModel.uploadUser(name, number, image_path, fcm_token)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun loadContacts(){

        var permissions : MutableList<String> = mutableListOf<String>()
        var PERMISSIONS : ArrayList<String>  = arrayListOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
        PERMISSIONS.forEach {
            if(ActivityCompat.checkSelfPermission(this,it) != PackageManager.PERMISSION_GRANTED){
                permissions.add(it)
                Log.e("permsiion", it)
            }
        }

        if(permissions.isNotEmpty()){
            AlertDialog.Builder(this)
                .setIcon(R.drawable.logo)
                .setTitle("Permission Manger")
                .setMessage("For your privacy concern we save your chats in your own device and we also store your contacts in your device . that's why we want permission for this please click on Setting option ")
                .setPositiveButton(
                    "Setting",
                    DialogInterface.OnClickListener { dialog, which -> //Stop the activity
                        run{
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            val uri: Uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                            this@UserProfile.finish()
                        }
                    })
                .setNegativeButton("back", null)
                .show()


        }
        else{

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }

    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            var imageUri = data?.data!!

            try{
                PICK_IMAGE_PATH = getRealPathFromURI(imageUri)
                profile_img.setImageURI(imageUri)
                flag = false
            }
            catch(e : Exception){

                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun getRealPathFromURI(contentUri: Uri): String? {

        return URIPathHelper.getPath(this, contentUri)
    }
}