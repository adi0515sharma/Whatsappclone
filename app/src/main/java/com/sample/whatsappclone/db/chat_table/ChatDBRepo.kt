package com.sample.whatsappclone.db.chat_table

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sample.whatsappclone.db.contacts.ContactDBDao
import com.sample.whatsappclone.models.Contact
import com.sample.whatsappclone.models.MediaResponse.FetchingCountOfNotSeenMessageToHomeScreen
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChatDBRepo constructor(private val chatDao: ChatDao) {

    val allChats: MutableLiveData<List<Chat>> = MutableLiveData<List<Chat>>()
    var liveSeen : MutableLiveData<HashMap<String, String>> = MutableLiveData<HashMap<String, String>>()
    fun fetchUpdatedChats(other_id: Int){
        chatDao.fetchAllChatOfUser(other_id).observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                allChats.value = it
            }.doOnError { it -> Log.e("ChatDBRepo1", "${it.message}") }
            .subscribe()
    }

    fun returnFetchUpdatedRecords() : LiveData<List<Chat>> {
        return allChats
    }


    fun insertChat(records: Chat){

        chatDao.insertChat(records)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnError { it -> Log.e("ChatDBRepo2", "${it.message}") }
            .doOnSuccess {
                Log.e("chat saved id ","${it}")
            }
            .subscribe()

    }

    fun liveSeenCount() : LiveData<HashMap<String, String>>{
        return liveSeen
    }
    fun fetchNotSeenCount(){

        chatDao.fetchTotalNotSeenMessageCount()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnError { it -> Log.e("ChatDBRepo3", "${it.message}") }
            .doOnNext {
                var data : HashMap<String, String> = HashMap<String, String>()
                it.forEach {
                    data.put("${it.from_u_id_no}", "${it.total_not_seen}")
                }
                liveSeen.value = data
            }.subscribe()

    }

    fun updateSeen(from_u_id_no : Int){
        chatDao.setSeenToMessage(from_u_id_no)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnError { it -> Log.e("ChatDBRepo4", "${it.message}") }
            .subscribe()
    }
    fun fetchNotSeenForSpecificCount(from_u_id_no : Int){

        chatDao.fetchTotalNotSeenMessageForSpecCount(from_u_id_no)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnError { it -> Log.e("ChatDBRepo5", "${it.message}") }
            .doOnNext {
                Log.e("chat saved id ","")
            }.subscribe()

    }
}