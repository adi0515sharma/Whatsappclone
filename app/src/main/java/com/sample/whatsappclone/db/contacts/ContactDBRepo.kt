package com.sample.whatsappclone.db.contacts


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sample.whatsappclone.models.Contact
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ContactDBRepo constructor(private val wordDao: ContactDBDao) {

    val allRecords: MutableLiveData<List<Contact>> = MutableLiveData<List<Contact>>()

    fun fetchUpdatedRecords(){
        wordDao.getAllContacts().observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                var c : ArrayList<Contact> = arrayListOf()
                it.forEach {
                    if(it.exist.equals("yes")){
                        c.add(0, it)
                    }
                    else{
                        if(it.phone_number.length==10 || (it.phone_number.length==13 && it.phone_number.startsWith("+91"))) {
                            c.add(it)
                        }
                    }
                }
                allRecords.value = c
            }.doOnError { it -> Log.e("ContactDBRepo1", "${it.message}") }
        .subscribe()
    }

    fun returnFetchUpdatedRecords() : LiveData<List<Contact>>{
        return allRecords
    }

    fun insertAllRecords(records: List<Contact>){
        GlobalScope.launch {
            Log.e("chat inserted", "${records.size}")
            wordDao.insertAllContacts(records).subscribeOn(Schedulers.io())
                .doOnError { it -> Log.e("ContactDBRepo2", "${it.message}") }
                .doOnSuccess {

                    Log.e("id","${it.size}")

            }.subscribe()
        }
    }


    fun deleteAllRecords(): Completable {
        Log.e("dd","delete called")
        return wordDao.deleteAllContacts()
    }

}