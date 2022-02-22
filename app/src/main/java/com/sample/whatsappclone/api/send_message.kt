package com.sample.whatsappclone.api

import io.reactivex.Completable
import retrofit2.http.*

interface send_message {

    @FormUrlEncoded
    @POST("send_message_single.php")
    fun send_message_single(
        @Field("from_id") from_id : Int,
        @Field("to_id") to_id : Int,
        @Field("message") message : String,
        @Field("time") time : String,
        ): Completable
}