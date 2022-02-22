package com.sample.whatsappclone.ui.ChatScreen

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
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.bumptech.glide.Glide
import com.sample.whatsappclone.R
import com.sample.whatsappclone.api.send_message
import com.sample.whatsappclone.db.WhatsappDataBase
import com.sample.whatsappclone.db.chat_table.Chat
import com.sample.whatsappclone.db.chat_table.ChatDBRepo
import com.sample.whatsappclone.db.user_details.UserDetails
import com.sample.whatsappclone.db.user_details.UserDetailsRepo
import com.sample.whatsappclone.db.user_profile.UserProfile
import com.sample.whatsappclone.db.user_profile.UserProfileRepo
import com.sample.whatsappclone.models.MediaResponse.HomePageResponses
import com.sample.whatsappclone.utils.RetrofitObjectInstance
import com.sample.whatsappclone.utils.UserSharedPreferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ChatRoom : AppCompatActivity() {

    lateinit var recycleview : RecyclerView
    lateinit var recyclerViewAdapter : ChatParent

    lateinit var send : ImageView
    lateinit var et_text : EditText

    lateinit var type_of_user : String
    lateinit var name : String
    lateinit var u_id_no : String
    lateinit var photo : String
    lateinit var number : String

    lateinit var retrofit : Retrofit
    lateinit var s_m : send_message

    lateinit var chatRepo : ChatDBRepo

    lateinit var whatsappDataBase: WhatsappDataBase
    lateinit var share : UserSharedPreferences

    var first_time : Int = 0

    lateinit var back : ImageView
    lateinit var profile_image : ImageView
    lateinit var profile_name : TextView
    val PERMISSIONS_REQUEST_READ_CONTACTS = 100

    private var hashMapForChatGrouping : LinkedHashMap<String, ArrayList<Chat>> = linkedMapOf()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        if(supportActionBar!=null)
            this.supportActionBar?.hide();
        share = UserSharedPreferences(this)
        share.setUp()

        whatsappDataBase = WhatsappDataBase.getDatabase(this)!!
        chatRepo = ChatDBRepo(whatsappDataBase.getChatDBDao())
        retrofit = RetrofitObjectInstance.getInstance()
        s_m = retrofit.create(send_message::class.java)

        type_of_user = intent.getStringExtra("user_type").toString()
        name = intent.getStringExtra("name").toString()
        u_id_no = "${intent.getIntExtra("u_id_no",0)}"
        photo = intent.getStringExtra("photo").toString()

        number = intent.getStringExtra("number").toString()



        profile_name = findViewById(R.id.profile_name)
        profile_image = findViewById(R.id.profile_image)
        back = findViewById(R.id.back)



        loadUser(name, photo)


        recyclerViewAdapter = ChatParent(linkedMapOf(), ChatRoom@this)

        recycleview = findViewById(R.id.recyclerView)
        recycleview.adapter = recyclerViewAdapter

        send = findViewById(R.id.send)
        et_text = findViewById(R.id.et_text)

        send.setOnClickListener {
            if(et_text.text.toString().isNotEmpty() and et_text.text.toString().isNotBlank()){
                if(loadContacts()){
                    var t_date : Long = System.currentTimeMillis()
                    sendMessage(t_date.toString())

                    send_message_to_server(t_date)
                }
            }
        }



    }


    fun loadUser(name:String, url:String){

        profile_name.text = name
        val generator: ColorGenerator = ColorGenerator.MATERIAL // or use DEFAULT
        val firstLetter : String = "${name[0]}"
        val color: Int = generator.getColor(name)
        val drawable: TextDrawable = TextDrawable.builder().buildRound(firstLetter, color)

        Glide.with(ChatRoom@this)
            .load(url)
            .placeholder(drawable)
            .into(profile_image)


    }
    fun liveChat(){
        chatRepo.fetchUpdatedChats(Integer.parseInt(u_id_no))
        chatRepo.returnFetchUpdatedRecords().observe(this,  Observer {
           // recyclerViewAdapter.list_of_chat = it as ArrayList<Chat>


            hashMapForChatGrouping  = linkedMapOf()
            it.forEach {
                var time : String = convertLongToTime(it.message_time)
                if(time.split(" ")[0] in hashMapForChatGrouping){
                    var v : ArrayList<Chat>? = hashMapForChatGrouping.get(time.split(" ")[0])
                    v?.add(it)
                    hashMapForChatGrouping.put(time.split(" ")[0], v as ArrayList<Chat>)
                }
                else{
                    var v = arrayListOf<Chat>()
                    v.add(it)

                    hashMapForChatGrouping.put(time.split(" ")[0], v)
                }
            }

            recyclerViewAdapter.list_of_chat = hashMapForChatGrouping

            recyclerViewAdapter.notifyDataSetChanged()
            recycleview.scrollTo(0,0)

//            if(first_time == 0){
//                recycleview.scrollToPosition(recyclerViewAdapter.list_of_chat.size-1)
//                first_time+=1
//            }
        })

        chatRepo.updateSeen(Integer.parseInt(u_id_no))


        //
    }

    fun send_message_to_server(t_date : Long){
        var retrofit : Retrofit = RetrofitObjectInstance.getInstance()
        var s_m : send_message = retrofit.create(send_message::class.java)



        var date : Long = t_date

        s_m.send_message_single(
            Integer.parseInt(share.sharedPreference.getString(UserSharedPreferences.USER_ID,"0")),
            Integer.parseInt(u_id_no),
            et_text.text.toString(),
            date.toString()
        )

            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                // future update for updating message status
            }
            .subscribe()
    }

    fun sendMessage(chat_time : String){

            var from_name : String? = share.sharedPreference.getString(UserSharedPreferences.USER_NAME,"Unknown")
            var from_u_id_no : String? = share.sharedPreference.getString(
                UserSharedPreferences.USER_ID,"0")
            var from_u_mobile : String? = share.sharedPreference.getString(
                UserSharedPreferences.PHONE_NUMBER,"")

                    if (type_of_user.equals("new")) {
                        // add to user details table
                        var user_detail_repo : UserDetailsRepo = UserDetailsRepo(whatsappDataBase.getUserDetailsDBDao())
                        val u_d : UserDetails = UserDetails()
                        u_d.u_id_no = Integer.parseInt(u_id_no)
                        u_d.name = name.toString()
                        u_d.photo = photo.toString()
                        u_d.number = number
                        user_detail_repo.checkUserExists(Integer.parseInt(u_id_no), u_d)
                    }



                    // add chat to chat table
                    var user_profile_repo : UserProfileRepo = UserProfileRepo(whatsappDataBase.getUserProfileDBDao())
                    var user_profile : UserProfile = UserProfile()
                    user_profile.u_id_no = Integer.parseInt(u_id_no)
                    user_profile.last_message = et_text.text.toString()
                    user_profile.unseen_message = "0"

                    user_profile.last_message_time = chat_time.toLong()

                    user_profile_repo.deleteUserProfile(Integer.parseInt(u_id_no), user_profile)


                    var chat_table : ChatDBRepo = ChatDBRepo(whatsappDataBase.getChatDBDao())
                    var chat : Chat = Chat()
                    chat.message = et_text.text.toString()
                    chat.from_u_id_no = Integer.parseInt(from_u_id_no)
                    chat.from_u_mobile = from_u_mobile.toString()
                    chat.from_u_name = from_name.toString()

                    chat.message_time = chat_time.toLong()
                    chat.message_seen = "yes"
                    chat.to_u_id_no = Integer.parseInt(u_id_no);
                    chat.to_u_mobile = number.toString();
                    chat.to_u_name = name.toString();
                    chat_table.insertChat(chat)

    }


    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun loadContacts() : Boolean{

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
                            this@ChatRoom.finish()
                        }
                    })
                .setNegativeButton("back", null)
                .show()


        }
        else{
            liveChat()
            return true
        }
        return false
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        loadContacts()
    }
}