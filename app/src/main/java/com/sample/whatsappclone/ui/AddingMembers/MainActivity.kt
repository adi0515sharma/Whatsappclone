package com.sample.whatsappclone.ui.AddingMembers

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.sample.whatsappclone.R
import com.sample.whatsappclone.db.WhatsappDataBase
import com.sample.whatsappclone.db.chat_table.Chat
import com.sample.whatsappclone.models.Contact
import com.sample.whatsappclone.ui.AddingMembers.MainActivityAdapter.OnItemClickListener
import com.sample.whatsappclone.ui.Authentications.UserProfile
import com.sample.whatsappclone.ui.ChatScreen.ChatRoom
import retrofit2.http.POST
import java.util.LinkedHashMap


class MainActivity : AppCompatActivity() {

    val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    lateinit var phoneContacts : MainActivityViewModel
    lateinit var adapter : MainActivityAdapter
    lateinit var recycleview : RecyclerView

    lateinit var search_icon : ImageView

    lateinit var toolbar_for_search : SearchView
    lateinit var toolbar_for_normal_header : RelativeLayout
    lateinit var progress : ProgressBar
    private var hashMapForContactGrouping : LinkedHashMap<String, ArrayList<Contact>> = linkedMapOf()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.adding_user)
        if(supportActionBar!=null)
            this.supportActionBar?.hide();

        recycleview = findViewById(R.id.recycleview)
        search_icon = findViewById(R.id.search_icon)
        toolbar_for_search = findViewById<SearchView>(R.id.toolbar_for_search)
        toolbar_for_normal_header = findViewById(R.id.toolbar_for_normal_header)
        progress = findViewById(R.id.progress)

        adapter = MainActivityAdapter(arrayListOf(), MainActivity@this)
        adapter.setListner(object : OnItemClickListener{
            override fun sendUserToChatScreen(position: Int) {
                // send user to chat screen

                send_to_chat(position)

            }

            override fun shareAppToOtherUser(position: Int) {
                // send app link to other user
            }

        })
        phoneContacts = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        phoneContacts.setUp(MainActivity@this)


        recycleview.adapter = adapter
        recycleview.setItemViewCacheSize(50);

        phoneContacts.fetch_records().observe(this, Observer {
            if(it.size>0){


                adapter.list_of_contact = it as ArrayList<Contact>
                adapter.copy_list_of_contact = it as ArrayList<Contact>
                adapter.notifyDataSetChanged()

                progress.visibility = View.GONE
                recycleview.visibility = View.VISIBLE

            }
        })

        if(getConnectivityStatusString(this)){

            loadContacts()
        }
        else{
        }

        toolbar_for_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {

                adapter.getFilter()?.filter(p0)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter.getFilter()?.filter(p0)
                return true
            }

        })
        toolbar_for_search.setOnCloseListener(object : SearchView.OnCloseListener{
            override fun onClose(): Boolean {
                adapter.getFilter()?.filter("")
                toolbar_for_normal_header.visibility = View.VISIBLE
                toolbar_for_search.visibility = View.GONE

                return true
            }

        })



        search_icon.setOnClickListener {
            toolbar_for_normal_header.visibility = View.GONE
            toolbar_for_search.visibility = View.VISIBLE
        }


    }



    fun getConnectivityStatusString(context: Context): Boolean {
        var status: String? = null
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (activeNetwork != null) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                status = "Wifi enabled"
                return true
            } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                status = "Mobile data enabled"
                return true
            }
        } else {
            status = "No internet is available"
            return false
        }
        return false
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
                            this@MainActivity.finish()
                        }
                    })
                .setNegativeButton("back", null)
                .show()


        }
        else{
            phoneContacts.fetchPhoneContacts()
        }
    }



    fun send_to_chat(position: Int){
        var intent = Intent(this, ChatRoom::class.java)
        intent.putExtra("name", adapter.list_of_contact.get(position).name)
        intent.putExtra("u_id_no", adapter.list_of_contact.get(position).u_id_no)
        intent.putExtra("photo", adapter.list_of_contact.get(position).icon)
        intent.putExtra("number", adapter.list_of_contact.get(position).phone_number)

        intent.putExtra("user_type","new")
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        loadContacts()
    }

}