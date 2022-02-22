package com.sample.whatsappclone.db.user_profile

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "UserProfile")
class UserProfile{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "u_id_no")
    var u_id_no: Int = 0

    @ColumnInfo(name = "last_message")
    lateinit var last_message: String

    @ColumnInfo(name = "last_message_time")
    var last_message_time: Long = 0

    @ColumnInfo(name = "unseen_message")
    lateinit var unseen_message: String

}