package com.sample.whatsappclone.models.AuthResp

data class UploadingUserResponse constructor(
    val phone : String,
    val name : String,
    val path : String,
    val id : String
)
