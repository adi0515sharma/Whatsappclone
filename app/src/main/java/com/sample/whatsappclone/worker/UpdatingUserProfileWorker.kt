package com.sample.whatsappclone.worker

import android.content.Context
import android.util.Log
import androidx.annotation.IntegerRes
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.sample.whatsappclone.api.Fetching_api
import com.sample.whatsappclone.db.WhatsappDataBase
import com.sample.whatsappclone.db.user_details.UserDetailsDao
import com.sample.whatsappclone.db.user_profile.UserProfleDao
import com.sample.whatsappclone.models.Contact
import com.sample.whatsappclone.models.Normal.user_detail_uptd
import com.sample.whatsappclone.utils.RetrofitObjectInstance
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import retrofit2.Retrofit

class UpdatingUserProfileWorker constructor(var context : Context, params: WorkerParameters) : Worker(context,params) {

    val database : WhatsappDataBase = WhatsappDataBase.getDatabase(this.context) as WhatsappDataBase
    val db_to_work : UserDetailsDao = database.getUserDetailsDBDao()
    override fun doWork(): Result {

        Log.e("UpdatingUserProfile", "start1")

        fetchUserProfile()

        return Result.success()
    }

    fun fetchUserProfile() {
        db_to_work
            .fetchUser()
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                if(it.size>0){
                    var id : Array<Int> = arrayOfNulls<Int>(it.size) as Array<Int>
                    for(i in 0..(it.size-1)){
                        id.set(i, it.get(i))
                    }
                    fetchFromServer(id)
                }
                Log.e("UpdatingUserProfile", "start2")

            }.subscribe()
    }

    fun fetchFromServer(it:Array<Int>){
        val retrofilt : Retrofit = RetrofitObjectInstance.getInstance()
        val fetch_api : Fetching_api = retrofilt.create(Fetching_api::class.java)
        fetch_api.fetch_user_profile_status(it)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                updateDb(it)
                Log.e("UpdatingUserProfile", "start2")

            }
            .subscribe()
    }

    fun updateDb(it: ArrayList<user_detail_uptd>){
        it.forEach {
            db_to_work.updateUserDetail(Integer.parseInt(it.u_id_no), it.u_mobile_no, it.u_photo)
                .subscribeOn(Schedulers.io())
                .doOnComplete {
                    Log.e("UpdatingUserProfile", "start3")
                }
                .subscribe()
        }
    }



}