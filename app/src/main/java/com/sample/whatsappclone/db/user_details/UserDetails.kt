package com.sample.whatsappclone.db.user_details

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "UserDetails")
class UserDetails {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "u_id")
    var u_id: Int = 0

    @ColumnInfo(name = "u_id_no")  var u_id_no: Int = 0
    @ColumnInfo(name = "name") lateinit var name: String
    @ColumnInfo(name = "number") lateinit var number: String
    @ColumnInfo(name = "photo") lateinit var photo: String
}