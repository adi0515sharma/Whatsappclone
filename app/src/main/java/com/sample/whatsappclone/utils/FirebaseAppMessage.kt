package com.sample.whatsappclone.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sample.whatsappclone.R
import com.sample.whatsappclone.db.WhatsappDataBase
import com.sample.whatsappclone.db.chat_table.Chat
import com.sample.whatsappclone.db.chat_table.ChatDBRepo
import com.sample.whatsappclone.db.user_details.UserDetails
import com.sample.whatsappclone.db.user_details.UserDetailsRepo
import com.sample.whatsappclone.db.user_profile.UserProfile
import com.sample.whatsappclone.db.user_profile.UserProfileRepo
import com.sample.whatsappclone.notification.NotificatonChannelClass
import com.sample.whatsappclone.ui.ChatScreen.ChatRoom
import java.util.*


class FirebaseAppMessage : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        // if use not present in USER_DETAILS table then add it or else update records
        // delete previous user of that id from USER_PROFILE table then add it again in bottom (Default)
        // add messsage, time, date in USER_PROFILE table
        // add message, time, date and other details in CHAT table
        // send notification


        // task 1 started
        var database_conn : WhatsappDataBase = WhatsappDataBase.getDatabase(this)!!

        var original_message : String? = p0.data.get("body")
        var from_id : String? = p0.data.get("from_id")
        var from_name : String? = p0.data.get("from_name")
        var from_number : String? = p0.data.get("from_number")
        var from_photo : String? = p0.data.get("from_photo")


        var chat_time : String? = p0.data.get("time")



        var user_detail_repo : UserDetailsRepo = UserDetailsRepo(database_conn.getUserDetailsDBDao())
        val u_d : UserDetails = UserDetails()
        u_d.u_id_no = Integer.parseInt(from_id)
        u_d.name = from_name as String
        u_d.photo = from_photo as String
        u_d.number = from_number as String

        user_detail_repo.checkUserExists(Integer.parseInt(from_id), u_d)
        // task 1 completed



        // task 2 staterd

        var user_profile_repo : UserProfileRepo = UserProfileRepo(database_conn.getUserProfileDBDao())
        var user_profile : UserProfile = UserProfile()
        user_profile.u_id_no = Integer.parseInt(from_id)
        user_profile.last_message = original_message as String
        user_profile.unseen_message = "0"


//        var time2 : String = "${Calendar.getInstance().time.hours}:${Calendar.getInstance().time.minutes}:${Calendar.getInstance().time.seconds}"
//        var date2 : String = "${Calendar.getInstance().time.day}:${Calendar.getInstance().time.month}:${Calendar.getInstance().time.year}"
//


        user_profile.last_message_time = chat_time!!.toLong()

        user_profile_repo.deleteUserProfile(Integer.parseInt(from_id), user_profile)


        Log.e("task no : ","3")
        // task 2 completed

        var chat_table : ChatDBRepo = ChatDBRepo(database_conn.getChatDBDao())
        var share : UserSharedPreferences = UserSharedPreferences(this)
        share.setUp()

        var to_name : String? = share.sharedPreference.getString(UserSharedPreferences.USER_NAME,"You")
        var to_u_id_no : String? = share.sharedPreference.getString(UserSharedPreferences.USER_ID,"0")
        var to_u_mobile : String? = share.sharedPreference.getString(UserSharedPreferences.PHONE_NUMBER,"+000000000000")

        var chat : Chat = Chat()
        chat.message = original_message
        chat.from_u_id_no = Integer.parseInt(from_id)
        chat.from_u_mobile = from_number
        chat.from_u_name = from_name
        chat.message_time = chat_time!!.toLong()
        chat.message_seen = "no"
        chat.to_u_id_no = Integer.parseInt(to_u_id_no);
        chat.to_u_mobile = to_u_mobile as String;
        chat.to_u_name = to_name as String;
        chat_table.insertChat(chat)

        send_notification(from_photo , "From ${from_name}", original_message)


    }

    fun send_notification( url : String, title : String, text : String){

        Log.e("url", url)
//        val intent = Intent(this, ChatRoom::class.java)
//        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(this)
//        stackBuilder.addNextIntent(intent)
//        val pendingIntent: PendingIntent =
//            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(this, NotificatonChannelClass.CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)

            .setContentText(text)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val futureTarget = Glide.with(this)
            .asBitmap()
            .load(url)
            .submit()

        val bitmap = futureTarget.get()
        notificationBuilder.setLargeIcon(bitmap)



        notificationManager.notify(0, notificationBuilder.build())
    }
}