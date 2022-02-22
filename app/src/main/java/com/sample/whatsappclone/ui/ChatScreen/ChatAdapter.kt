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
import com.sample.whatsappclone.utils.DateConverter
import com.sample.whatsappclone.utils.UserSharedPreferences
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter  constructor(var list_of_chat : ArrayList<Chat>, var context : Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var sharedPreferences : UserSharedPreferences = UserSharedPreferences(context)

    init {
        sharedPreferences.setUp()
    }
    class me_chat_item constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var text : TextView
        lateinit var time : TextView
        lateinit var date : TextView

        init {
            text = itemView.findViewById(R.id.text)
            time = itemView.findViewById(R.id.time)
            date = itemView.findViewById(R.id.date)
        }
    }


    class other_chat_item constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var text : TextView
        lateinit var time : TextView
        lateinit var date : TextView

        init {
            text = itemView.findViewById(R.id.text)
            time = itemView.findViewById(R.id.time)
            date = itemView.findViewById(R.id.date)
        }
    }


    override fun getItemViewType(position: Int): Int {
        if(list_of_chat.get(position).from_u_id_no == Integer.parseInt(sharedPreferences.sharedPreference.getString(UserSharedPreferences.USER_ID,"0"))){
            return 1
        }
        else{
            return 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view_holder : RecyclerView.ViewHolder? = null
        when(viewType){
            1->{
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.my_chat_item, parent, false)
                view_holder = me_chat_item(view)
            }
            2->{
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.other_chat_item, parent, false)
                view_holder = other_chat_item(view)
            }
        }
        return view_holder as RecyclerView.ViewHolder
    }


    override fun getItemCount(): Int {
        return list_of_chat.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        var time : String = convertLongToTime(list_of_chat.get(position).message_time)
        when(holder.getItemViewType()){
            1->{
                var m_c_i : me_chat_item = holder as me_chat_item
                m_c_i.text.text = list_of_chat.get(position).message.toString()
                m_c_i.time.text = time.split(" ")[1]
                m_c_i.date.text = time.split(" ")[0].replace(".","/")
            }
            2->{
                var o_c_i : other_chat_item = holder as other_chat_item
                o_c_i.text.text = list_of_chat.get(position).message.toString()
                o_c_i.time.text = time.split(" ")[1]
                o_c_i.date.text = time.split(" ")[0].replace(".","/")
            }
        }
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }


}