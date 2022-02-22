package com.sample.whatsappclone.db.chat_table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "Chat")
class Chat{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "from_u_id_no")
    var from_u_id_no: Int = 0

    @ColumnInfo(name = "to_u_id_no")
    var to_u_id_no: Int = 0

    @ColumnInfo(name = "from_u_name")
    lateinit var from_u_name: String

    @ColumnInfo(name = "to_u_name")
    lateinit var to_u_name: String

    @ColumnInfo(name = "from_u_mobile")
    lateinit var from_u_mobile: String

    @ColumnInfo(name = "to_u_mobile")
    lateinit var to_u_mobile: String

    @ColumnInfo(name = "message")
    lateinit var message: String

    @ColumnInfo(name = "message_time")
    var message_time: Long = 0

    @ColumnInfo(name = "message_seen")
    lateinit var message_seen: String
}