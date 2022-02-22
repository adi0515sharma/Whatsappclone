package com.sample.whatsappclone.ui.AddingMembers


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sample.whatsappclone.R
import com.sample.whatsappclone.models.Contact
import com.sample.whatsappclone.models.MediaResponse.HomePageResponses
import java.util.*
import kotlin.collections.ArrayList

class MainActivityAdapter constructor(var list_of_contact : ArrayList<Contact> = arrayListOf(), var context : Context) : RecyclerView.Adapter<MainActivityAdapter.ViewHolder>() {

    lateinit var listener: OnItemClickListener
    var copy_list_of_contact : ArrayList<Contact> = arrayListOf()


    interface OnItemClickListener {
        fun sendUserToChatScreen(position : Int)
        fun shareAppToOtherUser(position : Int)
    }


    fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString()?.toLowerCase() ?: ""
                if (charString.isEmpty()) list_of_contact = copy_list_of_contact else {
                    val filteredList = ArrayList<Contact>()
                    copy_list_of_contact
                        .filter {
                            (it.phone_number.contains(constraint!!)) or
                                    (it.name.toLowerCase().contains(constraint))

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
                    results.values as ArrayList<Contact>
                notifyDataSetChanged()
            }
        }
    }

    fun setListner(listener: OnItemClickListener){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_item, parent, false)
        return ViewHolder(view, this.listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.profile_name.setText(list_of_contact.get(position).name)
        var requestOptions:RequestOptions = RequestOptions()
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)


        if(list_of_contact.get(position).icon.isNotEmpty() && list_of_contact.get(position).icon.isNotBlank()){
            val generator: ColorGenerator = ColorGenerator.MATERIAL // or use DEFAULT
            val firstLetter : String = "${list_of_contact.get(position).name[0]}"
            val color: Int = generator.getColor(list_of_contact.get(position).name)
            val drawable: TextDrawable = TextDrawable.builder().buildRound(firstLetter, color)

            Glide.with(context)
                 .load(list_of_contact.get(position).icon)
                 .placeholder(drawable)
                 .into(holder.profile_image)
        }

        if(list_of_contact.get(position).exist.equals("yes")){
            holder.profile_send.visibility = View.VISIBLE
            holder.profile_share.visibility = View.GONE
        }
        else{
            holder.profile_send.visibility = View.GONE
            holder.profile_share.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return list_of_contact.size
    }

    class ViewHolder constructor(ItemView: View, listner : OnItemClickListener) : RecyclerView.ViewHolder(ItemView) {
        var profile_image: ImageView
        val profile_name: TextView
        val profile_send: ImageView
        val profile_share: ImageView

        init {
            profile_image = itemView.findViewById(R.id.profile_image)
            profile_name = itemView.findViewById(R.id.profile_name)
            profile_send = itemView.findViewById(R.id.profile_send)
            profile_send.setOnClickListener {
                listner.sendUserToChatScreen(position)
            }
            profile_share = itemView.findViewById(R.id.profile_share)
            profile_share.setOnClickListener {
                listner.shareAppToOtherUser(position)
            }

        }
    }

}