package com.sample.whatsappclone.ui.Home


import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.bumptech.glide.Glide
import com.sample.whatsappclone.R
import com.sample.whatsappclone.models.MediaResponse.HomePageResponses
import java.text.SimpleDateFormat
import java.time.Month
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class HomeAdapter (var list_of_contact : ArrayList<HomePageResponses>, var context : Context) : RecyclerView.Adapter<HomeAdapter.ViewHolder>()  {

    lateinit var listener: OnItemClickListener
    var not_seens : HashMap<String, String> = HashMap<String, String>()

    var copy_list_of_contact : ArrayList<HomePageResponses> = arrayListOf()
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


    fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString()?.toLowerCase() ?: ""
                if (charString.isEmpty()) list_of_contact = copy_list_of_contact else {
                    val filteredList = ArrayList<HomePageResponses>()
                    copy_list_of_contact
                        .filter {
                            (it.name.toLowerCase().contains(constraint!!)) or
                                    (it.number.contains(constraint))

                        }
                        .forEach { filteredList.add(it) }
                    list_of_contact = filteredList

                }
                return FilterResults().apply { values = list_of_contact }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                list_of_contact = if (results?.values == null)
                    ArrayList()
                else
                    results.values as ArrayList<HomePageResponses>
                notifyDataSetChanged()
            }
        }
    }


    interface OnItemClickListener {
        fun sendUserToChatScreen(position : Int)
    }

    fun setListner(listener: HomeAdapter.OnItemClickListener){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item, parent, false)
        return HomeAdapter.ViewHolder(view, this.listener)
    }


    fun updateSeen(map : HashMap<String, String>){

        not_seens = map
//        list_of_contact.forEach {
//            if(!(it.unseen_message.toString().equals(map.get("${it.u_id_no}").toString()))){
//                var index : Int = list_of_contact.indexOf(it)
//                it.unseen_message = map.get("${it.u_id_no}").toString()
//                Log.e("${it.u_id_no}jhk",it.unseen_message )
//                list_of_contact.remove(it)
//                list_of_contact.add(index, it)
//
//            }
//        }


//        for( i in 0..list_of_contact.size){
//            list_of_contact.get(i).unseen_message = map.get("${list_of_contact.get(i).u_id_no}").toString()
//            list_of_contact.set(i, list_of_contact.get(i))
//        }
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: HomeAdapter.ViewHolder, position: Int) {

        var data_item : HomePageResponses = list_of_contact.get(position)

        if(data_item.photo.isNotEmpty() && data_item.photo.isNotBlank()){


            val generator: ColorGenerator = ColorGenerator.MATERIAL // or use DEFAULT
            val firstLetter : String = "${list_of_contact.get(position).name[0]}"
            val color: Int = generator.getColor(list_of_contact.get(position).name)
            val drawable: TextDrawable = TextDrawable.builder().buildRound(firstLetter, color)

            Glide.with(context)
                .load(list_of_contact.get(position).photo)
                .into(holder.profile_image)
        }
        if(data_item.name.isNotEmpty() && data_item.name.isNotBlank()){


            holder.profile_name.text = data_item.name
        }
        else{
            holder.profile_name.text = list_of_contact.get(position).number
        }

        if(data_item.last_message.isNotEmpty() && data_item.last_message.isNotBlank()){
            holder.last_chat.text = data_item.last_message
            holder.last_chat.visibility = View.VISIBLE
        }
        else{
            holder.last_chat.text = data_item.last_message
            holder.last_chat.visibility = View.GONE
        }

        if(not_seens.containsKey("${data_item.u_id_no}")){
            if(Integer.parseInt(not_seens.get("${data_item.u_id_no}")!!)>0){
                holder.seen.text = "${not_seens.get("${data_item.u_id_no}")}"
                holder.seen.visibility = View.VISIBLE
            }
            else{
                holder.seen.text = "${not_seens.get("${data_item.u_id_no}")}"
                holder.seen.visibility = View.GONE
            }
        }
        else{
            holder.seen.text = "0"
            holder.seen.visibility = View.GONE
        }

        holder.time.text = convertLongToTime(data_item.last_message_time)

//        if(data_item.unseen_message.equals("null")){
//            Log.e("i am here","yes")
//            hotimelder.seen.text = "0"
//            holder.seen.visibility = View.GONE
//        }
//        else{
//            Log.e("i am here","no")
//            holder.seen.text = data_item.unseen_message
//            holder.seen.visibility = View.VISIBLE
//        }

//        if(data_item.unseen_message.isNotEmpty() && data_item.unseen_message.isNotBlank() && !data_item.unseen_message.not("0")){
//            holder.seen.text = data_item.unseen_message
//            holder.seen.visibility = View.VISIBLE
//        }
//        else{
//            holder.seen.text = data_item.unseen_message
//            holder.seen.visibility = View.GONE
//        }
    }

    class ViewHolder(ItemView: View, listner: OnItemClickListener) : RecyclerView.ViewHolder(ItemView) {
        var profile_image: ImageView
        val profile_name: TextView
        val time: TextView
        val last_chat: TextView
        val seen: TextView
        val item: ConstraintLayout

        init {
            profile_image = itemView.findViewById(R.id.profile_image)
            profile_name = itemView.findViewById(R.id.profile_name)
            time = itemView.findViewById(R.id.time)
            last_chat = itemView.findViewById(R.id.last_chat)
            seen = itemView.findViewById(R.id.seen)
            item = itemView.findViewById(R.id.item)

            item.setOnClickListener {
                if(listner != null){
                    listner.sendUserToChatScreen(position)
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return list_of_contact.size
    }


    fun convertLongToTime(chattime : Long): String {
        val date = Date(System.currentTimeMillis())
        val date2 = Date(chattime)
        var format = SimpleDateFormat("yyyy.MM.dd")

        if(format.format(date).equals(format.format(date2))){
            format = SimpleDateFormat("HH:mm")
            return format.format(date2)
        }
        var date3 : String =  format.format(chattime)
        if(date3.split(".")[0].equals(date3.split(".")[0])){
            date3 = "${
                monthString[ Integer.parseInt(date3.split(".")[1])-1 ].substring(0,3)
            } ${date3.split(".")[2]}"
        }
        else{
            date3 = "${
                monthString[ Integer.parseInt(date3.split(".")[1])-1 ].substring(0,3)
            } ${date3.split(".")[2]}, ${date3.split(".")[0]}"
        }

        return date3
    }


}