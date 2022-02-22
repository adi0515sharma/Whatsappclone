package com.sample.whatsappclone.models
//import android.arch.persistence.room.ColumnInfo
//import android.arch.persistence.room.Entity
//import android.arch.persistence.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "Contact")
class Contact{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "u_id")
    var u_id: Int = 0
    @ColumnInfo(name = "u_id_no")  var u_id_no : Int = 0
    @ColumnInfo(name = "name") lateinit var name: String
    @ColumnInfo(name = "phone_number") lateinit var phone_number: String
    @ColumnInfo(name = "icon") lateinit var icon: String
    @ColumnInfo(name = "exist") lateinit var exist: String
}
