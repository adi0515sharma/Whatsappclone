package com.sample.whatsappclone.models.AuthResp

import com.sample.whatsappclone.models.Normal.UserInfo

data class RegistrationResponse constructor(
    val status : String,
    val info : UserInfo,
)
