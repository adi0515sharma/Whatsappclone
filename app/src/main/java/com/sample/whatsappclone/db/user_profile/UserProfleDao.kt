package com.sample.whatsappclone.db.user_profile

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.sample.whatsappclone.db.user_details.UserDetails
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import java.util.Date


@Dao
interface UserProfleDao {

    @Query("SELECT COUNT(*) FROM UserProfile WHERE u_id_no = :u_id_no")
    fun checkUser(u_id_no : Int) : Maybe<Long>


    @Query("UPDATE UserProfile SET last_message = :last_message , last_message_time = :last_message_time  WHERE u_id_no = :u_id_no")
    fun updateUser(u_id_no : Int, last_message : String, last_message_time : Long ) : Completable


    @Insert
    fun insertUser(userDetails : UserProfile) : Maybe<Long>



    @Query("UPDATE UserProfile SET unseen_message =:seen WHERE u_id_no = :u_id_no")
    fun updateUserSeen(u_id_no : Int, seen : String) : Completable


}