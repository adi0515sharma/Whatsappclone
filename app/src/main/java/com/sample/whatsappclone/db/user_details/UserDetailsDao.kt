package com.sample.whatsappclone.db.user_details

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sample.whatsappclone.models.Contact
import com.sample.whatsappclone.models.MediaResponse.HomePageResponses
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single


@Dao
interface UserDetailsDao {

    @Query("Update UserDetails SET name = :name , number=:number, photo = :photo WHERE u_id_no = :u_id_no")
    fun updateUserDetail(u_id_no : Int, name : String, number : String, photo : String): Completable

    @Insert()
    fun insertUserDetail(users: UserDetails): Maybe<Long>

    @Query("SELECT UserDetails.u_id_no, UserDetails.name, UserDetails.number, UserDetails.photo, UserProfile.unseen_message,UserProfile.last_message, UserProfile.last_message_time  FROM UserDetails LEFT JOIN UserProfile ON UserDetails.u_id_no = UserProfile.u_id_no ORDER BY UserProfile.last_message_time DESC")
    fun selectForHomeScreen(): Flowable<List<HomePageResponses>>


    @Query("SELECT COUNT(*) FROM UserDetails WHERE u_id_no = :u_id_no")
    fun checkUserExists(u_id_no: Int): Single<Long>

    @Query("SELECT u_id_no FROM UserDetails")
    fun fetchUser() : Maybe<List<Int>>

    @Query("Update UserDetails SET  number=:number, photo = :photo WHERE u_id_no = :u_id_no")
    fun updateUserDetail(u_id_no : Int,  number : String, photo : String): Completable
}