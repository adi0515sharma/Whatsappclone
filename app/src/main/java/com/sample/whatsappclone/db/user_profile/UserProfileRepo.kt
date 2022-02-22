package com.sample.whatsappclone.db.user_profile

import android.util.Log
import androidx.room.Dao
import com.sample.whatsappclone.db.user_details.UserDetails
import com.sample.whatsappclone.db.user_details.UserDetailsDao
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class UserProfileRepo constructor(private val userProfile: UserProfleDao){
    fun updateSeen(u_id_no : Int, seen : String){

            Log.e("request to update seen ", "${u_id_no}")
            userProfile.updateUserSeen(u_id_no, seen).subscribeOn(Schedulers.io())
                .doOnError { it -> Log.e("UserProfileRepo1", "${it.message}") }
                .doOnComplete{
                    Log.e("Updated","yes")
                }.subscribe()

    }

    fun insertUserProfile(records: UserProfile){
        Log.e("insertUserProfile", "${records.last_message}")
        userProfile.insertUser(records).subscribeOn(Schedulers.io())
            .doOnError { it -> Log.e("UserProfileRepo2", "${it.message}") }
            .doOnSuccess {
                Log.e("saved user profile ","${it}")
            }.subscribe()
    }

    fun updateUserProfile(records: UserProfile){
        Log.e("insertUserProfile", "${records.last_message}")
        userProfile.updateUser(records.u_id_no, records.last_message, records.last_message_time).subscribeOn(Schedulers.io())
            .doOnError { it -> Log.e("UserProfileRepo2", "${it.message}") }
            .doOnComplete {
                Log.e("update user profile ","yes")
            }.subscribe()
    }


    fun deleteUserProfile(u_id_no: Int, records: UserProfile){
        Log.e("deleteUserDeatils", "${u_id_no}")
        userProfile.checkUser(u_id_no).subscribeOn(Schedulers.io())
            .doOnSuccess {
                if(it>0) {
                    updateUserProfile(records)
                }
                else {
                    insertUserProfile(records)
                }
            }
            .subscribe()
    }

}