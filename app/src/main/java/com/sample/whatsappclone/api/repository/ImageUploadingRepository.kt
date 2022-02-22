package com.sample.whatsappclone.api.repository

import android.util.Log
import com.sample.whatsappclone.api.Auth
import com.sample.whatsappclone.models.AuthResp.RegistrationResponse
import com.sample.whatsappclone.models.MediaResponse.ProfilePhotoUpload
import com.sample.whatsappclone.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Dispatcher
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

class ImageUploadingRepository (private val auth : Auth) {


    fun uploadImage(fileToUpload : MultipartBody.Part, filename: RequestBody)=
        flow<Resource<ProfilePhotoUpload>> {
            emit(Resource.loading())
            val upload = auth.uploadFile(fileToUpload, filename)
            emit(Resource.success(upload))
        }
        .catch {
            emit(Resource.failed(it.message!!))
        }.flowOn(Dispatchers.IO)


    fun uploadUser(name : String, phone : String, path : String, fcm_token : String)=
        flow<Resource<RegistrationResponse>> {
            emit(Resource.loading())
            val upload = auth.getRegistered(phone, path, name, fcm_token)
            emit(Resource.success(upload))
        }
            .catch {
                emit(Resource.failed(it.message!!))
            }.flowOn(Dispatchers.IO)
}