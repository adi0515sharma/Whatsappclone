package com.sample.whatsappclone.api

import com.sample.whatsappclone.models.Normal.User_status
import com.sample.whatsappclone.models.Normal.user_detail_uptd
import io.reactivex.Flowable
import io.reactivex.Maybe
import retrofit2.http.*

interface Fetching_api {


    @FormUrlEncoded
    @POST("fetching_user_status.php")
    fun fetch_user_status(
        @Field("phone_numbers[]")
        phone_numbers : Array<String>
    ): Flowable<ArrayList<User_status>>

    @FormUrlEncoded
    @POST("fetchUpdatedProfile.php")
    fun fetch_user_profile_status(
        @Field("u_id_no[]")
        phone_numbers : Array<Int>
    ): Maybe<ArrayList<user_detail_uptd>>
}