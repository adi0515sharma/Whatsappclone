package com.sample.whatsappclone.models.MediaResponse

import androidx.room.ColumnInfo
import java.util.Date

class HomePageResponses {
    lateinit var name: String
    lateinit var number: String
    lateinit var photo: String
    var u_id_no: Int = 0
    lateinit var last_message: String
    var last_message_time: Long = 0
    lateinit var unseen_message: String
}