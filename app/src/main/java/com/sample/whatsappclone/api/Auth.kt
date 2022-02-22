package com.sample.whatsappclone.api

import com.sample.whatsappclone.models.AuthResp.RegistrationResponse
import com.sample.whatsappclone.models.MediaResponse.ProfilePhotoUpload
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface Auth {


    @GET("reg_user.php")
    suspend fun getRegistered(
        @Query("u_mobile_no") phone: String,
        @Query("u_photo") image: String,
        @Query("u_name") name: String,
        @Query("fcm_token") fcm_token: String
        ) : RegistrationResponse

    @Multipart
    @POST("uploading_profile_image.php")
    suspend fun uploadFile(@Part file: MultipartBody.Part?, @Part("file") name: RequestBody?):
          ProfilePhotoUpload

}