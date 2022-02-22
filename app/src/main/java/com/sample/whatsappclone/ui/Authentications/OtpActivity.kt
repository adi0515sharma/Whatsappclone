package com.sample.whatsappclone.ui.Authentications

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.sample.whatsappclone.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class OtpActivity : AppCompatActivity() {

    lateinit var phone : String
    lateinit var phone_txtView : TextView
    lateinit var otp_1 : EditText
    lateinit var otp_2 : EditText
    lateinit var otp_3 : EditText
    lateinit var otp_4 : EditText
    lateinit var otp_5 : EditText
    lateinit var otp_6 : EditText
    lateinit var next : LinearLayout
    lateinit var resend_otp : TextView
    val PERMISSIONS_REQUEST_READ_CONTACTS = 100


    lateinit var auth: FirebaseAuth

    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var clock : TextView
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var clock_should_stop : Boolean = false
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        loadContacts()
        if(supportActionBar!=null)
            this.supportActionBar?.hide();
        auth = FirebaseAuth.getInstance()

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            }

            override fun onVerificationFailed(e: FirebaseException) {
                clock_should_stop = true
                e.printStackTrace()
            }
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                Log.d("TAG","onCodeSent:$verificationId")
                Log.d("TAG","onCodeSent:$token")

                storedVerificationId = verificationId
                resendToken = token
                clock_should_stop = true


//                var intent = Intent(applicationContext,Verify::class.java)
//                intent.putExtra("storedVerificationId",storedVerificationId)
//                startActivity(intent)
            }
        }

        phone_txtView = findViewById(R.id.phone)
        otp_1 = findViewById(R.id.otp_1)
        otp_1.doAfterTextChanged {
            if(otp_1.text.isNotEmpty()){
                otp_2.focusable = View.FOCUSABLE
            }
        }
        otp_2 = findViewById(R.id.otp_2)
        otp_2.doAfterTextChanged {
            if(otp_2.text.isNotEmpty()){
                otp_3.focusable = View.FOCUSABLE
            }
            else{
                otp_1.focusable = View.FOCUSABLE
            }
        }
        otp_3 = findViewById(R.id.otp_3)
        otp_3.doAfterTextChanged {
            if(otp_3.text.isNotEmpty()){
                otp_4.focusable = View.FOCUSABLE
            }
            else{
                otp_2.focusable = View.FOCUSABLE
            }
        }
        otp_4 = findViewById(R.id.otp_4)
        otp_4.doAfterTextChanged {
            if(otp_4.text.isNotEmpty()){
                otp_5.focusable = View.FOCUSABLE
            }
            else{
                otp_3.focusable = View.FOCUSABLE
            }
        }
        otp_5 = findViewById(R.id.otp_5)
        otp_5.doAfterTextChanged {
            if(otp_5.text.isNotEmpty()){
                otp_6.focusable = View.FOCUSABLE
            }
            else{
                otp_4.focusable = View.FOCUSABLE
            }
        }
        otp_6 = findViewById(R.id.otp_6)
        otp_6.doAfterTextChanged {
            if(otp_6.text.isNotEmpty()){

            }
            else{
                otp_5.focusable = View.FOCUSABLE
            }
        }
        next = findViewById(R.id.next)
        resend_otp = findViewById(R.id.resend_otp)

        clock = findViewById(R.id.clock)
        phone = intent.getStringExtra("phone").toString()

        phone_txtView.setText("Verifying "+phone)


        next.setOnClickListener {
            var otp : String = otp_1.text.toString() + otp_2.text.toString() + otp_3.text.toString() + otp_4.text.toString() + otp_5.text.toString() + otp_6.text.toString()
            if(otp.length==6){
                verifyUser(otp)
            }
        }

        resend_otp.setOnClickListener {
            sendOtp()
            counter()

        }

        sendOtp()
        counter()
    }



    fun counter(){


            object : CountDownTimer(1000 * 60, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val minutes = (millisUntilFinished / (1000 * 60) % 60).toInt()
                    val seconds = (millisUntilFinished / 1000).toInt() % 60


                    clock.setText("please wait for $seconds seconds")


                    if(clock_should_stop){
                        resend_otp.setTextColor(Color.BLUE)
                        clock_should_stop = false
                        cancel()
                    }
                }

                override fun onFinish() {

                    resend_otp.setTextColor(Color.BLUE)

                }
            }.start()


    }
    fun sendOtp(){
        Log.e("num","+91"+phone)
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91"+phone) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyUser(otp: String){

        val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
            storedVerificationId.toString(), otp)

        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this,"Sucessfull",Toast.LENGTH_SHORT).show()
                    var intent = Intent(this, UserProfile::class.java)
                    intent.putExtra("phone", "+91${phone}")
                    startActivity(intent)
                }
                else{
                    if (it.exception is FirebaseAuthInvalidCredentialsException) {
// The verification code entered was invalid
                        Toast.makeText(this,"Invalid OTP",Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun loadContacts(k : Int = 0) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(arrayOf(
                Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE,
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