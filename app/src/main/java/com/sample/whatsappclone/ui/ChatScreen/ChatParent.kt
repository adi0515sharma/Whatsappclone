package com.sample.whatsappclone.ui.ChatScreen

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sample.whatsappclone.R
import com.sample.whatsappclone.db.chat_table.Chat
import com.sample.whatsappclone.utils.UserSharedPreferences
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class ChatParent constructor(var list_of_chat : LinkedHashMap<String,ArrayList<Chat>>, var context : Context) :

    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var monthString = arrayOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )


    private var sharedPreferences : UserSharedPreferences = UserSharedPreferences(context)

    var childadapterhashmap : LinkedHashMap<String, ChatAdapter> = linkedMapOf()

    init {
        sharedPreferences.setUp()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_parent, parent, false)
        return ChatParentView(view) as RecyclerView.ViewHolder
    }



    override fun getItemCount(): Int {
        Log.e("size", "${list_of_chat.size}")
        return list_of_chat.size
    }
    class ChatParentView constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var chats : RecyclerView
        lateinit var date : TextView

        init {
            date = itemView.findViewById(R.id.date)
            chats = itemView.findViewById(R.id.chats)
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var c_p_v : ChatParentView = holder as ChatParentView

        var key : String = ArrayList<String>(list_of_chat.keys).get(position)

        Log.e("key", key)
        var value : ArrayList<Chat> = list_of_chat.get(key) as ArrayList<Chat>
        c_p_v.date.text = convertLongToTime(key)

        var chatAdapter : ChatAdapter = ChatAdapter(value, context)
        c_p_v.chats.adapter = chatAdapter
        chatAdapter.notifyDataSetChanged()


        childadapterhashmap.put(key, chatAdapter)
    }

    fun convertLongToTime(key: String): String
    {
        var date3 : String = key
        date3 = "${
            monthString[ Integer.parseInt(date3.split(".")[1])-1 ].substring(0,3)
        } ${date3.split(".")[2]}, ${date3.split(".")[0]}"
        return date3
    }
}