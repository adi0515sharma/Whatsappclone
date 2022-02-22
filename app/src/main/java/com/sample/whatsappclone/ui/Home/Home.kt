package com.sample.whatsappclone.ui.Home

//import com.sample.whatsappclone.UpdatingUsers

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
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sample.whatsappclone.R
import com.sample.whatsappclone.db.WhatsappDataBase
import com.sample.whatsappclone.db.chat_table.ChatDBRepo
import com.sample.whatsappclone.db.user_details.UserDetailsRepo
import com.sample.whatsappclone.models.MediaResponse.HomePageResponses
import com.sample.whatsappclone.notification.NotificatonChannelClass
import com.sample.whatsappclone.ui.AddingMembers.MainActivity
import com.sample.whatsappclone.ui.ChatScreen.ChatRoom
import com.sample.whatsappclone.worker.UpdatingUserDetailsWorker
import com.sample.whatsappclone.worker.UpdatingUserProfileWorker


class Home : AppCompatActivity() {

    lateinit var database : WhatsappDataBase
    lateinit var recycleview : RecyclerView
    lateinit var adding_new_member : FloatingActionButton

    lateinit var chatRepo : ChatDBRepo
    lateinit var homeUserDetailsRepo : UserDetailsRepo
    lateinit var whatsappDataBase: WhatsappDataBase

    lateinit var homeAdapter: HomeAdapter
    lateinit var inte: Intent
    lateinit var search_icon : ImageView

    lateinit var toolbar_for_search : SearchView
    lateinit var toolbar_for_normal_header : RelativeLayout
    val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    override fun onStart() {
        super.onStart()

    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)


        if(supportActionBar!=null)
            this.supportActionBar?.hide();


        homeAdapter = HomeAdapter(ArrayList(), this)


        adding_new_member = findViewById(R.id.add_new_member)
        recycleview = findViewById(R.id.recycleview)
        recycleview.adapter = homeAdapter

        search_icon = findViewById(R.id.search_icon)
        toolbar_for_search = findViewById<SearchView>(R.id.toolbar_for_search)
        toolbar_for_normal_header = findViewById(R.id.toolbar_for_normal_header)

        homeAdapter.setListner(object : HomeAdapter.OnItemClickListener{
            override fun sendUserToChatScreen(position: Int) {
                send_to_chat(position)
            }
        })

        toolbar_for_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {

                Log.e("text", p0!!)
                homeAdapter.getFilter()?.filter(p0)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                homeAdapter.getFilter()?.filter(p0)
                return true
            }

        })
        toolbar_for_search.setOnCloseListener(object : SearchView.OnCloseListener{
            override fun onClose(): Boolean {
                homeAdapter.getFilter()?.filter("")
                toolbar_for_normal_header.visibility = View.VISIBLE
                toolbar_for_search.visibility = View.GONE

                return true
            }

        })


        adding_new_member.setOnClickListener {
            val i : Intent = Intent(this, MainActivity::class.java)
            startActivity(i)
        }


        search_icon.setOnClickListener {
            toolbar_for_normal_header.visibility = View.GONE
            toolbar_for_search.visibility = View.VISIBLE
        }

    }

    fun init_system(){
        NotificatonChannelClass(Home@this)
        whatsappDataBase = WhatsappDataBase.getDatabase(this) as WhatsappDataBase
        chatRepo = ChatDBRepo(whatsappDataBase.getChatDBDao())
        homeUserDetailsRepo = UserDetailsRepo(whatsappDataBase.getUserDetailsDBDao())
    }

    fun fetchUpdateRecords(){

        homeUserDetailsRepo.fetchForHomeScreenUi()
        homeUserDetailsRepo.fetch_responseForHomeUi().observe(this, Observer {
            homeAdapter.list_of_contact = it as ArrayList<HomePageResponses>
            homeAdapter.copy_list_of_contact = it as ArrayList<HomePageResponses>
            homeAdapter.notifyDataSetChanged()
        })

        chatRepo.fetchNotSeenCount()
        chatRepo.liveSeenCount().observe(this, Observer {
            homeAdapter.updateSeen(it)
        })
    }
    fun send_to_chat(position: Int){
        var intent = Intent(this, ChatRoom::class.java)
        intent.putExtra("name", homeAdapter.list_of_contact.get(position).name)
        intent.putExtra("u_id_no", homeAdapter.list_of_contact.get(position).u_id_no)
        intent.putExtra("photo", homeAdapter.list_of_contact.get(position).photo)
        intent.putExtra("number", homeAdapter.list_of_contact.get(position).number)
        intent.putExtra("user_type","old")
        startActivity(intent)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        ActivityCompat.finishAffinity(this);
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    fun updatingUserDetails(){
        val workManager = WorkManager.getInstance(applicationContext)

        val constraints = Constraints.Builder()  //if you want to add constraints
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val updatingUserDetails = OneTimeWorkRequest
            .Builder(UpdatingUserDetailsWorker::class.java)
            .setConstraints(constraints)
            .build()
        val updatingUserProfiles = OneTimeWorkRequest
            .Builder(UpdatingUserProfileWorker::class.java)
            .setConstraints(constraints)
            .build()
        workManager
            .beginWith(listOf(updatingUserDetails, updatingUserProfiles))
            .enqueue()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun loadContacts() {

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
                            this@Home.finish()
                        }
                    })
                .setNegativeButton("back", null)
                .show()


        }
        else{
            init_system()
            updatingUserDetails()
            fetchUpdateRecords()
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        loadContacts()
    }

}