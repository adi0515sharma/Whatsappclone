package com.sample.whatsappclone.ui.Authentications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.whatsappclone.api.repository.ImageUploadingRepository
import com.sample.whatsappclone.models.AuthResp.RegistrationResponse
import com.sample.whatsappclone.models.MediaResponse.ProfilePhotoUpload
import com.sample.whatsappclone.utils.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserProfileViewModel(private val image_uploading_repo: ImageUploadingRepository) : ViewModel(){

    private val _image : MutableLiveData<Resource<ProfilePhotoUpload>> = MutableLiveData()
    val image : LiveData<Resource<ProfilePhotoUpload>>
        get() = _image

    private val _user_upload : MutableLiveData<Resource<RegistrationResponse>> = MutableLiveData()
    val user_upload : LiveData<Resource<RegistrationResponse>>
        get() = _user_upload

    fun uploadUserProfileImage(fileToUpload : MultipartBody.Part,  filename: RequestBody)
    = viewModelScope.async{
            image_uploading_repo.uploadImage(fileToUpload, filename).collect {
                _image.value = it
            }
        }

    fun uploadUser(name : String, phone : String, path : String, fcm_token : String)
            = viewModelScope.async {
        image_uploading_repo.uploadUser(name, phone, path, fcm_token).collect {
            _user_upload.value = it
        }
    }


}