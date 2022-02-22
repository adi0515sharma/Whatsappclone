package com.sample.whatsappclone.ui.AddingMembers

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sample.whatsappclone.R
import com.sample.whatsappclone.db.chat_table.Chat
import com.sample.whatsappclone.models.Contact
import com.sample.whatsappclone.ui.ChatScreen.ChatAdapter
import com.sample.whatsappclone.ui.ChatScreen.ChatParent
import com.sample.whatsappclone.utils.UserSharedPreferences

class ParentAdapter constructor(var list_of_contact : LinkedHashMap<String,ArrayList<Contact>>, var context : Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    private var sharedPreferences : UserSharedPreferences = UserSharedPreferences(context)

    var contactadapterhashmap : LinkedHashMap<String, MainActivityAdapter> = linkedMapOf()


    init {
        sharedPreferences.setUp()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_parent, parent, false)
        return ParentAdapter.ContactParentView(view) as RecyclerView.ViewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var c_p_v : ParentAdapter.ContactParentView = holder as ContactParentView

        var key : String = ArrayList<String>(list_of_contact.keys).get(position)

        Log.e("key", key)
        var value : ArrayList<Contact> = list_of_contact.get(key) as ArrayList<Contact>
        c_p_v.status.text = key

        var chatAdapter : MainActivityAdapter = MainActivityAdapter(value, context)
        c_p_v.contacts.adapter = chatAdapter
        chatAdapter.notifyDataSetChanged()


        contactadapterhashmap.put(key, chatAdapter)
    }

    override fun getItemCount(): Int {
        Log.e("size", "${list_of_contact.size}")
        return list_of_contact.size
    }

    class ContactParentView constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var contacts : RecyclerView
        lateinit var status : TextView

        init {
            status = itemView.findViewById(R.id.date)
            contacts = itemView.findViewById(R.id.chats)
        }


    }

}