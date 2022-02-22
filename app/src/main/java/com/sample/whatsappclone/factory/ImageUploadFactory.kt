package com.sample.whatsappclone.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sample.whatsappclone.ui.Authentications.UserProfileViewModel
import com.sample.whatsappclone.api.repository.ImageUploadingRepository

class ImageUploadFactory(private val iur : ImageUploadingRepository)
    :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserProfileViewModel::class.java)) {
            return UserProfileViewModel(iur) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}