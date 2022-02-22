package com.sample.whatsappclone.ui.Authentications

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.sample.whatsappclone.R
import com.sample.whatsappclone.ui.Home.Home
import com.sample.whatsappclone.utils.UserSharedPreferences
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var next : LinearLayout
    lateinit var select_country : EditText
    lateinit var country_code : EditText
    lateinit var phone_number : EditText
    val PERMISSIONS_REQUEST_READ_CONTACTS = 100



    override fun onStart() {
        super.onStart()

        var auth = FirebaseAuth.getInstance().currentUser
        if(auth != null){
            var sharedPreferences : UserSharedPreferences = UserSharedPreferences(UserProfile@this)
            sharedPreferences.setUp()

            if(sharedPreferences.user_register_or_not()){
                var intent = Intent(this, Home::class.java)
                startActivity(intent)
            }
            else{
                var intent = Intent(this, UserProfile::class.java)
                intent.putExtra("phone", auth.phoneNumber)
                startActivity(intent)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadContacts()
        if(supportActionBar!=null)
            this.supportActionBar?.hide();
        next = findViewById<LinearLayout>(R.id.next)
//        select_country = findViewById(R.id.select_country)
//        country_code = findViewById(R.id.country_code)
        phone_number = findViewById(R.id.phone_number)
        add_listner()
    }



    fun add_listner(){
        next.setOnClickListener {

            if(phone_number.text.toString().isEmpty()){
                phone_number.setError("Please enter phone number")
                return@setOnClickListener
            }

            showDialog(phone_number.text.toString())
        }
    }
    private fun showDialog(phone: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.popup_verification)

        val edit = dialog.findViewById(R.id.edit) as TextView
        val ok = dialog.findViewById(R.id.ok) as TextView
        val number = dialog.findViewById(R.id.phone_number) as TextView
        number.setText(phone)
        edit.setOnClickListener {
            dialog.dismiss()
        }
        ok.setOnClickListener {
            val intent = Intent(this, OtpActivity::class.java).apply {
                putExtra("phone", phone)
            }
            dialog.dismiss()
            startActivity(intent)
        }
        dialog.show()

    }

    private fun loadContacts() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA), PERMISSIONS_REQUEST_READ_CONTACTS)

        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            } else {
                //  toast("Permission must be granted in order to display contacts information")
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()

            } else {
                //  toast("Permission must be granted in order to display contacts information")
            }
            if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()

            } else {
                //  toast("Permission must be granted in order to display contacts information")
            }
            if (grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()

            } else {
                //  toast("Permission must be granted in order to display contacts information")
            }

        }
    }



}