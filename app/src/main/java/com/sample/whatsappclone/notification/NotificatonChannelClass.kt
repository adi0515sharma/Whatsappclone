package com.sample.whatsappclone.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.content.ContextCompat.getSystemService

class NotificatonChannelClass{

    lateinit var context : Context
    companion object{
        public var CHANNEL_ID : String = "channel id 1"
    }
    constructor(context  : Context){
        this.context = context
        initializeChannel()
    }

    fun initializeChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "chat message channel"
            val descriptionText = "this channel is only for alerting user about new chat message by other user"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}