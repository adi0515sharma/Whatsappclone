package com.sample.whatsappclone.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sample.whatsappclone.db.chat_table.Chat
import com.sample.whatsappclone.db.chat_table.ChatDao
import com.sample.whatsappclone.db.contacts.ContactDBDao
import com.sample.whatsappclone.db.user_details.UserDetails
import com.sample.whatsappclone.db.user_details.UserDetailsDao
import com.sample.whatsappclone.db.user_profile.UserProfile
import com.sample.whatsappclone.db.user_profile.UserProfleDao
import com.sample.whatsappclone.models.Contact
import com.sample.whatsappclone.utils.DateConverter


@Database(entities = [Contact::class, UserProfile::class, Chat::class, UserDetails::class], version = 1)

abstract class WhatsappDataBase : RoomDatabase() {

    abstract fun getContactDBDao(): ContactDBDao
    abstract fun getUserProfileDBDao(): UserProfleDao
    abstract fun getChatDBDao(): ChatDao
    abstract fun getUserDetailsDBDao(): UserDetailsDao

    companion object {
        private var instance: WhatsappDataBase? = null
        fun getDatabase(context: Context?): WhatsappDataBase? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context as Context,
                    WhatsappDataBase::class.java, "WhatsappDataBase"
                ).build()
            }
            return instance
        }
    }
}
