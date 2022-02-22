package com.sample.whatsappclone.db.user_details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sample.whatsappclone.db.WhatsappDataBase
import com.sample.whatsappclone.db.chat_table.Chat
import com.sample.whatsappclone.db.chat_table.ChatDao
import com.sample.whatsappclone.db.user_profile.UserProfileRepo
import com.sample.whatsappclone.models.MediaResponse.HomePageResponses
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class UserDetailsRepo constructor(private val userDetails: UserDetailsDao){

    val responseForHomeUi : MutableLiveData<List<HomePageResponses>> = MutableLiveData<List<HomePageResponses>>()
    fun updateUserDetails(u_id_no : Int, name : String, number : String, photo : String){

            Log.e("request to update ", "${u_id_no}")
            userDetails.updateUserDetail(u_id_no, name , number , photo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { it -> Log.e("UserDetailsRep1", "${it.message}") }
                .doOnComplete {
                    Log.e("user updated success ","yes")
                }.subscribe()

    }

    fun insertUserDeatils(records: UserDetails){

        Log.e("chat message", "${records.name}")
        userDetails
            .insertUserDetail(records)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { it -> Log.e("UserDetailsRepo2", "${it.message}") }
                .doOnSuccess {
                    Log.e("user saved id ","${it}")
                }.subscribe()

    }

    fun checkUserExists(u_id_no: Int, user_detail : UserDetails){

        userDetails.checkUserExists(u_id_no)
            .subscribeOn(Schedulers.io())

            .doOnError { it -> Log.e("UserDetailsRepo2", "${it.message}") }
            .doOnSuccess {
                if(it>0){
                    // user exists
                    userDetails.updateUserDetail(user_detail.u_id_no, user_detail.name, user_detail.number, user_detail.photo)
                        .doOnComplete {
                        }.subscribe()
                }
                else{
                    insertUserDeatils(user_detail)
                }
            }
            .subscribe()
    }


    fun fetch_responseForHomeUi() : LiveData<List<HomePageResponses>> {
        return responseForHomeUi
    }
    fun fetchForHomeScreenUi()
    {
        userDetails.selectForHomeScreen()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { it -> Log.e("UserDetailsRepo2", "${it.message}") }
            .doOnNext {
                responseForHomeUi.value = it
            }
            .subscribe()
    }


}