package com.sample.whatsappclone.utils

import android.content.Context
import android.content.Context.*
import android.content.SharedPreferences
import android.preference.PreferenceManager

class UserSharedPreferences constructor(var context: Context){

    companion object{
        val SHARED_PREF = "whatsapp_user_credential"
        val USER_NAME : String = "USER_NAME"
        val PHONE_NUMBER : String = "PHONE_NUMBER"
        val USER_ID : String = "USER_ID"
        val USER_PHOTO_PATH : String = "USER_PHOTO_PATH"
        val USER_ALLREADY_REGISTER : String = "USER_ALLREADY_REGISTER"

    }
    lateinit var sharedPreference: SharedPreferences

    fun setUp(){
        sharedPreference =
            PreferenceManager.getDefaultSharedPreferences(context)
    }


    fun register_user_credential(name : String, id : String, photo : String, number : String){
        var editor = sharedPreference.edit()
        editor.putString(USER_ID,id)
        editor.putString(USER_NAME,name)
        editor.putString(USER_PHOTO_PATH,photo)
        editor.putString(PHONE_NUMBER,number)
        editor.putBoolean(USER_ALLREADY_REGISTER,true)
        editor.commit()

    }

    fun update_user_credential(key : String, value : String){
        val editor: SharedPreferences.Editor = sharedPreference.edit()
        editor.remove(key)
        editor.putString(key, value)
        editor.commit()
    }

    fun delete_user_credential(key : String){
        val editor: SharedPreferences.Editor = sharedPreference.edit()
        editor.remove(key)
        editor.commit()
    }

    fun create_one_more_key_and_value(key : String, value : String){
        val editor: SharedPreferences.Editor = sharedPreference.edit()
        editor.putString(key, value)
        editor.commit()
    }
    fun user_register_or_not(): Boolean{
        return sharedPreference.getBoolean(USER_ALLREADY_REGISTER, false)
    }

}